package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.FlooFlamesTemp;
import net.minecraft.block.Block;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class DroppedFlooPowderEntity extends ItemEntity {
    private byte concentration;

	public DroppedFlooPowderEntity(World world, double x, double y, double z, ItemStack stack, byte conc) {
		super(world, x, y, z, stack);
        this.concentration = conc;
	}

    /**
     * Writes this entity to NBT, unless it has been removed or it is a passenger. Also writes this entity's passengers,
     * and the entity type ID (so the produced NBT is sufficient to recreate the entity).
     * To always write the entity, use {@link #writeWithoutTypeId}.
     *
     * @return True if the entity was written (and the passed compound should be saved); false if the entity was not
     * written.
     */
    @Override
    public boolean writeUnlessPassenger(@Nonnull CompoundNBT compound) {
        super.writeUnlessPassenger(compound);
        compound.putByte("Concentration", this.concentration);
        return true;
    }

    @Override
    public void read(CompoundNBT par1) {
        super.read(par1);
        this.concentration = par1.getByte("Concentration");
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.BLOCKS;
    }

    /**
     * Called when the entity is attacked.
     * TODO Add a case for campfires whenever this works again
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if(source == DamageSource.IN_FIRE || source == DamageSource.ON_FIRE) {
            BlockPos pos = this.getPosition();
            if (this.world.getBlockState(pos).getBlock().isIn(BlockTags.FIRE)) {
                if(((FlooFlamesTemp)FloocraftBase.GREEN_FLAMES_TEMP.get()).isInFireplace(this.world, pos) != null) {
                    Block fireBlock = SoulFireBlock.shouldLightSoulFire(this.world.getBlockState(pos.down()).getBlock()) ?
                            FloocraftBase.MAGENTA_FLAMES_BUSY.get() : FloocraftBase.GREEN_FLAMES_BUSY.get();
                    this.world.setBlockState(pos, fireBlock.getDefaultState().with(BlockStateProperties.AGE_0_15, (int) this.concentration), 3);
                    this.playSound(FloocraftBase.GREENED.get(), 1.0F, 1.0F);
                }
                this.remove();
                return true;
            }
            else
            {
                return false;
            }
        }
        return super.attackEntityFrom(source, amount);
    }
}
