package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.BlockFlooTorch;
import com.fredtargaryen.floocraft.block.GreenFlames;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
		super.onUpdate();
        BlockPos pos = new BlockPos(this);
        IBlockState state = this.worldObj.getBlockState(pos);
		if(state.getBlock() == Blocks.fire)
		{
            BlockPos oneAbove = pos.up();
            if(this.worldObj.getBlockState(oneAbove).getBlock() == Blocks.air)
            {
			    this.worldObj.setBlockState(pos, FloocraftBase.greenFlames.getDefaultState()
                        .withProperty(GreenFlames.AGE, this.concentration)
                        .withProperty(GreenFlames.ACTIVE, true), 2);
            }
            this.worldObj.playSound((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), "ftfloocraft:greened", 1.0F, 1.0F, true);
			this.setDead();
		}
		else if(state.getBlock() == Blocks.torch)
        {
            EnumFacing e = (EnumFacing)state.getValue(BlockTorch.FACING);
            this.worldObj.setBlockState(pos, FloocraftBase.blockFlooTorch.getDefaultState().withProperty(BlockFlooTorch.FACING, e));
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
