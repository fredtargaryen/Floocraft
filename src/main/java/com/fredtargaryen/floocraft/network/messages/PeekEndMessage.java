//package com.fredtargaryen.floocraft.network.messages;
//
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraftforge.event.network.CustomPayloadEvent;
//
//import java.util.UUID;
//
//public class PeekEndMessage {
//    public UUID peekerUUID;
//
//    public void handle(CustomPayloadEvent.Context context) {
////        context.enqueueWork(() -> {
////            ServerWorld sw = context.getSender().getServerWorld();
////            if(this.peekerUUID != null) {
////                PeekerEntity pe = (PeekerEntity) sw.getEntityByUuid(this.peekerUUID);
////                if (pe != null) pe.remove();
////            }
////        });
//        context.setPacketHandled(true);
//    }
//
//    public PeekEndMessage() {
//    }
//
//    /**
//     * Effectively fromBytes from 1.12.2
//     */
//    public PeekEndMessage(FriendlyByteBuf buf) {
//        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
//    }
//
//    public void encode(FriendlyByteBuf buf) {
//        buf.writeLong(this.peekerUUID.getMostSignificantBits());
//        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
//    }
//}
