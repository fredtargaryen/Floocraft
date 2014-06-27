package com.fredtargaryen.floocraft.network.messages;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;

public class MessageAddFireplace implements IMessage, IMessageHandler<MessageAddFireplace, IMessage>
{
	public String name;
	public int x, y, z;
	
	public MessageAddFireplace(){}
	
	@Override
	public IMessage onMessage(MessageAddFireplace message, MessageContext ctx)
	{
		FloocraftWorldData.forWorld(ctx.getServerHandler().playerEntity.worldObj).addLocation(name, x, y, z);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int nameLength = buf.readInt();
        this.name = new String(buf.readBytes(nameLength).array());
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(name.length());
        buf.writeBytes(name.getBytes());
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
}
