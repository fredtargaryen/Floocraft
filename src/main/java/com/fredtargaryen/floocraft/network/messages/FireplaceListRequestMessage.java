package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import com.fredtargaryen.floocraft.network.MessageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Requests a list of the fireplaces in the current Level.
 * Direction: client to server
 */
public class FireplaceListRequestMessage {
    public FireplaceListRequestMessage() {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public FireplaceListRequestMessage(FriendlyByteBuf buf) {
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            try (ServerLevel level = Objects.requireNonNull(sender).serverLevel()) {
                FireplaceListResponseMessage mfl = FloocraftLevelData.getForLevel(level).assembleNewFireplaceList(level, Optional.of(sender.blockPosition()));
                MessageHandler.sendToPlayer(mfl, context.getSender());
            } catch (IOException ignored) {}
        });
        context.setPacketHandled(true);
    }
}
