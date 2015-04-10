package com.fredtargaryen.floocraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesIdleTemp extends GreenFlamesIdle
{
    @Override
    public int tickRate(World par1World)
    {
        return 100;
    }

    public GreenFlamesIdleTemp()
    {
        super();
    }

    public boolean approveOrDenyTeleport(World par1World, BlockPos pos)
    {
        return this.isInFireplace(par1World, pos);
    }

    @Override
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state)
    {
        par1World.scheduleUpdate(pos, this, this.tickRate(par1World));
    }

    @Override
    public void updateTick(World w, BlockPos pos, IBlockState state, Random par5Random)
    {
        w.setBlockState(pos, Blocks.fire.getDefaultState());
    }
}