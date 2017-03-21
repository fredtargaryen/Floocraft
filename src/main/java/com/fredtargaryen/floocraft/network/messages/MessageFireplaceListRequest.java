package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class MessageFireplaceListRequest implements IMessage, IMessageHandler<MessageFireplaceListRequest, IMessage>
{
	@Override
	public IMessage onMessage(MessageFireplaceListRequest message, MessageContext ctx)
	{
		World w = ctx.getServerHandler().playerEntity.getServerWorld();
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
