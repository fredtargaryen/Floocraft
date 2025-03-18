package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Describes whether the location name on the Floo Sign will be added to the Floo Network.
 * Should only be sent when the user pressed the Connect to Network button in @link{FlooSignEditScreen},
 * sending a @link{FlooSignNameRequestMessage}.
 * Direction: server to client
 * @param answer Whether or not the name was valid and therefore added to the Network
 */
public record FlooSignNameResponseMessage(Boolean answer) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FlooSignNameResponseMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("floo_sign_name_response"));

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, FlooSignNameResponseMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, FlooSignNameResponseMessage::answer,
                    FlooSignNameResponseMessage::new);

    public static void handle(final FlooSignNameResponseMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> FloocraftBase.ClientModEvents.handleMessage(message));
    }
}
