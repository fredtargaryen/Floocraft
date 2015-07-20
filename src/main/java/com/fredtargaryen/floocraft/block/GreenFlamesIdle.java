package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesIdle extends GreenFlamesBase
{
	public GreenFlamesIdle()
    {
		super();
    }

    @Override
    public void updateTick(World w, BlockPos pos, IBlockState state, Random par5Random)
    {
        if(w.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE) != null)
        {
            w.setBlockState(pos, FloocraftBase.greenFlamesBusy.getDefaultState().withProperty(AGE, w.getBlockState(pos).getValue(AGE)));
        }
        super.updateTick(w, pos, state, par5Random);
    }
}
