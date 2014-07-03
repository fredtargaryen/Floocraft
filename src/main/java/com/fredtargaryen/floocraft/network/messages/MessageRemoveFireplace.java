package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageRemoveFireplace implements IMessage, IMessageHandler<MessageRemoveFireplace, IMessage>
{
	public int x, y, z;
	@Override
	public IMessage onMessage(MessageRemoveFireplace message, MessageContext ctx)
	{
		FloocraftWorldData.forWorld(ctx.getServerHandler().playerEntity.worldObj).removeLocation(message.x, message.y, message.z);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
	}
}