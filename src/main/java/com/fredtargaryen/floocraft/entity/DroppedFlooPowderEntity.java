package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftSounds;
import com.fredtargaryen.floocraft.block.FlooFlames;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static com.fredtargaryen.floocraft.block.FlooCampfireBlock.FACING;
import static com.fredtargaryen.floocraft.block.FlooFlames.BEHAVIOUR;
import static com.fredtargaryen.floocraft.block.FlooFlames.BUSY;
import static com.fredtargaryen.floocraft.block.FlooMainTeleporterBase.*;
import static net.minecraft.world.level.block.CampfireBlock.WATERLOGGED;

public class DroppedFlooPowderEntity extends ItemEntity {
    private byte concentration;

    public DroppedFlooPowderEntity(Level level, double x, double y, double z, ItemStack stack, byte conc) {
        super(level, x, y, z, stack);
        this.concentration = conc;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Concentration", this.concentration);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.concentration = tag.getByte("Concentration");
    }

    @Override
    public void tick() {
        BlockPos location = this.blockPosition();
        BlockState bs = this.level().getBlockState(location);
        if (bs.is(BlockTags.FIRE)) {
            FlooFlames flames = ((FlooFlames) FloocraftBlocks.FLOO_FLAMES.get());
            if (flames.isInFireplace(this.level(), location) != null) {
                this.level().setBlockAndUpdate(location, flames.defaultBlockState()
                        .setValue(TPS_REMAINING, (int) this.concentration)
                        .setValue(COLOUR, this.level().getBlockState(location.below()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS))
                        .setValue(BEHAVIOUR, BUSY));
                this.playSound(FloocraftSounds.GREENED.get(), 1.0F, 1.0F);
            }
            this.kill();
        } else {
            Block b = bs.getBlock();
            if (b == Blocks.CAMPFIRE) {
                this.level().setBlockAndUpdate(location,
                        FloocraftBlocks.FLOO_CAMPFIRE.get().defaultBlockState()
                                .setValue(FACING, bs.getValue(FACING))
                                .setValue(WATERLOGGED, bs.getValue(WATERLOGGED))
                                .setValue(TPS_REMAINING, (int) this.concentration)
                                .setValue(COLOUR, STANDARD)
                                .setValue(BEHAVIOUR, BUSY));
                this.playSound(FloocraftSounds.GREENED.get(), 1.0F, 1.0F);
                this.kill();
            } else if (b == Blocks.SOUL_CAMPFIRE) {
                this.level().setBlockAndUpdate(location,
                        FloocraftBlocks.FLOO_CAMPFIRE.get().defaultBlockState()
                                .setValue(FACING, bs.getValue(FACING))
                                .setValue(WATERLOGGED, bs.getValue(WATERLOGGED))
                                .setValue(TPS_REMAINING, (int) this.concentration)
                                .setValue(COLOUR, SOUL)
                                .setValue(BEHAVIOUR, BUSY));
                this.playSound(FloocraftSounds.GREENED.get(), 1.0F, 1.0F);
                this.kill();
            }
        }
        super.tick();
    }

    /**
     * Called when the entity is attacked.
     * TODO Add a case for campfires whenever this works again
     */
//    @Override
//    public boolean attackEntityFrom(DamageSource source, float amount) {
//        if(source.type() == DamageType.IN_FIRE || source == DamageSource.ON_FIRE) {
//            BlockPos pos = this.getPosition();
//            if (this.world.getBlockState(pos).getBlock().isIn(BlockTags.FIRE)) {
//                if(((FlooFlamesTemp)FloocraftBase.GREEN_FLAMES_TEMP.get()).isInFireplace(this.world, pos) != null) {
//                    Block fireBlock = SoulFireBlock.shouldLightSoulFire(this.world.getBlockState(pos.down()).getBlock()) ?
//                            FloocraftBase.MAGENTA_FLAMES_BUSY.get() : FloocraftBase.GREEN_FLAMES_BUSY.get();
//                    this.world.setBlockState(pos, fireBlock.getDefaultState().with(BlockStateProperties.AGE_0_15, (int) this.concentration), 3);
//                    this.playSound(FloocraftBase.GREENED.get(), 1.0F, 1.0F);
//                }
//                this.remove();
//                return true;
//            }
//            else
//            {
//                return false;
//            }
//        }
//        return super.attackEntityFrom(source, amount);
//    }
}
