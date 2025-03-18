package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.entity.PeekerEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent when the PeekScreen is closed.
 * Direction: client to server
 */
public record EndPeekMessage(Integer peekerNetworkId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<EndPeekMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("peek_end"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, EndPeekMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, EndPeekMessage::peekerNetworkId,
                    EndPeekMessage::new
            );

    public static void handle(final EndPeekMessage message, final IPayloadContext context) {
        ServerLevel level = ((ServerPlayer) context.player()).serverLevel();
        PeekerEntity pe = (PeekerEntity) level.getEntity(message.peekerNetworkId());
        if (pe != null) pe.remove(Entity.RemovalReason.DISCARDED);
    }
}
