package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBusy;
import com.fredtargaryen.floocraft.block.GreenFlamesTemp;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityDroppedFlooPowder extends EntityItem {
    private byte concentration;

	public EntityDroppedFlooPowder(World world, double x, double y, double z, ItemStack stack, byte conc) {
		super(world, x, y, z, stack);
        this.concentration = conc;
	}
	
	public void setImmunity()
	{
		super.isImmuneToFire = true;
	}

	/**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        BlockPos pos = this.getPosition();
        if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
			if(((GreenFlamesTemp)FloocraftBase.GREEN_FLAMES_TEMP).isInFireplace(this.world, pos) != null) {
                this.world.setBlockState(pos, FloocraftBase.GREEN_FLAMES_BUSY.getDefaultState().with(BlockStateProperties.AGE_0_15, (int) this.concentration), 3);
                this.playSound(FloocraftBase.GREENED, 1.0F, 1.0F);
            }
            this.remove();
        }
        super.tick();
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
    public boolean writeUnlessPassenger(NBTTagCompound compound) {
        super.writeUnlessPassenger(compound);
        compound.setByte("Concentration", this.concentration);
        return true;
    }

    @Override
    public void read(NBTTagCompound par1) {
        super.read(par1);
        this.concentration = par1.getByte("Concentration");
    }
}
