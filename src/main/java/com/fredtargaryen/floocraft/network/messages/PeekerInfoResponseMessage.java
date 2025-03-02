//package com.fredtargaryen.floocraft.network.messages;
//
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraftforge.event.network.CustomPayloadEvent;
//
//import java.util.UUID;
//
//public class PeekerInfoResponseMessage {
//    public UUID peekerUUID;
//    public UUID playerUUID;
//
//    public void handle(CustomPayloadEvent.Context context) {
//        //context.enqueueWork(() -> FloocraftBase.proxy.setUUIDs(this));
//        context.setPacketHandled(true);
//    }
//
//    public PeekerInfoResponseMessage(UUID peekerUUID, UUID playerUUID) {
//        this.peekerUUID = peekerUUID;
//        this.playerUUID = playerUUID;
//    }
//
//    /**
//     * Effectively fromBytes from 1.12.2
//     */
//    public PeekerInfoResponseMessage(FriendlyByteBuf buf) {
//        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
//        this.playerUUID = new UUID(buf.readLong(), buf.readLong());
//    }
//
//    public void encode(FriendlyByteBuf buf) {
//        buf.writeLong(this.peekerUUID.getMostSignificantBits());
//        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
//        buf.writeLong(this.playerUUID.getMostSignificantBits());
//        buf.writeLong(this.playerUUID.getLeastSignificantBits());
//    }
//}