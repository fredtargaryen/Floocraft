package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Sends a list of all fireplaces in the current Level to the requesting player.
 * Direction: server to client
 *
 * @param places            The list of place names in the Level
 * @param enabledList       Whether the place with the corresponding index in places can be teleported to
 * @param canPeekList       Whether the player can peek into this fire (i.e. the chunk is loaded for entity simulation)
 * @param playerPlaceIndex  The index of the fireplace the player is trying to teleport from. -1 if not teleporting from a fireplace connected to the Floo Network
 */
public record FireplaceListResponseMessage(List<String> places, List<Boolean> enabledList,
                                           List<Boolean> canPeekList, int playerPlaceIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FireplaceListResponseMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("fireplace_list_response"));

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, FireplaceListResponseMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), FireplaceListResponseMessage::places,
                    ByteBufCodecs.BOOL.apply(ByteBufCodecs.list()), FireplaceListResponseMessage::enabledList,
                    ByteBufCodecs.BOOL.apply(ByteBufCodecs.list()), FireplaceListResponseMessage::canPeekList,
                    ByteBufCodecs.INT, FireplaceListResponseMessage::playerPlaceIndex,
                    FireplaceListResponseMessage::new);

    public static void handle(final FireplaceListResponseMessage message, IPayloadContext context) {
        context.enqueueWork(() -> FloocraftBase.ClientModEvents.handleMessage(message));
    }
}
