package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FlooWallTorchBlock extends WallTorchBlock {
    private SimpleParticleType flooFlameParticle;

    public FlooWallTorchBlock() {
        super(ParticleTypes.FLAME, Block.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, DataReference.getResourceLocation(FloocraftBlocks.FLOO_WALL_TORCH_RL)))
                .noCollission()
                .instabreak()
                .lightLevel(state -> 14)
                .sound(SoundType.WOOD)
                .overrideLootTable(Optional.of(FloocraftBlocks.FLOO_TORCH.get().getLootTable().get()))
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity, InsideBlockEffectApplier effectApplier) {
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