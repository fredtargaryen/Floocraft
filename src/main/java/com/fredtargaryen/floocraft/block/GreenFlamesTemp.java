package com.fredtargaryen.floocraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class GreenFlamesTemp extends GreenFlamesBusy {
    public GreenFlamesTemp() { super(); }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) { }
}