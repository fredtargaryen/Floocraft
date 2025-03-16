package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftEntityTypes;
import com.fredtargaryen.floocraft.block.FlooFlamesBlock;
import com.fredtargaryen.floocraft.block.FlooMainTeleporterBase;
import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/**
 * Sent to the server when the player presses the "Peek..." button.
 * Direction: client to server
 *
 * @param initPos The position of the block the player entered to open the teleport screen
 * @param dest The string name of the location the player is attempting to peek into
 */
public record StartPeekRequestMessage(BlockPos initPos, String dest) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StartPeekRequestMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("start_peek_request"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, StartPeekRequestMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, StartPeekRequestMessage::initPos,
                    ByteBufCodecs.STRING_UTF8, StartPeekRequestMessage::dest,
                    StartPeekRequestMessage::new);

    public static void handle(final StartPeekRequestMessage message, final IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        ServerLevel level = sender.serverLevel();
        context.enqueueWork(() -> {
            BlockState initState = level.getBlockState(message.initPos);
            Block initBlock = initState.getBlock();
            int[] destCoords = FloocraftLevelData.getForLevel(level).placeList.get(message.dest);
            //Stop everything if the destination has the same coordinates as where the player is
            if (destCoords[0] == message.initPos.getX() && destCoords[1] == message.initPos.getY() && destCoords[2] == message.initPos.getZ()) {
                context.reply(new StartPeekResponseMessage(false, 0));
                return;
            }
            int destX = destCoords[0];
            int destY = destCoords[1];
            int destZ = destCoords[2];

            // Determine whether block to depart from is valid
            if (!(initBlock instanceof FlooMainTeleporterBase)) {
                context.reply(new StartPeekResponseMessage(false, 0));
                return;
            }
            if (!((FlooMainTeleporterBase) initBlock).canDepartFromBlock(initState)) {
                context.reply(new StartPeekResponseMessage(false, 0));
                return;
            }

            BlockPos destPos = new BlockPos(destX, destY, destZ);
            BlockState destState = level.getBlockState(destPos);
            //Checks whether the destination is fire
            if (destState.is(BlockTags.FIRE) || destState.getBlock() instanceof FlooFlamesBlock) {
                Direction direction = FloocraftBlocks.FLOO_FLAMES.get().isInFireplace(level, destPos);
                if (direction != null) {
                    Direction.Axis axis = direction.getAxis();
                    if (axis.isHorizontal()) {
                        //Create peeker
                        PeekerEntity peeker = new PeekerEntity(FloocraftEntityTypes.PEEKER.get(), level);
                        peeker.setPeekerData(sender, destPos, direction);
                        level.addFreshEntity(peeker);
                        //Create response
                        UUID peekerUUID = peeker.getUUID();
                        StartPeekResponseMessage reply = new StartPeekResponseMessage(
                                true,
                                peeker.getId());
                        context.reply(reply);
                    }
                }
            }
        });
    }
}