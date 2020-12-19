package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.TeleportScreen;
import com.fredtargaryen.floocraft.config.CommonConfig;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static net.minecraft.state.properties.BlockStateProperties.AGE_0_15;

public abstract class FlooMainTeleporterBase extends Block {

    public FlooMainTeleporterBase(AbstractBlock.Properties properties)
    {
        super(properties.notSolid());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AGE_0_15);
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isRemote) {
            if (entityIn == Minecraft.getInstance().player) {
                doClientGuiTings(pos);
            }
        }
        else {
            //Server side. Players are dealt with on the client because of the GUI
            if(!(entityIn instanceof PlayerEntity)) {
                boolean teleport = false;
                //Set teleport destination one block outside the fire, instead of in the fire
                boolean landOutside = false;

                if(CommonConfig.ITEMS_TELEPORT.get() && entityIn instanceof ItemEntity) {
                    teleport = true;
                    landOutside = true;
                }
                else if(    (CommonConfig.VILLAGERS_TELEPORT.get() && entityIn instanceof VillagerEntity)
                          ||(CommonConfig.MISC_MOBS_TELEPORT.get() && entityIn instanceof LivingEntity)) {
                    teleport = worldIn.rand.nextFloat() < 0.2;
                }
                if(teleport) {
                    //Get list of locations and whether they are available
                    FloocraftWorldData fwd = FloocraftWorldData.forWorld(worldIn);
                    MessageFireplaceList mfl = fwd.assembleNewFireplaceList(worldIn);
                    ArrayList<String> possibleLocations = new ArrayList<>();
                    //Add the enabled locations to possibleLocations
                    for (int i = 0; i < mfl.places.length; ++i) {
                        if (mfl.enabledList[i]) possibleLocations.add((String) mfl.places[i]);
                    }
                    if(!possibleLocations.isEmpty()) {
                        //Pick a random location from possibleLocations
                        int destNo = worldIn.rand.nextInt(possibleLocations.size());
                        //Teleport to that location
                        String destName = possibleLocations.get(destNo);
                        //Get location coords
                        int[] coords = fwd.placeList.get(destName);
                        BlockPos dest = new BlockPos(coords[0], coords[1], coords[2]);
                        //Set a temporary Floo fire here
                        Block blockOnTop = worldIn.getBlockState(dest).getBlock();
                        if(blockOnTop.isIn(BlockTags.FIRE))
                        {
                            if(SoulFireBlock.shouldLightSoulFire(worldIn.getBlockState(dest.down()).getBlock()))
                            {
                                worldIn.setBlockState(dest, FloocraftBase.MAGENTA_FLAMES_TEMP.get().getDefaultState());
                            }
                            else
                            {
                                worldIn.setBlockState(dest, FloocraftBase.GREEN_FLAMES_TEMP.get().getDefaultState());
                            }
                        }
                        if (landOutside) {
                            dest = dest.offset(((FlooFlamesBase) FloocraftBase.GREEN_FLAMES_TEMP.get()).isInFireplace(worldIn, dest));
                            entityIn.setLocationAndAngles(
                                    dest.getX(), coords[1], dest.getZ(), entityIn.rotationYaw, entityIn.rotationPitch);
                        } else {
                            entityIn.setLocationAndAngles(
                                    coords[0], coords[1], coords[2], entityIn.rotationYaw, entityIn.rotationPitch);
                        }
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientGuiTings(BlockPos pos) {
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
        if (Minecraft.getInstance().currentScreen == null && !proxy.overrideTicker.isOverriding()) {
            Minecraft.getInstance().displayGuiScreen(new TeleportScreen(pos.getX(), pos.getY(), pos.getZ()));
            proxy.overrideTicker.start();
        }
    }

    //FOR ALLOWING COLLISIONS TO HAPPEN
    @Override
    @Nonnull
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }
}