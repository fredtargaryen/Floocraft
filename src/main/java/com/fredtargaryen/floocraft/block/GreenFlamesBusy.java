package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

import static net.minecraft.state.properties.BlockStateProperties.AGE_0_15;

public class GreenFlamesBusy extends GreenFlamesBase {
    public GreenFlamesBusy() { super(15); }

	@Override
    public void tick(BlockState state, ServerWorld w, BlockPos pos, Random rand) {
        if(w.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE, false) == null) {
            w.setBlockState(pos, FloocraftBase.GREEN_FLAMES_IDLE.getDefaultState().with(AGE_0_15, state.get(AGE_0_15)));
        }
        super.tick(state, w, pos, rand);
    }

    ////////////////////////
    //MIRAGE COMPATIBILITY//
    ////////////////////////
//    @Override
//    public TileEntity createTileEntity(World world, BlockState state)
//    {
//        TileEntityMirageFire temf = null;
//        if(FloocraftBase.isMirageInstalled())
//        {
//            temf = new TileEntityMirageFire();
//            temf.setRadius(7.0F);
//        }
//        return temf;
//    }
}