package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiFlooSign;
import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class MessageApproval implements IMessage, IMessageHandler<MessageApproval, IMessage>
{
	public boolean answer;
	
	@Override
	public IMessage onMessage(MessageApproval message, MessageContext ctx)
	{
        GuiScreen s = Minecraft.getMinecraft().currentScreen;
        if(s instanceof GuiFlooSign)
        {
            ((GuiFlooSign) s).dealWithAnswer(message.answer);
        }
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
