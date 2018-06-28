package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessagePlayerID implements IMessage, IMessageHandler<MessagePlayerID, IMessage> {
    public UUID peekerUUID;
    public UUID playerUUID;

    @Override
    public IMessage onMessage(final MessagePlayerID message, MessageContext ctx) {
        final IThreadListener clientThread = Minecraft.getMinecraft();
        clientThread.addScheduledTask(() -> {
            FloocraftBase.proxy.setUUIDs(message);
        });
        return null;
    }

    @Override
	public void fromBytes(ByteBuf buf) {
        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
        this.playerUUID = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
	public void toBytes(ByteBuf buf) {
        buf.writeLong(this.peekerUUID.getMostSignificantBits());
        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
        buf.writeLong(this.playerUUID.getMostSignificantBits());
        buf.writeLong(this.playerUUID.getLeastSignificantBits());
    }
}