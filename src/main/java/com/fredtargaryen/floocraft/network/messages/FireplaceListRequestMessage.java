package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Requests a list of the fireplaces in the current Level.
 * Direction: client to server
 * @param blockPos The position of the teleporter block. Not necessarily the same as the player position
 */
public record FireplaceListRequestMessage(BlockPos blockPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FireplaceListRequestMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("fireplace_list_request"));

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, FireplaceListRequestMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, FireplaceListRequestMessage::blockPos,
                    FireplaceListRequestMessage::new
            );

    public static void handle(final FireplaceListRequestMessage message, final IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        ServerLevel level = sender.serverLevel();
        context.enqueueWork(() -> {
            FireplaceListResponseMessage mfl = FloocraftLevelData.getForLevel(level).assembleNewFireplaceList(level, Optional.of(message.blockPos()));
            context.reply(mfl);
        }).exceptionally(e -> {
            context.disconnect(Component.literal(e.getMessage()));
            return null;
        });
    }
}
