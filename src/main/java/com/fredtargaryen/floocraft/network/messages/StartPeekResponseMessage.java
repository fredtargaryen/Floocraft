package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record StartPeekResponseMessage(UUID uuid) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StartPeekResponseMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("start_peek_response"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, StartPeekResponseMessage> STREAM_CODEC =
            StreamCodec.unit(new StartPeekResponseMessage(UUID.randomUUID()));
    //StreamCodec.composite(
    //ByteBufCodecs.UUID, StartPeekResponseMessage::uuid,
    //StartPeekResponseMessage::new);

    public static void handle(final StartPeekResponseMessage message, final IPayloadContext context) {
        //context.enqueueWork(() -> FloocraftBase.proxy.onMessage(this));
    }
}
