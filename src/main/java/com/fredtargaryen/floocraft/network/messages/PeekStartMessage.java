package com.fredtargaryen.floocraft.network.messages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.nio.charset.Charset;

public class PeekStartMessage {
    public int initX, initY, initZ;
    public String dest;
    private static final Charset defaultCharset = Charset.defaultCharset();

    public void handle(CustomPayloadEvent.Context context) {
//        context.enqueueWork(() -> {
//            int initX = this.initX;
//            int initY = this.initY;
//            int initZ = this.initZ;
//            ServerPlayerEntity player = context.getSender();
//            World world = player.world;
//            Block initBlock = world.getBlockState(new BlockPos(initX, initY, initZ)).getBlock();
//            int[] destCoords = FloocraftWorldData.forWorld(world).placeList.get(this.dest);
//            //Stop everything if the destination has the same coordinates as where the player is
//            if(!(destCoords[0] == this.initX && destCoords[1] == this.initY && destCoords[2] == this.initZ)) {
//                Block greenBusy = FloocraftBase.GREEN_FLAMES_BUSY.get();
//                int destX = destCoords[0];
//                int destY = destCoords[1];
//                int destZ = destCoords[2];
//                //Checks whether the player is currently in busy or idle green flames
//                ITagCollection<Block> blockTags = BlockTags.getCollection();
//                if (initBlock.isIn(blockTags.get(DataReference.VALID_DEPARTURE_BLOCKS))) {
//                    BlockPos dest = new BlockPos(destX, destY, destZ);
//                    Block destBlock = world.getBlockState(dest).getBlock();
//                    //Checks whether the destination is fire
//                    if (destBlock.isIn(blockTags.get(DataReference.VALID_ARRIVAL_BLOCKS))) {
//                        Direction direction = ((FlooFlamesBase) FloocraftBase.GREEN_FLAMES_TEMP.get()).isInFireplace(world, dest);
//                        if (direction != null) {
//                            Direction.Axis axis = direction.getAxis();
//                            if (axis == Direction.Axis.X || axis == Direction.Axis.Z) {
//                                //Create peeker
//                                PeekerEntity peeker = new PeekerEntity(world);
//                                peeker.setPeekerData(player, dest, direction);
//                                world.addEntity(peeker);
//                                //Create message
//                                MessageStartPeek msp = new MessageStartPeek(peeker.getUniqueID());
//                                MessageHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msp);
//                            }
//                        }
//                    }
//                }
//            }
//        });
        context.setPacketHandled(true);
    }

    public PeekStartMessage() {
    }

    public PeekStartMessage(FriendlyByteBuf buf) {
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