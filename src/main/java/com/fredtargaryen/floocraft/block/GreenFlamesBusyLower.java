package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesBusyLower extends GreenFlamesLowerBase
{	
	public GreenFlamesBusyLower()
    {
		super();
    }
	
	@Override
    public void updateTick(World w, int x, int y, int z, Random par5Random)
    {
        if(w.getBlock(x, y + 1, z) == Blocks.air)
        {
            w.setBlock(x, y + 1, z, FloocraftBase.greenFlamesBusyHigher);
        }

        if(w.getClosestPlayer((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE) == null)
        {
            w.setBlock(x, y, z, FloocraftBase.greenFlamesIdle);
        }
        super.updateTick(w, x, y, z, par5Random);
    }
}