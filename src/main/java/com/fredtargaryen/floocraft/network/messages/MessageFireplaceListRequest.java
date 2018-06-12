package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
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
		final EntityPlayerMP player = ctx.getServerHandler().player;
		final IThreadListener serverListener = player.getServerWorld();
		serverListener.addScheduledTask(() -> {
			World w = (World) serverListener;
			MessageFireplaceList mfl = FloocraftWorldData.forWorld(w).assembleNewFireplaceList(w);
			PacketHandler.INSTANCE.sendTo(mfl, player);
		});
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
