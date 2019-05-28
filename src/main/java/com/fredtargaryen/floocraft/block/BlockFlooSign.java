package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFlooSign extends BlockWallSign {
	public BlockFlooSign() {
		super(Properties.create(Material.WOOD)
                .doesNotBlockMovement()
                .hardnessAndResistance(1.0F)
                .sound(SoundType.WOOD));
		this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, false));
	}

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        try {
            return new TileEntityFireplace();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    //pos is the position of this block...
    public void neighborChanged(IBlockState state, World w, BlockPos pos, Block blockIn, BlockPos p_189540_5_) {
        if (!w.getBlockState(pos.offset(state.get(FACING).getOpposite())).getMaterial().isSolid()) {
            state.dropBlockAsItem(w, pos, 0);
        }
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
        return 1;
    }

    @Override
    public void onReplaced(IBlockState state, World w, BlockPos pos, IBlockState newState, boolean isMoving) {
        if(!w.isRemote) {
            TileEntityFireplace tef = (TileEntityFireplace) w.getTileEntity(pos);
            if (tef.getConnected()) {
                //Finds the fireplace position from the sign position and rotation
                //The block below the block at the top of the fireplace
                pos = pos.offset(state.get(FACING).getOpposite());
                FloocraftWorldData.forWorld(w).removeLocation(pos.getX(), tef.getY(), pos.getZ());
            }
        }
        super.onReplaced(state, w, pos, newState, isMoving);
    }
}