package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class MessageFireplaceListRequest implements IMessage, IMessageHandler<MessageFireplaceListRequest, IMessage>
{
	@Override
	public IMessage onMessage(MessageFireplaceListRequest message, MessageContext ctx)
	{
		World w = ctx.getServerHandler().playerEntity.worldObj;
		return FloocraftWorldData.forWorld(w).assembleNewFireplaceList(w);
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