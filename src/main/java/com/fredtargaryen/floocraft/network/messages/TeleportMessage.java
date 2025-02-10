package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.block.FlooFlames;
import com.fredtargaryen.floocraft.block.FlooMainTeleporterBase;
import com.fredtargaryen.floocraft.config.CommonConfig;
import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import com.fredtargaryen.floocraft.network.MessageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.nio.charset.Charset;

import static com.fredtargaryen.floocraft.block.FlooFlames.BEHAVIOUR;
import static com.fredtargaryen.floocraft.block.FlooFlames.TEMP;
import static com.fredtargaryen.floocraft.block.FlooMainTeleporterBase.*;
import static net.minecraft.world.level.block.CampfireBlock.FACING;
import static net.minecraft.world.level.block.CampfireBlock.WATERLOGGED;

/**
 * Teleports player that sent this to (destX, destY, destZ) if departure and arrival points are valid
 * Direction: client to server
 */
public class TeleportMessage {
    public int initX, initY, initZ;
    public String dest;
    private static final Charset defaultCharset = Charset.defaultCharset();

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            //Determine whether the destination is valid
            ServerPlayer player = context.getSender();
            ServerLevel level = (ServerLevel) player.level();
            //The coordinates of the destination: [x, y, z]
            int[] destCoords = FloocraftLevelData.getForLevel(level).placeList.get(this.dest);
            //Stop everything if the destination has the same coordinates as where the player is
            if (destCoords[0] != this.initX || destCoords[1] != this.initY || destCoords[2] != this.initZ) return;
            //Checks whether the destination has a block that can be arrived in, and is in a valid fireplace
            BlockPos destBlockPos = new BlockPos(destCoords[0], destCoords[1], destCoords[2]);
            BlockState destBlockState = level.getBlockState(destBlockPos);
            boolean validDest = false;
            FlooFlames flooFlames = (FlooFlames) FloocraftBlocks.FLOO_FLAMES.get();
            if (destBlockState.is(FloocraftBlocks.ARRIVAL_BLOCKS)) {
                validDest = flooFlames.isInFireplace(level, destBlockPos) != null;
            }
            if (!validDest) return;

            // Determine whether block to depart from is valid
            BlockPos initBlockPos = new BlockPos(this.initX, this.initY, this.initZ);
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
            MessageHandler.sendToPlayer(new TeleportFlashMessage(initSoul), player);
            if (player.getVehicle() != null) {
                player.stopRiding();
            }
            player.connection.teleport(destCoords[0] + 0.5D, destCoords[1], destCoords[2] + 0.5D, player.getRandom().nextFloat() * 360, player.getXRot());
            player.fallDistance = 0.0F;

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
        context.setPacketHandled(true);
    }

    public TeleportMessage() {
    }

    public TeleportMessage(FriendlyByteBuf buf) {
        this.initX = buf.readInt();
        this.initY = buf.readInt();
        this.initZ = buf.readInt();
        this.dest = buf.readBytes(buf.readInt()).toString(defaultCharset);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(initX);
        buf.writeInt(initY);
        buf.writeInt(initZ);
        buf.writeInt(dest.length());
        buf.writeBytes(dest.getBytes());
    }
}