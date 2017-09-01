package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.tileentity.TileEntityAlbedoFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesIdle extends GreenFlamesBase
{
	public GreenFlamesIdle()
    {
		super();
    }

    @Override
    public void updateTick(World w, BlockPos pos, IBlockState state, Random par5Random)
    {
        if(w.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE, false) != null)
        {
            w.setBlockState(pos, FloocraftBase.greenFlamesBusy.getDefaultState().withProperty(AGE, state.getValue(AGE)));
        }
        super.updateTick(w, pos, state, par5Random);
    }

    ////////////////////////
    //ALBEDO COMPATIBILITY//
    ////////////////////////
    @Override
    public boolean hasTileEntity(IBlockState ibs)
    {
        return FloocraftBase.isAlbedoInstalled();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) throws NullPointerException
    {
        TileEntityAlbedoFire teaf = null;
        if(FloocraftBase.isAlbedoInstalled())
        {
            teaf = new TileEntityAlbedoFire();
            teaf.setRadius(6.0F);
        }
        return teaf;
    }
}
