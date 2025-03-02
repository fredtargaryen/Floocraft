package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class FlooWallTorchBlock extends WallTorchBlock {
    private SimpleParticleType flooFlameParticle;

    public FlooWallTorchBlock() {
        super(ParticleTypes.FLAME, Block.Properties.of()
                .noCollission()
                .instabreak()
                .lightLevel(state -> 14)
                .sound(SoundType.WOOD)
                .dropsLike(FloocraftBlocks.FLOO_TORCH.get())
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        FlooTorchBlock.attemptFlooTorchTeleport(state, level, pos, entity);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        Direction direction = pState.getValue(FACING);
        double d0 = (double)pPos.getX() + 0.5;
        double d1 = (double)pPos.getY() + 0.7;
        double d2 = (double)pPos.getZ() + 0.5;
        double d3 = 0.22;
        double d4 = 0.27;
        Direction direction1 = direction.getOpposite();
        if (this.flooFlameParticle == null) this.flooFlameParticle = FloocraftParticleTypes.FLOO_TORCH_FLAME.get();
        pLevel.addParticle(
                this.flooFlameParticle, d0 + 0.27 * (double)direction1.getStepX(), d1 + 0.22, d2 + 0.27 * (double)direction1.getStepZ(), 0.0, 0.0, 0.0
        );
    }
}