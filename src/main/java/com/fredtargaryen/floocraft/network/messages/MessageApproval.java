package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiFlooSign;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class MessageApproval implements IMessage, IMessageHandler<MessageApproval, IMessage>
{
	public boolean answer;
	
	@Override
	public IMessage onMessage(final MessageApproval message, MessageContext ctx)
	{
		final IThreadListener clientThread = Minecraft.getMinecraft();
		clientThread.addScheduledTask(() -> {
            GuiScreen s = ((Minecraft)clientThread).currentScreen;
            if(s instanceof GuiFlooSign)
            {
                ((GuiFlooSign) s).dealWithAnswer(message.answer);
            }
        });
        return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.answer = buf.getBoolean(0);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(this.answer);
	}
}
