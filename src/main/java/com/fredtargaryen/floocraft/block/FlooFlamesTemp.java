package com.fredtargaryen.floocraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class FlooFlamesTemp extends FlooFlamesBusy {
    public FlooFlamesTemp(int lightLevel) { super(lightLevel); }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        Block fireBlock = SoulFireBlock.shouldLightSoulFire(world.getBlockState(pos.down()).getBlock()) ? Blocks.SOUL_FIRE : Blocks.FIRE;
        world.setBlockState(pos, fireBlock.getDefaultState());
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) { }
}