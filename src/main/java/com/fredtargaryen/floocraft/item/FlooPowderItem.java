package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftSounds;
import com.fredtargaryen.floocraft.block.FlooFlamesBlock;
import com.fredtargaryen.floocraft.config.CommonConfig;
import com.fredtargaryen.floocraft.entity.DroppedFlooPowderEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

import static com.fredtargaryen.floocraft.block.FlooFlamesBlock.BEHAVIOUR;
import static com.fredtargaryen.floocraft.block.FlooFlamesBlock.BUSY;
import static com.fredtargaryen.floocraft.block.FlooMainTeleporterBase.*;

/**
 * All Floo Powder items are generated from this class
 */
public class FlooPowderItem extends Item {
    /**
     * The number of teleports that can be done using the particular Floo Powder item.
     */
    private final byte concentration;

    public byte getConcentration() {
        return this.concentration;
    }

    public FlooPowderItem(byte conc) {
        super(new Item.Properties()
                .stacksTo(64)
                .fireResistant());
        this.concentration = conc;
    }

    @Override
    public @Nonnull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            Block b = state.getBlock();
            if (state.is(BlockTags.FIRE)) {
                FlooFlamesBlock flooFlames = FloocraftBlocks.FLOO_FLAMES.get();
                if (flooFlames.isInFireplace(level, pos) != null) {
                    level.setBlockAndUpdate(pos, flooFlames.defaultBlockState()
                            .setValue(TPS_REMAINING, (int) this.concentration)
                            .setValue(COLOUR, SoulFireBlock.canSurviveOnBlock(level.getBlockState(pos.below())))
                            .setValue(BEHAVIOUR, BUSY));
                    level.playSound(null, pos, FloocraftSounds.GREENED.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    context.getItemInHand().shrink(1);
                    return InteractionResult.sidedSuccess(false);
                }
                return InteractionResult.SUCCESS;
            } else if (b == Blocks.CAMPFIRE && state.getValue(BlockStateProperties.LIT)) {
                level.setBlockAndUpdate(pos, FloocraftBlocks.FLOO_CAMPFIRE.get().defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING))
                        .setValue(COLOUR, STANDARD)
                        .setValue(TPS_REMAINING, (int) this.concentration));
                level.playSound(null, pos, FloocraftSounds.GREENED.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                context.getItemInHand().shrink(1);
                return InteractionResult.sidedSuccess(false);
            } else if (b == Blocks.SOUL_CAMPFIRE && state.getValue(BlockStateProperties.LIT)) {
                level.setBlockAndUpdate(pos, FloocraftBlocks.FLOO_SOUL_CAMPFIRE.get().defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING))
                        .setValue(COLOUR, SOUL)
                        .setValue(TPS_REMAINING, (int) this.concentration));
                level.playSound(null, pos, FloocraftSounds.GREENED.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                context.getItemInHand().shrink(1);
                return InteractionResult.sidedSuccess(false);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    /**
     * Creates a DroppedFlooPowderEntity instead of the default ItemEntity.
     *
     * @param level    The world object
     * @param location The ItemEntity object, useful for getting the position of the entity
     * @param stack    The current item stack
     * @return A new Entity object to spawn or null
     */
    @Override
    public Entity createEntity(Level level, Entity location, ItemStack stack) {
        if (!level.isClientSide) {
            DroppedFlooPowderEntity flp = new DroppedFlooPowderEntity(level, location.getX(), location.getEyeY() - 0.3, location.getZ(), stack, this.concentration);
            flp.setPickUpDelay(40);
            flp.setDeltaMovement(location.getDeltaMovement());
            return flp;
        }
        return null;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, components, flag);
        if (this.concentration == INFINITE_TPS) {
            components.add(Component.translatable("item.floocraftft.concentration", "∞").withStyle(ChatFormatting.GREEN));
            components.add(Component.translatable(("item.floocraftft.creative_only")));
        } else {
            if (CommonConfig.DEPLETE_FLOO) {
                components.add(Component.translatable("item.floocraftft.concentration", this.concentration).withStyle(ChatFormatting.GREEN));
            } else {
                components.add(Component.translatable("item.floocraftft.concentration", "∞").withStyle(ChatFormatting.GREEN));
            }
            if (this.concentration == 1) {
                components.add(Component.translatable(("item.floocraftft.craftable")));
            }
        }
    }
}