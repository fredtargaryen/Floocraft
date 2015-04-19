package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFlooSign extends BlockWallSign
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockFlooSign()
	{
		super();
		setHardness(2.0F);
	}

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[]{FACING});
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
    public void onNeighborBlockChange(World w, BlockPos pos, IBlockState state, Block neighbourBlock)
    {
        if(!w.isRemote)
        {
            TileEntityFireplace t = (TileEntityFireplace) w.getTileEntity(pos);
            if(t.getConnected())
            {
                TileEntityFireplace.removeLocation(w, pos, (EnumFacing)w.getBlockState(pos).getValue(FACING));
            }
        }
        super.onNeighborBlockChange(w, pos, state, neighbourBlock);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return FloocraftBase.itemFlooSign;
    }
	
	/**
     * Called when a user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
     */
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos)
    {
        return new ItemStack(FloocraftBase.itemFlooSign, 1);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random p_149745_1_)
    {
        return 1;
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state)
    {
        if(!w.isRemote)
        {
            TileEntityFireplace tef = (TileEntityFireplace) w.getTileEntity(pos);
            if (tef.getConnected())
            {
                TileEntityFireplace.removeLocation(w, pos, (EnumFacing)w.getBlockState(pos).getValue(FACING));
            }
        }
        super.breakBlock(w, pos, state);
    }
}