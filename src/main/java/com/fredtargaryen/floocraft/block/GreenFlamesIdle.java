package com.fredtargaryen.floocraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
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
