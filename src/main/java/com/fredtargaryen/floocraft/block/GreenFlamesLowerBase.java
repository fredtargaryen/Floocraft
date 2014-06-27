package com.fredtargaryen.floocraft.block;

import java.util.Random;

import com.fredtargaryen.floocraft.DataReference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import com.fredtargaryen.floocraft.tileentity.TileEntityFire;
import net.minecraft.block.BlockFire;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class GreenFlamesLowerBase extends BlockFire implements ITileEntityProvider
{
	protected TileEntityFire fireTE;
	private int[] chanceToEncourageFire = new int[0];
	protected int speedCount;
	
	public TileEntityFire getTileEntity()
	{
		return this.fireTE;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IIconRegister)
	{
		this.blockIcon = par1IIconRegister.registerIcon(DataReference.MODID+":"+this.getUnlocalizedName().substring(5));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public IIcon getFireIcon(int par1)
    {
        return this.blockIcon;
    }
 
	@SideOnly(Side.CLIENT)
	/**
	 * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	 */
	public IIcon getIcon(int par1, int par2)
	{
		return this.blockIcon;
	}
	
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face)
	{
		return true;
	}
	 
	public boolean isCollidable()
	{
		return true;
	}

	private boolean canNeighborBurn(World par1World, int par2, int par3, int par4)
	{
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		if(par5Entity instanceof EntityPlayer)
		{
			if(par1World.isRemote)
			{
				doClientGuiTings((EntityPlayer)par5Entity, par2, par3, par4);
			}
			else
			{
				this.fireTE = (TileEntityFire)par1World.getTileEntity(par2, par3, par4);
				if(this.fireTE == null)
				{
					this.fireTE = (TileEntityFire) createNewTileEntity(par1World, 0);
				}
				this.add((EntityPlayer)par5Entity);
			}			
		}
	}
		
	public void add(EntityPlayer e)
	{
		if(e instanceof EntityPlayerMP)
		{
			this.fireTE.addEntity((EntityPlayerMP)e);
		}
	}
		
	@SideOnly(Side.CLIENT)
	public void doClientGuiTings(EntityPlayer e, int x, int y, int z)
	{
		if(Minecraft.getMinecraft().currentScreen == null && FloocraftBase.proxy.ticker.override == -1)
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiTeleport((EntityClientPlayerMP)e, this.fireTE, x, y, z));
			FloocraftBase.proxy.ticker.start();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new TileEntityFire();
	}
	 
	public int tickRate(World par1World)
	{
		return 20;
	}
	
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		if (findFireplaceSpeed(par1World, par2, par3, par4) == 0)
		{
			par1World.setBlock(par2, par3, par4, Blocks.fire);
		}
		else
		{
			par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
		}
	}
	
	/**
	 * After release I might add support for fireplaces made from bricks
	 * from other mods, through the OreDictionary. They will probably
	 * be the same tier as stone bricks.
	 */
	 protected int findFireplaceSpeed(World w, int x, int y, int z)
	 {
		 if(isNiceBlock(w, x, y + 2, z) > -1)
		 {
			 byte b0 = 0;
		     byte b1 = 0;
		     
		     if(isNiceBlock(w, x - 1, y, z) != -1 && isNiceBlock(w, x + 1, y, z) != -1)
		     {
		    	 b0 = 1;
		     }
		     
		     if(isNiceBlock(w, x, y, z - 1) != -1 && isNiceBlock(w, x, y, z + 1) != -1)
		     {
		    	 b1 = 1;
		     }
		     
		     if(b0 != b1)
		     {
		    	  b0 = 0;
		    	  b1 = 0;
		     
		    	  if(isNiceBlock(w, x - 1, y + 1, z) != -1 && isNiceBlock(w, x + 1, y + 1, z) != -1)
		    	  {
		    		  b0 = 1;
		    	  }
		     
		    	  if(isNiceBlock(w, x, y + 1, z - 1) != -1 && isNiceBlock(w, x, y + 1, z + 1) != -1)
		    	  {
		    		  b1 = 1;
		    	  }
		     
		    	  if(b0 != b1)
		    	  {
		    		 if(b0 == 1)
		    		 {	 
		    			 return isNiceBlock(w, x - 1, y, z)
		    			 +isNiceBlock(w, x + 1, y, z)
		    			 +isNiceBlock(w, x - 1, y + 1, z)
		    			 +isNiceBlock(w, x + 1, y + 1, z)
		    			 +isNiceBlock(w, x, y + 2, z);
		    		 }
		    		 else if(b1 == 1)
		    		 {
		    			 return isNiceBlock(w, x, y, z - 1)
		    			+isNiceBlock(w, x, y, z + 1)
		    			+isNiceBlock(w, x, y + 1, z - 1)
		    			+isNiceBlock(w, x, y + 1, z + 1)
		    			+isNiceBlock(w, x, y + 2, z);
		    		 }
		    	  }
		     }
		 }
		 return 0;
	 }
	 
	 protected int isNiceBlock(World w, int x, int y, int z)
	 {
		 int i = FloocraftBase.acceptedBlocks.indexOf(w.getBlock(x, y, z));
		 if(i != -1)
		 {
			 return i+1;
		 }
		 return i;
	 }
		 
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
	{
		int s = findFireplaceSpeed(par1World, par2, par3, par4);
		if(s == 0)
	    {
		    par1World.setBlock(par2, par3, par4, Blocks.fire);
		}
	    else
		{
	    	this.fireTE
	    	.setFireplaceSpeed(s);
		    if(par1World.getBlock(par2, par3 + 1, par4) == Blocks.air)
		    {
		    	par1World.setBlock(par2, par3 + 1, par4, FloocraftBase.greenFlamesBusyHigher);
		    }
		    par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
		}
	}
}
