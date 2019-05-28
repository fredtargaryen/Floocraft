package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.tileentity.TileEntityMirageFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesBusy extends GreenFlamesBase
{
    public GreenFlamesBusy() { super(15); }

    private final AxisAlignedBB BUSYBOX = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BUSYBOX;
    }

	@Override
    public void updateTick(World w, BlockPos pos, IBlockState state, Random par5Random)
    {
        if(w.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE, false) == null)
        {
            w.setBlockState(pos, FloocraftBase.greenFlamesIdle.getDefaultState().withProperty(AGE, state.get(AGE)));
        }
        super.updateTick(w, pos, state, par5Random);
    }

    ////////////////////////
    //MIRAGE COMPATIBILITY//
    ////////////////////////
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        TileEntityMirageFire temf = null;
        if(FloocraftBase.isMirageInstalled())
        {
            temf = new TileEntityMirageFire();
            temf.setRadius(7.0F);
        }
        return temf;
    }
}