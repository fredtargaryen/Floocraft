package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Begins the flashing and dizziness effect when received, if the request to teleport was accepted.
 * Direction: server to client
 *
 * @param accepted Whether the teleport is allowed. Triggers the flash and dizzy effects on the client if enabled
 * @param soul Whether to do a magenta soul flash or a green normal flash
 */
public record TeleportResponseMessage(Boolean accepted, Boolean soul) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TeleportResponseMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("flash"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, TeleportResponseMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, TeleportResponseMessage::accepted,
                    ByteBufCodecs.BOOL, TeleportResponseMessage::soul,
                    TeleportResponseMessage::new);

    public static void handle(final TeleportResponseMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> FloocraftBase.ClientModEvents.handleMessage(message));
    }
}
