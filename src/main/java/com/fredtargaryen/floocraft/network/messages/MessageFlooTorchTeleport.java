package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MessageFlooTorchTeleport {
    public int torchX, torchY, torchZ;

    /**
     * Calculate whether two BlockPoss are 1 block to the north, south, east or west of each other
     * @param pos1 A BlockPos
     * @param pos2 Another BlockPos
     * @return Whether pos1 and pos2 differ on the x axis by 1 XOR on the z axis by 1
     */
    private static boolean isAdjacent(BlockPos pos1, BlockPos pos2) {
        int xDiff = Math.abs(pos1.getX() - pos2.getX());
        int zDiff = Math.abs(pos1.getZ() - pos2.getZ());
        return (xDiff == 0 && zDiff == 1) || xDiff == 1 && zDiff == 0;
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            int torchX1 = this.torchX;
            int torchY1 = this.torchY;
            int torchZ1 = this.torchZ;
            ServerPlayerEntity player = ctx.get().getSender();
            World world = player.getServerWorld();
            int minx = torchX1 - 3;
            int maxx = torchX1 + 3;
            int minz = torchZ1 - 3;
            int maxz = torchZ1 + 3;
            List<BlockPos> coords = new ArrayList<>();
            List<BlockPos> torchCoords = new ArrayList<>();
            for (int x = minx; x <= maxx; x++) {
                for (int z = minz; z <= maxz; z++) {
                    //Prevent the player from teleporting onto the torch again
                    if(x != torchX1 && z != torchZ1) {
                        BlockPos nextPos = new BlockPos(x, torchY1, z);
                        if(world.isAirBlock(nextPos.up())) {
                            //There is enough headroom for the player to teleport here
                            if (world.isAirBlock(nextPos)) {
                                //There is enough legroom
                                coords.add(nextPos);
                            } else if (world.getBlockState(nextPos).getBlock() == FloocraftBase.BLOCK_FLOO_TORCH) {
                                //There is a Floo Torch nearby
                                torchCoords.add(nextPos);
                            }
                        }
                    }
                }
            }
            //Shortlist of places to go. A random place will be picked from here.
            List<BlockPos> finalCoords = new ArrayList<>();
            if(torchCoords.isEmpty()) {
                //There are no nearby torches so just pick any available position
                finalCoords = coords;
            }
            else {
                //Get all empty positions next to the torch positions
                for(BlockPos torchPos : torchCoords) {
                    for(BlockPos emptyPos : coords) {
                        if(isAdjacent(torchPos, emptyPos)) {
                            finalCoords.add(emptyPos);
                        }
                    }
                }
                //If there are none, just go to any empty position
                if(finalCoords.isEmpty()) {
                    finalCoords = coords;
                }
            }
            if(finalCoords.size() > 0)
            {
                BlockPos chosenCoord = finalCoords.get(world.rand.nextInt(finalCoords.size()));
//                double x = chosenCoord.getX();
//                if(x < torchX1)
//                {
//                    x += 0.5;
//                }
//                else if(x > torchX1) {
//                    x -= 0.5;
//                }
                double x = chosenCoord.getX() + 0.5;
                double y = chosenCoord.getY();
                double z = chosenCoord.getZ() + 0.5;
//                double z = chosenCoord.getZ();
//                if(z < torchZ1)
//                {
//                    z += 0.5;
//                }
//                else if(z > torchZ1)
//                {
//                    z -= 0.5;
//                }
                if(player.getRidingEntity() != null) {
                    player.stopRiding();
                }
                player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
                player.fallDistance = 0.0F;
                world.playSound(null, new BlockPos(torchX1, torchY1, torchZ1), FloocraftBase.FLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        });

        ctx.get().setPacketHandled(true);
    }

    public MessageFlooTorchTeleport() {}

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageFlooTorchTeleport(ByteBuf buf) {
        this.torchX = buf.readInt();
        this.torchY = buf.readInt();
        this.torchZ = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.torchX);
        buf.writeInt(this.torchY);
        buf.writeInt(this.torchZ);
    }
}
