package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class MessageApproveName implements IMessage, IMessageHandler<MessageApproveName, IMessage>
{
	public String name;
	private static final Charset defaultCharset = Charset.defaultCharset();
	
	@Override
	public IMessage onMessage(MessageApproveName message, MessageContext ctx)
	{
        MessageApproval m = new MessageApproval();
        m.answer = !FloocraftWorldData.forWorld(ctx.getServerHandler().player.getServerWorld()).placeList.containsKey(message.name);
		return m;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int nameLength = buf.readInt();
        this.name = buf.readBytes(nameLength).toString(defaultCharset);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(name.length());
        buf.writeBytes(name.getBytes());
	}
}
