package com.fredtargaryen.floocraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesTemp extends GreenFlamesBusy {
    public GreenFlamesTemp() { super(); }

    @Override
    public int tickRate(IWorldReaderBase par1World)
    {
        return 100;
    }

    @Override
    public void tick(IBlockState state, World world, BlockPos pos, Random rand) {
        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
    }

    @Override
    public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) { }
}