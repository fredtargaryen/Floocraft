package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageApproveName implements IMessage, IMessageHandler<MessageApproveName, IMessage>
{
	public String name;
	
	@Override
	public IMessage onMessage(MessageApproveName message, MessageContext ctx)
	{
        MessageApproval m = new MessageApproval();
        m.answer = !FloocraftWorldData.forWorld(ctx.getServerHandler().playerEntity.worldObj).placenamelist.contains(message.name);
		return m;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int nameLength = buf.readInt();
        this.name = new String(buf.readBytes(nameLength).array());
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(name.length());
        buf.writeBytes(name.getBytes());
	}
}
