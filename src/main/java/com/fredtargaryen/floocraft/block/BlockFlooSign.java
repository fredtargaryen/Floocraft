package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockFlooSign extends BlockWallSign
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockFlooSign()
	{
		super();
		setHardness(2.0F);
	}

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
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
    //pos is the position of this block...
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block blockIn, BlockPos p_189540_5_)
    {
        if (!w.getBlockState(pos.offset(((EnumFacing) state.getValue(FACING)).getOpposite())).getMaterial().isSolid())
        {
            this.dropBlockAsItem(w, pos, state, 0);
            w.setBlockToAir(pos);
        }
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
                //Finds the fireplace position from the sign position and rotation
                //The block below the block at the top of the fireplace
                pos = pos.offset(((EnumFacing)state.getValue(BlockFlooSign.FACING)).getOpposite());
                FloocraftWorldData.forWorld(w).removeLocation(pos.getX(), tef.getY(), pos.getZ());
            }
        }
        super.breakBlock(w, pos, state);
    }

    //////////////////////////
    //METHODS FROM BLOCKSIGN//
    //////////////////////////
    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return FloocraftBase.itemFlooSign;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(FloocraftBase.itemFlooSign);
    }
}