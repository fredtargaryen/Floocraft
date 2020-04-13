package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.fredtargaryen.floocraft.network.MessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class MessagePlayerIDRequest {
    public UUID peekerUUID;

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity spe = ctx.get().getSender();
            PeekerEntity pe = (PeekerEntity) spe.getServerWorld().getEntityByUuid(this.peekerUUID);
            MessagePlayerID mpID = new MessagePlayerID(this.peekerUUID, pe.getPlayerUUID());
            MessageHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> spe), mpID);
        });
        ctx.get().setPacketHandled(true);
    }

    public MessagePlayerIDRequest() {}

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessagePlayerIDRequest(ByteBuf buf) {
        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.peekerUUID.getMostSignificantBits());
        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
    }
}
