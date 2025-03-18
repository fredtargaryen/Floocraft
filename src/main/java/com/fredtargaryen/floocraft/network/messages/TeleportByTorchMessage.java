package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Randomly teleport a sender that touches a Floo Torch.
 * Sent when a client sender touches a Floo Torch and causes a random teleport.
 * If there are Floo Torches nearby, go to them; otherwise go to an empty place if possible.
 * This can't be done using just the server; this just results in "sender moved too quickly!"
 * Direction: client to server
 *
 * @param blockPos The position of the torch. Not necessarily the same as the player position
 */
public record TeleportByTorchMessage(BlockPos blockPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TeleportByTorchMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("tp_torch"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, TeleportByTorchMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, TeleportByTorchMessage::blockPos,
                    TeleportByTorchMessage::new
            );

    public static void handle(final TeleportByTorchMessage message, final IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        ServerLevel level = sender.serverLevel();
        context.enqueueWork(() -> {
            BlockPos tpOrigin = message.blockPos();
            int originX = tpOrigin.getX();
            int originY = tpOrigin.getY();
            int originZ = tpOrigin.getZ();
            int minx = originX - 3;
            int maxx = originX + 3;
            int minz = originZ - 3;
            int maxz = originZ + 3;
            List<BlockPos> airCoords = new ArrayList<>();
            List<BlockPos> torchCoords = new ArrayList<>();
            for (int x = minx; x <= maxx; x++) {
                for (int z = minz; z <= maxz; z++) {
                    // Prevent the sender from teleporting onto the torch again
                    if (x == originX && z == originZ) continue;
                    BlockPos nextPos = new BlockPos(x, originY, z);
                    BlockState feetBlockState = level.getBlockState(nextPos);
                    BlockState headBlockState = level.getBlockState(nextPos.above());
                    int posScore = getPosScore(feetBlockState) + getPosScore(headBlockState);
                    if (posScore > 3) {
                        // There's a Floo Torch and a non-solid block there, or two Floo Torches
                        torchCoords.add(nextPos);
                    } else if (posScore == 2) {
                        // There are two non-solid blocks here
                        airCoords.add(nextPos);
                    }
                }
            }
            BlockPos chosenPos = null;
            if (!torchCoords.isEmpty()) {
                //There are no nearby torches so just pick any available position
                chosenPos = torchCoords.get(level.random.nextInt(torchCoords.size()));
            } else if (!airCoords.isEmpty()) {
                //There are no nearby torches so just pick any available position
                chosenPos = airCoords.get(level.random.nextInt(airCoords.size()));
            }
            if (chosenPos != null) {
                double x = chosenPos.getX() + 0.5;
                double y = chosenPos.getY();
                double z = chosenPos.getZ() + 0.5;
                if (sender.getVehicle() != null) {
                    sender.stopRiding();
                }
                sender.connection.teleport(x, y, z, sender.getYRot(), sender.getXRot());
                sender.fallDistance = 0.0F;
                level.playSound(null, chosenPos, FloocraftSounds.FLICK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal(e.getMessage()));
            return null;
        });
    }

    private static int getPosScore(BlockState blockState) {
        Block flooTorchBlock = FloocraftBlocks.FLOO_TORCH.get();
        if (blockState.getBlock() == flooTorchBlock) {
            return 3;
        } else if (blockState.canBeReplaced()) {
            return 1;
        }
        return 0;
    }
}
