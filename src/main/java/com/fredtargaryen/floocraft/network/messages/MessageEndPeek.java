package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageEndPeek implements IMessage, IMessageHandler<MessageEndPeek, IMessage> {
    public UUID peekerUUID;

    @Override
    public IMessage onMessage(final MessageEndPeek message, MessageContext ctx) {
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
