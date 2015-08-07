package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesTemp;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDroppedFlooPowder extends EntityItem
{
    private byte concentration;

	public EntityDroppedFlooPowder(World world, double x, double y, double z, ItemStack stack, byte conc)
	{
		super(world, x, y, z, stack);
        this.concentration = conc;
	}
	
	public void setPickupDelay(int time)
	{
		super.delayBeforeCanPickup = time;
	}
	
	public void setImmunity()
	{
		super.isImmuneToFire = true;
	}
	
	@Override
	/**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
		super.onUpdate();
		int intX = MathHelper.floor_double(this.posX);
        int intY = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset) + 1;
        int intZ = MathHelper.floor_double(this.posZ);
		if(this.worldObj.getBlock(intX, intY, intZ) == Blocks.fire) {
			this.worldObj.setBlock(intX, intY, intZ, FloocraftBase.greenFlamesTemp);
			if(((GreenFlamesTemp)this.worldObj.getBlock(intX, intY, intZ)).isInFireplace(this.worldObj, intX, intY, intZ)){
				this.worldObj.setBlock(intX, intY, intZ, FloocraftBase.greenFlamesBusy, this.concentration, 3);
				this.playSound(DataReference.MODID+":greened", 1.0F, 1.0F);
			}
			else
			{
				this.worldObj.setBlock(intX, intY, intZ, Blocks.fire);
			}
			this.setDead();
		}
    }

    @Override
    public void writeToNBT(NBTTagCompound par1)
    {
        super.writeToNBT(par1);
        par1.setByte("Concentration", this.concentration);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1)
    {
        super.readFromNBT(par1);
        this.concentration = par1.getByte("Concentration");
    }
}
