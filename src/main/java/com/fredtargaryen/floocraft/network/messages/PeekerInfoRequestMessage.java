//package com.fredtargaryen.floocraft.network.messages;
//
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraftforge.event.network.CustomPayloadEvent;
//
//import java.util.UUID;
//
//public class PeekerInfoRequestMessage {
//    public UUID peekerUUID;
//
//    public void handle(CustomPayloadEvent.Context context) {
////        context.enqueueWork(() -> {
////            ServerPlayerEntity spe = context.getSender();
////            PeekerEntity pe = (PeekerEntity) spe.getServerWorld().getEntityByUuid(this.peekerUUID);
////            PeekerInfoResponseMessage mpID = new PeekerInfoResponseMessage(this.peekerUUID, pe.getPlayerUUID());
////            MessageHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> spe), mpID);
////        });
//        context.setPacketHandled(true);
//    }
//
//    public PeekerInfoRequestMessage() {
//    }
//
//    /**
//     * Effectively fromBytes from 1.12.2
//     */
//    public PeekerInfoRequestMessage(FriendlyByteBuf buf) {
//        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
//    }
//
//    public void encode(FriendlyByteBuf buf) {
//        buf.writeLong(this.peekerUUID.getMostSignificantBits());
//        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
//    }
//}
