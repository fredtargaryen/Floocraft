package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PeekerInfoResponseMessage(Long peekerMsb, Long peekerLsb,
                                        Long playerMsb, Long playerLsb) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PeekerInfoResponseMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("peeker_info_response"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, PeekerInfoResponseMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, PeekerInfoResponseMessage::peekerMsb,
                    ByteBufCodecs.VAR_LONG, PeekerInfoResponseMessage::peekerLsb,
                    ByteBufCodecs.VAR_LONG, PeekerInfoResponseMessage::playerMsb,
                    ByteBufCodecs.VAR_LONG, PeekerInfoResponseMessage::playerLsb,
                    PeekerInfoResponseMessage::new
            );

    public static void handle(final PeekerInfoResponseMessage message, final IPayloadContext context) {
        FloocraftBase.ClientModEvents.handleMessage(message);
    }
}