package com.fredtargaryen.floocraft.block;

import net.minecraft.world.World;

public class GreenFlamesIdle extends GreenFlamesLowerBase
{	
	public GreenFlamesIdle()
    {
		super();
    }
	
	public boolean approveOrDenyTeleport(World par1World, int par2, int par3, int par4)
	{
		return this.isInFireplace(par1World, par2, par3, par4);
	}
}
