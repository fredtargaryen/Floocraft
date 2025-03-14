package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftSounds;
import com.fredtargaryen.floocraft.block.FlooFlamesBlock;
import com.fredtargaryen.floocraft.block.FlooMainTeleporterBase;
import com.fredtargaryen.floocraft.config.CommonConfig;
import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import com.fredtargaryen.floocraft.network.MessageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.fredtargaryen.floocraft.block.FlooFlamesBlock.BEHAVIOUR;
import static com.fredtargaryen.floocraft.block.FlooFlamesBlock.TEMP;
import static com.fredtargaryen.floocraft.block.FlooMainTeleporterBase.*;
import static net.minecraft.world.level.block.CampfireBlock.FACING;
import static net.minecraft.world.level.block.CampfireBlock.WATERLOGGED;

/**
 * Teleports player that sent this to (destX, destY, destZ) if departure and arrival points are valid
 * Direction: client to server
 *
 * @param initPos Position of the block the player has stepped into
 * @param dest    The name of the fireplace the player wants to go to
 */
public record TeleportRequestMessage(BlockPos initPos, String dest) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TeleportRequestMessage> TYPE =
            new CustomPacketPayload.Type<>(DataReference.getResourceLocation("tp"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, TeleportRequestMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, TeleportRequestMessage::initPos,
                    ByteBufCodecs.STRING_UTF8, TeleportRequestMessage::dest,
                    TeleportRequestMessage::new);

    public static void handle(final TeleportRequestMessage message, final IPayloadContext context) {
        ServerPlayer sender = (ServerPlayer) context.player();
        ServerLevel level = sender.serverLevel();
        context.enqueueWork(() -> {
            //Determine whether the destination is valid
            //The coordinates of the destination: [x, y, z]
            int[] destCoords = FloocraftLevelData.getForLevel(level).placeList.get(message.dest);
            BlockPos initBlockPos = message.initPos;
            int initX = initBlockPos.getX();
            int initY = initBlockPos.getY();
            int initZ = initBlockPos.getZ();

            //Stop everything if the destination has the same coordinates as where the player is
            if (destCoords[0] == initX && destCoords[1] == initY && destCoords[2] == initZ)
                return;
            //Checks whether the destination has a block that can be arrived in, and is in a valid fireplace
            BlockPos destBlockPos = new BlockPos(destCoords[0], destCoords[1], destCoords[2]);
            BlockState destBlockState = level.getBlockState(destBlockPos);
            boolean validDest = false;
            FlooFlamesBlock flooFlames = FloocraftBlocks.FLOO_FLAMES.get();
            if (destBlockState.is(FloocraftBlocks.ARRIVAL_BLOCKS)) {
                validDest = flooFlames.isInFireplace(level, destBlockPos) != null;
            }
            if (!validDest) return;

            // Determine whether block to depart from is valid
            BlockState initBlockState = level.getBlockState(initBlockPos);
            Block initBlock = initBlockState.getBlock();
            if (!(initBlock instanceof FlooMainTeleporterBase)) return;
            if (!((FlooMainTeleporterBase) initBlock).canDepartFromBlock(initBlockState)) return;

            // Check if the player is teleporting from or to soul blocks
            boolean initSoul = initBlockState.getValue(COLOUR);
            boolean destSoul = SoulFireBlock.canSurviveOnBlock(level.getBlockState(destBlockPos.below()));

            // Get the fire ready. If it's a fire block, set a temporary Floo fire; otherwise leave as-is
            if (destBlockState.is(BlockTags.FIRE)) {
                level.setBlockAndUpdate(destBlockPos, flooFlames.defaultBlockState()
                        .setValue(COLOUR, destSoul)
                        .setValue(BEHAVIOUR, TEMP));
            }

            // Then do the teleport
            context.reply(new TeleportResponseMessage(true, initSoul));
            if (sender.getVehicle() != null) {
                sender.stopRiding();
            }
            sender.connection.teleport(destCoords[0] + 0.5D, destCoords[1], destCoords[2] + 0.5D, sender.getRandom().nextFloat() * 360, sender.getXRot());
            sender.fallDistance = 0.0F;
            level.playSound(null, destBlockPos, FloocraftSounds.TP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

            // Then update the age of the departure fire, if configured that way
            if (!CommonConfig.DEPLETE_FLOO) return;
            int tpsRemaining = initBlockState.getValue(TPS_REMAINING);
            if (tpsRemaining == 1) {
                // That was the last teleport
                if (initBlock == FloocraftBlocks.FLOO_CAMPFIRE.get()) {
                    level.setBlockAndUpdate(initBlockPos,
                            Blocks.CAMPFIRE.defaultBlockState()
                                    .setValue(FACING, initBlockState.getValue(FACING))
                                    .setValue(WATERLOGGED, initBlockState.getValue(WATERLOGGED)));
                } else if (initBlock == FloocraftBlocks.FLOO_SOUL_CAMPFIRE.get()) {
                    level.setBlockAndUpdate(initBlockPos,
                            Blocks.SOUL_CAMPFIRE.defaultBlockState()
                                    .setValue(FACING, initBlockState.getValue(FACING))
                                    .setValue(WATERLOGGED, initBlockState.getValue(WATERLOGGED)));
                } else {
                    level.setBlockAndUpdate(initBlockPos,
                            initSoul ? Blocks.SOUL_FIRE.defaultBlockState()
                                    : Blocks.FIRE.defaultBlockState());
                }
            } else if (tpsRemaining != INFINITE_TPS) {
                level.setBlockAndUpdate(initBlockPos, initBlockState
                        .setValue(TPS_REMAINING, initBlockState.getValue(TPS_REMAINING) - 1));
            }
        });
    }
}