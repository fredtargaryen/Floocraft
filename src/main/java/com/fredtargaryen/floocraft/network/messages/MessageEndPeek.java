package com.fredtargaryen.floocraft.network.messages;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageEndPeek {
    public UUID peekerUUID;

    @Override
    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        final IThreadListener serverListener = player.getServerWorld();
        serverListener.addScheduledTask(() -> {
            EntityPeeker ep = (EntityPeeker) FloocraftBase.getEntityWithUUID((World) serverListener, message.peekerUUID);
            if (ep != null) ep.setDead();
        });
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.peekerUUID.getMostSignificantBits());
        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
    }
}
