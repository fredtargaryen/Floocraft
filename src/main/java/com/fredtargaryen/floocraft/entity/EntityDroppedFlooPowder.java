package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBusyHigher;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDroppedFlooPowder extends EntityItem
{
	public EntityDroppedFlooPowder(World world, double x, double y, double z, ItemStack stack)
	{
		super(world, x, y, z, stack);
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
		if(this.worldObj.getBlock(intX, intY, intZ) instanceof BlockFire)
		{
			this.worldObj.setBlock(intX, intY, intZ, FloocraftBase.greenFlamesBusyLower);
            if(this.worldObj.getBlock(intX, intY + 1, intZ) instanceof BlockAir)
            {
                this.worldObj.setBlock(intX, intY + 1, intZ, new GreenFlamesBusyHigher());
            }
			this.setDead();
		}
		else if(this.worldObj.getBlock(intX, intY, intZ) instanceof BlockTorch)
		{
			this.worldObj.setBlock(intX, intY, intZ, FloocraftBase.flooTorch);
			this.setDead();
		}
    }
}
