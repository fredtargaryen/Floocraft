//package com.fredtargaryen.floocraft.network.messages;
//
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraftforge.event.network.CustomPayloadEvent;
//
//import java.util.UUID;
//
//public class MessageStartPeek {
//    public UUID peekerUUID;
//
//    public void handle(CustomPayloadEvent.Context context) {
//        //context.enqueueWork(() -> FloocraftBase.proxy.onMessage(this));
//        context.setPacketHandled(true);
//    }
//
//    public MessageStartPeek(UUID peekerUUID) {
//        this.peekerUUID = peekerUUID;
//    }
//
//    /**
//     * Effectively fromBytes from 1.12.2
//     */
//    public MessageStartPeek(FriendlyByteBuf buf) {
//        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
//    }
//
//    public void encode(FriendlyByteBuf buf) {
//        buf.writeLong(this.peekerUUID.getMostSignificantBits());
//        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
//    }
//}
