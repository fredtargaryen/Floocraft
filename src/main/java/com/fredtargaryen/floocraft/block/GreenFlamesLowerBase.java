package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFire;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public abstract class GreenFlamesLowerBase extends BlockFire
{
    protected TileEntityFireplace boundSign;
	protected int speedCount;
    @SideOnly(Side.CLIENT)
    protected IIcon[] icons;
	
	@SideOnly(Side.CLIENT)
	@Override
    public IIcon getFireIcon(int i0)
    {
        return this.icons[i0];
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return this.icons[0];
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister i)
    {
        this.icons = new IIcon[] {i.registerIcon(DataReference.resPath(this.getUnlocalizedName()) + "_layer_0"),
                                    i.registerIcon(DataReference.resPath(this.getUnlocalizedName()) + "_layer_1")};
    }

	public boolean isCollidable()
	{
		return true;
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
		}
	}
		
	@SideOnly(Side.CLIENT)
	public void doClientGuiTings(EntityPlayer e, int x, int y, int z)
	{
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
		if(Minecraft.getMinecraft().currentScreen == null && proxy.ticker.override == -1)
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiTeleport((EntityClientPlayerMP)e, x, y, z));
			proxy.ticker.start();
		}
	}

	//@Override
	//public TileEntity createNewTileEntity(World var1, int var2)
	//{
		//return new TileEntityFire();
	//}

    @Override
	public int tickRate(World par1World)
	{
		return 20;
	}

    @Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		if (!isInFireplace(par1World, par2, par3, par4))
		{
			par1World.setBlock(par2, par3, par4, Blocks.fire);
		}
		else
		{
			par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
		}
	}
	/*
	 * Use instead of getFireplaceSpeed when necessary - faster process
	 */
    protected boolean isInFireplace(World w, int x, int y, int z)
    {
        if(!w.canBlockSeeTheSky(x, y, z))
        {
            Block blockAbove = w.getBlock(x, y + 1, z);
            if(blockAbove instanceof GreenFlamesBusyHigher || blockAbove instanceof BlockAir)
            {
                int topBlock = y + 2;
                while (w.getBlock(x, topBlock, z) instanceof BlockAir)
                {
                    topBlock++;
                }
                if(FloocraftBase.acceptedBlocks.contains(w.getBlock(x, topBlock, z)))
                {
                    int i0 = this.validColumn(w, x, y, topBlock, z - 1)?1:0;
                    int i1 = this.validColumn(w, x, y, topBlock, z + 1)?1:0;
                    int i2 = this.validColumn(w, x - 1, y, topBlock, z)?1:0;
                    int i3 = this.validColumn(w, x + 1, y, topBlock, z)?1:0;
                    switch(i0+i1+i2+i3)
                    {
                        case 3:
                        {
                            int bondX, bondZ;
                            if (i0 == 0)
                            {
                                bondX = x;
                                bondZ = z - 1;
                            }
                            else if (i1 == 0)
                            {
                                bondX = x;
                                bondZ = z + 1;
                            }
                            else if (i2 == 0)
                            {
                                bondX = x - 1;
                                bondZ = z;
                            }
                            else if (i3 == 0)
                            {
                                bondX = x + 1;
                                bondZ = z;
                            }
                            else
                            {
                                break;
                            }
                            TileEntity t = w.getTileEntity(bondX, topBlock, bondZ);
                            if(t instanceof TileEntityFireplace)
                            {
                                this.boundSign = (TileEntityFireplace) t;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected boolean validColumn(World w, int x, int bottomY, int topY, int z)
    {
        boolean valid = true;
        while(valid && bottomY < topY)
        {
            if(!FloocraftBase.acceptedBlocks.contains(w.getBlock(x, bottomY, z)))
            {
                valid = false;
            }
            bottomY++;
        }
        return valid;
    }

	/**
	 * After release I might add support for fireplaces made from bricks
	 * from other mods, through the OreDictionary. They will probably
	 * be the same tier as stone bricks.
	 */
	 protected int getFireplaceSpeed(World w, int x, int y, int z)
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

    @Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
	{
        if(!isInFireplace(par1World, par2, par3, par4))
	    {
		    par1World.setBlock(par2, par3, par4, Blocks.fire);
		}
	    par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World) + par5Random.nextInt(10));
	}

    @Override
    public int getChanceToEncourageFire(IBlockAccess world, int x, int y, int z, int oldChance, ForgeDirection face)
    {
        return 0;
    }
}