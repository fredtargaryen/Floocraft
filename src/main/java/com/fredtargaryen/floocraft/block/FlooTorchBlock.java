package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.FloocraftParticleTypes;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.TeleportByTorchMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nonnull;

public class FlooTorchBlock extends TorchBlock {
    private SimpleParticleType flooFlameParticle;

    public FlooTorchBlock() {
        super(ParticleTypes.FLAME, Block.Properties.of()
                .noCollission()
                .instabreak()
                .lightLevel(state -> 14)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        attemptFlooTorchTeleport(state, level, pos, entity);
    }

    public static void attemptFlooTorchTeleport(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        if (level.isClientSide) {
            if (!FloocraftBase.ClientModEvents.torchTicker.isRunning()) {
                if (entity instanceof Player) {
                    FloocraftBase.info("Sending tp message");
                    MessageHandler.sendToServer(new TeleportByTorchMessage(pos));
                    FloocraftBase.ClientModEvents.torchTicker.start();
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        double d0 = (double) pPos.getX() + 0.5;
        double d1 = (double) pPos.getY() + 0.7;
        double d2 = (double) pPos.getZ() + 0.5;
        if (this.flooFlameParticle == null) this.flooFlameParticle = FloocraftParticleTypes.FLOO_TORCH_FLAME.get();
        pLevel.addParticle(this.flooFlameParticle, d0, d1, d2, 0.0, 0.0, 0.0);
    }
}