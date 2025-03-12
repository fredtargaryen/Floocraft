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

import java.util.UUID;

public record PeekEndMessage(Long peekerMsb, Long peekerLsb) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PeekEndMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("peek_end"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, PeekEndMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, PeekEndMessage::peekerMsb,
                    ByteBufCodecs.VAR_LONG, PeekEndMessage::peekerLsb,
                    PeekEndMessage::new
            );

    public static void handle(final PeekEndMessage message, final IPayloadContext context) {
        ServerLevel level = ((ServerPlayer) context.player()).serverLevel();
        UUID peekerUUID = new UUID(message.peekerMsb(), message.peekerLsb());
        PeekerEntity pe = (PeekerEntity) level.getEntity(peekerUUID);
        if (pe != null) pe.remove(Entity.RemovalReason.DISCARDED);
    }
}
