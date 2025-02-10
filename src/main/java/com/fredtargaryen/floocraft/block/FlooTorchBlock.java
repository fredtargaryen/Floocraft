package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.FloocraftParticleTypes;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.TeleportByTorchMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

/**
 * TODO:
 * No sound played
 * Get green flame
 */
public class FlooTorchBlock extends TorchBlock {
    public FlooTorchBlock() {
        //super((SimpleParticleType) FloocraftParticleTypes.FLOO_TORCH_FLAME.get(), Block.Properties.of()
                super(ParticleTypes.FLAME, Block.Properties.of()
                .noCollission()
                .instabreak()
                .lightLevel(state -> 14)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        attemptFlooTorchTeleport(state, level, pos, entity);
    }

    public static void attemptFlooTorchTeleport(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (level.isClientSide) {
            if (!FloocraftBase.ClientModEvents.torchTicker.isRunning()) {
                if (entity instanceof Player) {
                    FloocraftBase.info("Sending tp message");
                    MessageHandler.sendToServer(new TeleportByTorchMessage());
                    FloocraftBase.ClientModEvents.torchTicker.start();
                }
            }
        }
    }
}