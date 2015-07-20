package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesIdle extends GreenFlamesBase
{
	public GreenFlamesIdle()
    {
		super();
    }

    @Override
    public void updateTick(World w, int x, int y, int z, Random par5Random)
    {
        if(w.getClosestPlayer((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE) != null)
        {
            w.setBlock(x, y, z, FloocraftBase.greenFlamesBusyLower, w.getBlockMetadata(x, y, z), 2);
        }
        super.updateTick(w, x, y, z, par5Random);
    }
}
