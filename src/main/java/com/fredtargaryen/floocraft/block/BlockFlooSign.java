package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFlooSign extends BlockSign
{
	private IIcon tileIcon;

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

    @Override
    public void onNeighborBlockChange(World w, int x, int y, int z, Block b)
    {
        int backX = x;
        int backZ = z;
        int m = w.getBlockMetadata(x, y, z);
        switch(m)
        {
            case 2:
            {
                backZ++;
                break;
            }
            case 3:
            {
                backZ--;
                break;
            }
            case 4:
            {
                backX++;
                break;
            }
            case 5:
            {
                backX--;
                break;
            }
        }
        if (!w.getBlock(backX, y, backZ).isNormalCube())
        {
            this.dropBlockAsItem(w, x, y, z, m, 0);
            w.setBlockToAir(x, y, z);
        }
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
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        return new ItemStack(FloocraftBase.itemFlooSign, 1);
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

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random p_149745_1_)
    {
        return 1;
    }

    public void breakBlock(World w, int x, int y, int z, Block b, int m)
    {
        if(!w.isRemote)
        {
            TileEntityFireplace tef = (TileEntityFireplace) w.getTileEntity(x, y, z);
            if (tef.getConnected())
            {
                //Finds the fireplace position from the sign position and rotation
                //The block below the block at the top of the fireplace
                int newX = x;
                int newZ = z;
                switch(m)
                {
                    case 2:
                    {
                        newZ++;
                        break;
                    }
                    case 3:
                    {
                        newZ--;
                        break;
                    }
                    case 4:
                    {
                        newX++;
                        break;
                    }
                    case 5:
                    {
                        newX--;
                        break;
                    }
                }
                int newY = TileEntityFireplace.iterateDownFromTop(w, newX, y, newZ);
                FloocraftWorldData.forWorld(w).removeLocation(newX, newY, newZ);
            }
        }
        super.breakBlock(w, x, y, z, b, m);
    }
}