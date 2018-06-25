package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageStartPeek implements IMessage, IMessageHandler<MessageStartPeek, IMessage> {
    public UUID peekerUUID;

    @Override
    public IMessage onMessage(final MessageStartPeek message, MessageContext ctx)
    {
        final IThreadListener clientThread = Minecraft.getMinecraft();
        clientThread.addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                GuiScreen s = ((Minecraft)clientThread).currentScreen;
                if(s instanceof GuiTeleport)
                {
                    ((GuiTeleport) s).onStartPeek(message);
                }
            }
        });
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(this.peekerUUID.getMostSignificantBits());
        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
    }
}
