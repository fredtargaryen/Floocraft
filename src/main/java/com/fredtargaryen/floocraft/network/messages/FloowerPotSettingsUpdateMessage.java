package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBlockEntityTypes;
import com.fredtargaryen.floocraft.blockentity.FloowerPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FloowerPotSettingsUpdateMessage(Boolean rangeToChange, Integer amount,
                                              BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FloowerPotSettingsUpdateMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("floower_pot_settings_update"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, FloowerPotSettingsUpdateMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    FloowerPotSettingsUpdateMessage::rangeToChange,
                    ByteBufCodecs.INT,
                    FloowerPotSettingsUpdateMessage::amount,
                    BlockPos.STREAM_CODEC,
                    FloowerPotSettingsUpdateMessage::pos,
                    FloowerPotSettingsUpdateMessage::new
            );

    public static void handle(final FloowerPotSettingsUpdateMessage message, final IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        ServerLevel level = sender.serverLevel();
        context.enqueueWork(() -> {
            BlockEntity be = level.getBlockEntity(message.pos());
            if(be != null && be.getType() == FloocraftBlockEntityTypes.FLOOWER_POT.get())
                ((FloowerPotBlockEntity) be).adjustPotRange(message.rangeToChange(), message.amount());
        });
    }
}
