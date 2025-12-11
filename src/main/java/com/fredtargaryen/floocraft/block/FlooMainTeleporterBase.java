package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.client.gui.screens.teleport.TeleportScreen;
import com.fredtargaryen.floocraft.config.CommonConfig;
import com.fredtargaryen.floocraft.network.FloocraftSavedData;
import com.fredtargaryen.floocraft.network.messages.FireplaceListResponseMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.fredtargaryen.floocraft.block.FlooFlamesBlock.BEHAVIOUR;
import static com.fredtargaryen.floocraft.block.FlooFlamesBlock.TEMP;

public abstract class FlooMainTeleporterBase extends Block {
    /**
     * How many teleports the block has left.
     */
    public static IntegerProperty TPS_REMAINING;
    public static final int INFINITE_TPS = 0;

    /**
     * The fire colour
     */
    public static BooleanProperty COLOUR;
    public static final boolean SOUL = true;
    public static final boolean STANDARD = false;

    static {
        // We are saving properties by just making aliases for existing properties which use the values we want...
        // so careful to use the below property names in the resource files!
        TPS_REMAINING = BlockStateProperties.LEVEL_COMPOSTER; // "level"
        COLOUR = BlockStateProperties.OMINOUS; // "ominous"
    }

    public FlooMainTeleporterBase(BlockBehaviour.Properties properties) {
        super(properties.noCollission());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TPS_REMAINING, COLOUR);
    }

    public boolean canDepartFromBlock(BlockState state) {
        return true;
    }

    @Override
    public void entityInside(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Entity entity, InsideBlockEffectApplier effectApplier) {
        if (level.isClientSide) {
            if (entity == Minecraft.getInstance().player) {
                openTeleportGui(pos);
            }
        } else {
            //Server side. Players are dealt with on the client because of the GUI
            if (!(entity instanceof Player)) {
                boolean teleport = false;
                //Set teleport destination one block outside the fire, instead of in the fire
                boolean landOutside = false;

                if (CommonConfig.ITEMS_TELEPORT && entity instanceof ItemEntity) {
                    teleport = true;
                    landOutside = true;
                } else if ((CommonConfig.VILLAGERS_TELEPORT && entity instanceof Villager)
                        || (CommonConfig.MISC_MOBS_TELEPORT && entity instanceof LivingEntity)) {
                    teleport = level.random.nextFloat() < 0.2;
                }
                if (teleport) {
                    //Get list of locations and whether they are available
                    FloocraftSavedData levelData = FloocraftSavedData.getForLevel((ServerLevel) level);
                    FireplaceListResponseMessage flrm = levelData.assembleNewFireplaceList(level, pos);
                    List<String> places = flrm.places();
                    List<Boolean> enabledList = flrm.enabledList();
                    ArrayList<String> possibleLocations = new ArrayList<>();
                    //Add the enabled locations to possibleLocations
                    for (int i = 0; i < places.size(); ++i) {
                        if (enabledList.get(i)) possibleLocations.add(places.get(i));
                    }
                    if (!possibleLocations.isEmpty()) {
                        //Pick a random location from possibleLocations
                        int destNo = level.random.nextInt(possibleLocations.size());
                        //Teleport to that location
                        String destName = possibleLocations.get(destNo);
                        //Get location coords
                        BlockPos coords = levelData.placeList.get(destName);
                        BlockPos dest = new BlockPos(coords.getX(), coords.getY(), coords.getZ());
                        //Set a temporary Floo fire here
                        if (level.getBlockState(dest).is(BlockTags.FIRE)) {
                            boolean fireColour = SoulFireBlock.canSurviveOnBlock(level.getBlockState(dest.below())) ? SOUL : STANDARD;
                            level.setBlockAndUpdate(dest, FloocraftBlocks.FLOO_FLAMES.get().defaultBlockState()
                                    .setValue(COLOUR, fireColour)
                                    .setValue(BEHAVIOUR, TEMP));
                        }
                        if (landOutside) {
                            dest = dest.relative(FloocraftBlocks.FLOO_FLAMES.get().isInFireplace(level, dest));
                            entity.moveOrInterpolateTo(new Vec3(dest.getX(), coords.getY(), dest.getZ()), entity.getYRot(), entity.getXRot());
                        } else {
                            entity.moveOrInterpolateTo(new Vec3(coords.getX(), coords.getY(), coords.getZ()), entity.getYRot(), entity.getXRot());
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void openTeleportGui(BlockPos pos) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.screen == null && !FloocraftBase.ClientModEvents.flashTicker.isRunning()) {
            instance.setScreen(new TeleportScreen(instance.screen, pos));
            FloocraftBase.ClientModEvents.flashTicker.start();
        }
    }

    //FOR ALLOWING COLLISIONS TO HAPPEN
    @Override
    @Nonnull
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return Shapes.empty();
    }
}