package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.HelperFunctions;
import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Send the text to set on a Floo Sign.
 * If just using the sign for decoration it will always be set, but the sign won't be added to the Floo Network.
 * If actually connecting, the sign will only be approved if it doesn't share a name with any other sign in the Level.
 * If approved, the text will be set and the sign will be added to the Floo Network.
 * If not approved, a rejection will be sent to the client and the text won't be set.
 * Direction: client to server
 * @param signPos The position of the Floo sign
 * @param attemptingToConnect Whether the sign is being named with the intention of connecting it to the Floo Network
 * @param name The sign's name as a list of the values of the 4 editable lines in the GUI
 */
public record FlooSignNameRequestMessage(BlockPos signPos, Boolean attemptingToConnect, List<String> name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FlooSignNameRequestMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("floo_sign_name_request"));

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, FlooSignNameRequestMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, FlooSignNameRequestMessage::signPos,
                    ByteBufCodecs.BOOL, FlooSignNameRequestMessage::attemptingToConnect,
                    ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), FlooSignNameRequestMessage::name,
                    FlooSignNameRequestMessage::new
            );

    public static void handle(final FlooSignNameRequestMessage message, final IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        ServerLevel level = sender.serverLevel();
        context.enqueueWork(() -> {
            FlooSignBlockEntity fsbe = (FlooSignBlockEntity) (level.getBlockEntity(message.signPos));
            if (fsbe != null && !fsbe.getConnected()) {
                if (message.attemptingToConnect) {
                    String locationName = HelperFunctions.convertArrayToLocationName(message.name);
                    boolean approved = !locationName.isEmpty()
                            && !FloocraftLevelData.getForLevel(level).placeList.containsKey(locationName);
                    FlooSignNameResponseMessage fsnrm = new FlooSignNameResponseMessage(approved);
                    context.reply(fsnrm);
                    if (approved) {
                        fsbe.addLocation(message.name);
                        fsbe.setConnected(true);
                        fsbe.markUpdate();
                    } else {
                        fsbe.setConnected(false);
                    }
                } else {
                    fsbe.setNameOnSign(message.name);
                    fsbe.setConnected(false);
                    fsbe.markUpdate();
                }
            }
        });
    }
}
