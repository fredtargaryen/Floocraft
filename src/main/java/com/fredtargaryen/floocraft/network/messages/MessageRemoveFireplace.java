package com.fredtargaryen.floocraft.network.messages;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;

public class MessageRemoveFireplace implements IMessage, IMessageHandler<MessageRemoveFireplace, IMessage>
{
	public int x, y, z;
	@Override
	public IMessage onMessage(MessageRemoveFireplace message, MessageContext ctx)
	{
		FloocraftWorldData.forWorld(ctx.getServerHandler().playerEntity.worldObj).removeLocation(x, y, z);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
}