package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesBusyLower extends GreenFlamesLowerBase
{
    private int renderID;

	public GreenFlamesBusyLower(int id)
    {
		super();
        this.renderID = id;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
    }

    @Override
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state)
    {
        par1World.scheduleUpdate(pos, this, this.tickRate(par1World));
    }

	@Override
    public void updateTick(World w, BlockPos pos, IBlockState state, Random par5Random)
    {
        if(w.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE) == null)
        {
            w.setBlockState(pos, FloocraftBase.greenFlamesIdle.getDefaultState().withProperty(AGE, w.getBlockState(pos).getValue(AGE)), 2);
        }
        super.updateTick(w, pos, state, par5Random);
    }

    @Override
    public int getRenderType(){return this.renderID;}
}