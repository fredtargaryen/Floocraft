package com.fredtargaryen.floocraft.block;

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
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if(par1World.getBlock(par2, par3 + 1, par4) == Blocks.air)
        {
            par1World.setBlock(par2, par3 + 1, par4, FloocraftBase.greenFlamesBusyHigher);
        }
    }
}