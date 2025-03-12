package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.entity.PeekerEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record PeekerInfoRequestMessage(Long peekerMsb, Long peekerLsb) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PeekerInfoRequestMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("peeker_info_request"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, PeekerInfoRequestMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, PeekerInfoRequestMessage::peekerMsb,
                    ByteBufCodecs.VAR_LONG, PeekerInfoRequestMessage::peekerLsb,
                    PeekerInfoRequestMessage::new
            );

    public static void handle(final PeekerInfoRequestMessage message, final IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        PeekerEntity pe = (PeekerEntity) sender.serverLevel().getEntity(new UUID(message.peekerMsb(), message.peekerLsb()));
        UUID playerUUID = pe.getPlayerUUID();
        PeekerInfoResponseMessage reply = new PeekerInfoResponseMessage(
                message.peekerMsb(), message.peekerLsb(),
                playerUUID.getMostSignificantBits(), playerUUID.getLeastSignificantBits());
        context.reply(reply);
    }
}
