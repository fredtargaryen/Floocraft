package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

/**
 * TODO:
 * Get green flame
 */
public class FlooWallTorchBlock extends WallTorchBlock {

    public FlooWallTorchBlock() {
        //super((SimpleParticleType) FloocraftParticleTypes.FLOO_TORCH_FLAME.get(), Block.Properties.of()
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
}