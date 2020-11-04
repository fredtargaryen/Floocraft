package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

import static net.minecraft.state.properties.BlockStateProperties.AGE_0_15;

public class FlooFlamesBusy extends FlooFlamesBase {
    public FlooFlamesBusy() { super(15); }

	@Override
    public void tick(BlockState state, ServerWorld w, BlockPos pos, Random rand) {
        if(w.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE, false) == null) {
            Block fireBlock = SoulFireBlock.shouldLightSoulFire(w.getBlockState(pos.down()).getBlock()) ? FloocraftBase.MAGENTA_FLAMES_IDLE.get() : FloocraftBase.GREEN_FLAMES_IDLE.get();
            w.setBlockState(pos, fireBlock.getDefaultState().with(AGE_0_15, state.get(AGE_0_15)));
        }
        super.tick(state, w, pos, rand);
    }
}