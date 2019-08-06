package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.PeekerEntity;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageEndPeek {
    public UUID peekerUUID;

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PeekerEntity ep = (PeekerEntity) FloocraftBase.getEntityWithUUID(ctx.get().getSender().world, this.peekerUUID);
            if (ep != null) ep.remove();
        });
        ctx.get().setPacketHandled(true);
    }

    public MessageEndPeek(){}

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageEndPeek(ByteBuf buf) {
        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.peekerUUID.getMostSignificantBits());
        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
    }
}
