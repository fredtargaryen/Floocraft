package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * Sent when a Floo Sign is placed and opens the sign edit GUI
 * Direction: server to client
 *
 * @param signPos The position of the sign
 */
public record OpenFlooSignEditScreenMessage(BlockPos signPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenFlooSignEditScreenMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("open_floo_sign_edit_screen"));

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, OpenFlooSignEditScreenMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, OpenFlooSignEditScreenMessage::signPos,
                    OpenFlooSignEditScreenMessage::new);

    public static void handle(final OpenFlooSignEditScreenMessage message, final IPayloadContext context) {
        context.enqueueWork(() -> FloocraftBase.ClientModEvents.handleMessage(message));
    }

    public BlockPos getSignPos() { return signPos; }
}
