package com.fredtargaryen.floocraft.block;

import net.minecraft.block.BlockFire;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.fredtargaryen.floocraft.FloocraftBase;

public class GreenFlamesBusyHigher extends BlockFire
{
	 public GreenFlamesBusyHigher()
     {
           super();
     }
	 
	 @SideOnly(Side.CLIENT)
	 public IIcon getFireIcon(int par1)
	 {
	     return this.blockIcon;
	 }
	 
	 public void onBlockAdded(World par1World, int par2, int par3, int par4)
	 {
		 if (par1World.getBlock(par2, par3 - 1, par4) != FloocraftBase.greenFlamesBusyLower)
	     {
			 par1World.setBlockToAir(par2, par3, par4);
	     }
		 else
		 {
			 par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
		 }
	 }
	 
	 public int tickRate(World par1World)
	 {
	     return 20;
	 }
	 
	 public boolean canBlockStay(World par1World, int par2, int par3, int par4)
	 {
		 if(par1World.getBlock(par2, par3 - 1, par4) != FloocraftBase.greenFlamesBusyLower)
		 {
			 return true;
		 }
		 return false;
	 }
}