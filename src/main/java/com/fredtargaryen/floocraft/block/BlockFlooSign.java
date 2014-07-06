package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSign;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFlooSign extends BlockSign
{
	public IIcon tileIcon;
	public BlockFlooSign()
	{
		super(TileEntityFireplace.class, false);
		setHardness(2.0F);
	}
	
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
		int l = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        float f = 0.28125F;
        float f1 = 0.78125F;
        float f2 = 0.0F;
        float f3 = 1.0F;
        float f4 = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        if (l == 2)
        {
            this.setBlockBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
        }

        if (l == 3)
        {
            this.setBlockBounds(f2, f, 0.0F, f3, f1, f4);
        }

        if (l == 4)
        {
            this.setBlockBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
        }
        
        if (l == 5)
        {
            this.setBlockBounds(0.0F, f, f2, f4, f1, f3);
        }        
    }

    @Override
	public TileEntity createNewTileEntity(World par1World, int par2)
    {
        try
        {
            return new TileEntityFireplace();
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }
	
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
    {
		if(!par1World.isRemote)
		{
	        int i1 = par1World.getBlockMetadata(par2, par3, par4);
	        boolean flag = true;
	
	        if (i1 == 2 && FloocraftBase.acceptedBlocks.contains(par1World.getBlock(par2, par3, par4 + 1)))
	        {
	            flag = false;
	        }
	        else if (i1 == 3 && FloocraftBase.acceptedBlocks.contains(par1World.getBlock(par2, par3, par4 - 1)))
	        {
	            flag = false;
	        }
	        else if (i1 == 4 && FloocraftBase.acceptedBlocks.contains(par1World.getBlock(par2 + 1, par3, par4)))
	        {
	            flag = false;
	        }
	        else if (i1 == 5 && FloocraftBase.acceptedBlocks.contains(par1World.getBlock(par2 - 1, par3, par4)))
	        {
	            flag = false;
	        }
	        if (flag)
	        {
	        	super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
	    		this.onBlockDestroyedByPlayer(par1World, par2, par3, par4, i1);
	        }
		}
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
    }
	
	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return FloocraftBase.itemFlooSign;
    }
	
	/**
     * Called when a user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
     */
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        return new ItemStack(FloocraftBase.itemFlooSign, 1);
    }
	
    /**
     * Called upon the block being destroyed by an explosion
     */
    @Override
    public void onBlockDestroyedByExplosion(World w, int x, int y, int z, Explosion p_149723_5_)
    {
        int m = w.getBlockMetadata(x, y, z);
    	TileEntityFireplace.removeLocation(w, x, y, z, m);
    	super.onBlockDestroyedByExplosion(w, x, y, z, p_149723_5_);
    }
    
    @Override
	public void onBlockDestroyedByPlayer(World par1World, int x, int y, int z, int m)
	{
    	TileEntityFireplace.removeLocation(par1World, x, y, z, m);
		super.onBlockDestroyedByPlayer(par1World, x, y, z, m);
	}
	
	@SideOnly(Side.CLIENT)
	 public void registerBlockIcons(IIconRegister par1IIconRegister)
	 {
		 this.tileIcon = par1IIconRegister.registerIcon("ftfloocraft:"+this.getUnlocalizedName().substring(5));
	 }
	
	@SideOnly(Side.CLIENT)
	/**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public IIcon getIcon(int par1, int par2)
    {
        return this.tileIcon;
    }
}