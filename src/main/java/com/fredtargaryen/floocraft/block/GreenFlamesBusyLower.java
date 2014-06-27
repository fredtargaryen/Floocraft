package com.fredtargaryen.floocraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.fredtargaryen.floocraft.FloocraftBase;

public class GreenFlamesBusyLower extends GreenFlamesLowerBase
{	
	public GreenFlamesBusyLower()
    {
		super();
    }
	
	@Override
	public void onBlockAdded(World par1World, int x, int y, int z)
	{
		if(par1World.getBlock(x, y + 1, z) == Blocks.air)
		{
			par1World.setBlock(x, y + 1, z, FloocraftBase.greenFlamesBusyHigher);
    	}
		super.onBlockAdded(par1World, x, y, z);
	}
}