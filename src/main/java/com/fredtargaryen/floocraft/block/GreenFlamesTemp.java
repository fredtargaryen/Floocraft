package com.fredtargaryen.floocraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesTemp extends GreenFlamesBusy
{
    public GreenFlamesTemp()
    {
        super();
    }

    @Override
    public int tickRate(World par1World)
    {
        return 100;
    }

    @Override
    public void updateTick(World w, BlockPos pos, IBlockState state, Random par5Random)
    {
        w.setBlockState(pos, Blocks.fire.getDefaultState());
    }

    @Override
    public void onEntityCollidedWithBlock(World w, BlockPos pos, IBlockState state, Entity e)
    {
    }

    @Override
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state) {}
}