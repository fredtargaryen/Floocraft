package com.fredtargaryen.floocraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesTemp extends GreenFlamesBusy {
    public GreenFlamesTemp() { super(); }

    @Override
    public int tickRate(IWorldReader par1World)
    {
        return 100;
    }

    @Override
    public void tick(BlockState state, World world, BlockPos pos, Random rand) {
        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) { }
}