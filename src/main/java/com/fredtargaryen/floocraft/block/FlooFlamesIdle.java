package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import static net.minecraft.state.properties.BlockStateProperties.AGE_0_15;

public class FlooFlamesIdle extends FlooFlamesBase {
    private static final VoxelShape SMALLBOX = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public FlooFlamesIdle() { super(12); }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) { return SMALLBOX; }

    @Override
    public void tick(BlockState state, ServerWorld w, BlockPos pos, Random rand) {
        if(w.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, (double) DataReference.FLOO_FIRE_DETECTION_RANGE, false) != null)
        {
            Block fireBlock = SoulFireBlock.shouldLightSoulFire(w.getBlockState(pos.down()).getBlock()) ? FloocraftBase.MAGENTA_FLAMES_BUSY.get() : FloocraftBase.GREEN_FLAMES_BUSY.get();
            w.setBlockState(pos, fireBlock.getDefaultState().with(AGE_0_15, state.get(AGE_0_15)));
        }
        super.tick(state, w, pos, rand);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void doSmokeParticles(BlockState stateIn, World world, BlockPos pos, Random rand) {
        for (int i = 0; i < 3; ++i) {
            double d0 = (double)pos.getX() + rand.nextDouble();
            double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double)pos.getZ() + rand.nextDouble();
            world.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}
