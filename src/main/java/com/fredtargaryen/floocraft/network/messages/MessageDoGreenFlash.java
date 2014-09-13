package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageDoGreenFlash implements IMessage, IMessageHandler<MessageDoGreenFlash, IMessage>
{
	@Override
	public IMessage onMessage(MessageDoGreenFlash message, MessageContext ctx)
	{
        ((ClientProxy) FloocraftBase.proxy).flash.start();
        return null;
     }

	@Override
	public void fromBytes(ByteBuf buf)
	{
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
	}
}
