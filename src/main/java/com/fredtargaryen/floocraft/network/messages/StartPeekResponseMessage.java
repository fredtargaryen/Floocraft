package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StartPeekResponseMessage(Boolean accepted, Long peekerMsb, Long peekerLsb) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StartPeekResponseMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("start_peek_response"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, StartPeekResponseMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, StartPeekResponseMessage::accepted,
                    ByteBufCodecs.VAR_LONG, StartPeekResponseMessage::peekerMsb,
                    ByteBufCodecs.VAR_LONG, StartPeekResponseMessage::peekerLsb,
                    StartPeekResponseMessage::new);

    public static void handle(final StartPeekResponseMessage message, final IPayloadContext context) {
        FloocraftBase.ClientModEvents.handleMessage(message);
    }
}
