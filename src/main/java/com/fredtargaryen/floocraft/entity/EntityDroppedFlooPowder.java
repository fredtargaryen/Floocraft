package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBusy;
import com.fredtargaryen.floocraft.block.GreenFlamesTemp;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityDroppedFlooPowder extends EntityItem
{
    private byte concentration;

	public EntityDroppedFlooPowder(World world, double x, double y, double z, ItemStack stack, byte conc)
	{
		super(world, x, y, z, stack);
        this.concentration = conc;
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
        BlockPos pos = new BlockPos(this);
        if (this.worldObj.getBlockState(pos).getBlock() == Blocks.FIRE)
        {
			this.worldObj.setBlockState(pos, FloocraftBase.greenFlamesTemp.getDefaultState(), 2);
			if(((GreenFlamesTemp)this.worldObj.getBlockState(pos).getBlock()).isInFireplace(this.worldObj, pos))
			{
                this.worldObj.setBlockState(pos, FloocraftBase.greenFlamesBusy.getDefaultState().withProperty(GreenFlamesBusy.AGE, (int) this.concentration), 2);
                //this.playSound(DataReference.MODID+":greened", 1.0F, 1.0F);
            }
			else
			{
				this.worldObj.setBlockState(pos, Blocks.FIRE.getDefaultState(), 2);
			}
            this.setDead();
        }
        super.onUpdate();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound par1)
    {
        par1 = super.writeToNBT(par1);
        par1.setByte("Concentration", this.concentration);
        return par1;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1)
    {
        super.readFromNBT(par1);
        this.concentration = par1.getByte("Concentration");
    }
}
