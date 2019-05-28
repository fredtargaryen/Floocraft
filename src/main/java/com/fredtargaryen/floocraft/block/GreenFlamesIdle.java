package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.tileentity.TileEntityMirageFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesIdle extends GreenFlamesBase
{
	public GreenFlamesIdle() { super(12); }

    @Override
    public void updateTick(World w, BlockPos pos, IBlockState state, Random par5Random)
    {
        if(w.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE, false) != null)
        {
            w.setBlockState(pos, FloocraftBase.greenFlamesBusy.getDefaultState().withProperty(AGE, state.get(AGE)));
        }
        super.updateTick(w, pos, state, par5Random);
    }

    ////////////////////////
    //MIRAGE COMPATIBILITY//
    ////////////////////////
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
//        TileEntityMirageFire temf = null;
//        if(FloocraftBase.isMirageInstalled())
//        {
//            temf = new TileEntityMirageFire();
//            temf.setRadius(6.0F);
//        }
//        return temf;
        return null;
    }
}
