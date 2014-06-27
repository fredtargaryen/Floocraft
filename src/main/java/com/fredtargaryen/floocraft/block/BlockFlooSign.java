package com.fredtargaryen.floocraft.block;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;

public class BlockFlooSign extends BlockSign
{
	public Class fireplaceTE = TileEntityFireplace.class;
	public IIcon tileIcon;
	public BlockFlooSign(Class par2Class, boolean par3)
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
	
	public TileEntity createNewTileEntity(World par1World)
    {
        try
        {
            return (TileEntity)new TileEntityFireplace();
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
	
	        if (i1 == 2 && par1World.getBlock(par2, par3, par4 + 1) == Blocks.brick_block)
	        {
	            flag = false;
	        }
	        else if (i1 == 3 && par1World.getBlock(par2, par3, par4 - 1) == Blocks.brick_block)
	        {
	            flag = false;
	        }
	        else if (i1 == 4 && par1World.getBlock(par2 + 1, par3, par4) == Blocks.brick_block)
	        {
	            flag = false;
	        }
	        else if (i1 == 5 && par1World.getBlock(par2 - 1, par3, par4) == Blocks.brick_block)
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
    public void onBlockDestroyedByExplosion(World p_149723_1_, int p_149723_2_, int p_149723_3_, int p_149723_4_, Explosion p_149723_5_)
    {
    	this.removeLocation(p_149723_1_, p_149723_2_, p_149723_3_, p_149723_4_);
    	super.onBlockDestroyedByExplosion(p_149723_1_, p_149723_2_, p_149723_3_, p_149723_4_, p_149723_5_);
    }
    
    @Override
	public void onBlockDestroyedByPlayer(World par1World, int x, int y, int z, int m)
	{
    	this.removeLocation(par1World, x, y, z);
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
	
	private void removeLocation(World w, int x, int y, int z)
	{
		if(!w.isRemote)
		{
			int m = w.getBlockMetadata(x, y, z);
			//Translates the sign's xyz to the fireplace's xyz.
			if(m == 2)
			{
				--z;
			}	
			if(m == 3)
			{
				++z;
			}
			if(m == 4)
			{
				--x;
			}
			if(m == 5)
			{
				++x;
			}
	        y -= 2;
	        FloocraftWorldData.forWorld(w).removeLocation(x, y, z);
		}
	}
}