package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Randomly teleport a player that touches a Floo Torch.
 * Sent when a client player touches a Floo Torch and causes a random teleport.
 * If there are Floo Torches nearby, go to them; otherwise go to an empty place if possible.
 * This can't be done using just the server; this just results in "Player moved too quickly!"
 * Direction: client to server
 */
public class TeleportByTorchMessage {
    public TeleportByTorchMessage() {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public TeleportByTorchMessage(FriendlyByteBuf buf) {
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            FloocraftBase.info("Torch message executing");
            ServerPlayer player = context.getSender();
            try (Level level = Objects.requireNonNull(player).level()) {
                BlockPos tpOrigin = player.blockPosition();
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
                        // Prevent the player from teleporting onto the torch again
                        if (x != originX && z != originZ) {
                            BlockPos nextPos = new BlockPos(x, originY, z);
                            BlockState feetBlockState = level.getBlockState(nextPos);
                            BlockState headBlockState = level.getBlockState(nextPos.above());
                            int posScore = this.getPosScore(feetBlockState) + this.getPosScore(headBlockState);
                            if (posScore > 3) {
                                // There's a Floo Torch and a non-solid block there, or two Floo Torches
                                torchCoords.add(nextPos);
                            } else if (posScore == 2) {
                                // There are two non-solid blocks here
                                airCoords.add(nextPos);
                            }
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
                    if (player.getVehicle() != null) {
                        player.stopRiding();
                    }
                    player.connection.teleport(x, y, z, player.getYRot(), player.getXRot());
                    player.fallDistance = 0.0F;
                    level.playSound(null, chosenPos, FloocraftSounds.FLICK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            } catch (IOException ignored) {
            }
        });
        context.setPacketHandled(true);
    }

    private int getPosScore(BlockState blockState) {
        Block flooTorchBlock = FloocraftBlocks.FLOO_TORCH.get();
        if (blockState.getBlock() == flooTorchBlock) {
            return 3;
        } else if (blockState.canBeReplaced()) {
            return 1;
        }
        return 0;
    }
}
