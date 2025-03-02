package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Begins the flashing and dizziness effect when received.
 * Direction: server to client
 *
 * @param soul Whether to do a magenta soul flash or a green normal flash
 */
public record TeleportFlashMessage(Boolean soul) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TeleportFlashMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("flash"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, TeleportFlashMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, TeleportFlashMessage::soul,
                    TeleportFlashMessage::new);

    public static void handle(final TeleportFlashMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> FloocraftBase.ClientModEvents.handleMessage(message));
    }
}
