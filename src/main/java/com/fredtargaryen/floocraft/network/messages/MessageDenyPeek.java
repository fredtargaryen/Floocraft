package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDenyPeek implements IMessage, IMessageHandler<MessageDenyPeek, IMessage> {
    @Override
    public IMessage onMessage(final MessageDenyPeek message, MessageContext ctx) {
        final IThreadListener clientThread = Minecraft.getMinecraft();
        clientThread.addScheduledTask(() -> {
            GuiScreen s = ((Minecraft)clientThread).currentScreen;
            if(s instanceof GuiTeleport) {
                ((GuiTeleport) s).onPeekDenied(message);
            }
        });
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) { }

    @Override
    public void toBytes(ByteBuf buf) { }
}
