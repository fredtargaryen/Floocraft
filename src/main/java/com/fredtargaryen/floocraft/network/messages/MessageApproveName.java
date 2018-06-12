package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageApproveName implements IMessage, IMessageHandler<MessageApproveName, IMessage>
{
	public String name;
	
	@Override
	public IMessage onMessage(final MessageApproveName message, MessageContext ctx)
	{
		final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		final IThreadListener serverListener = player.getServerWorld();
		serverListener.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				World w = (World) serverListener;
				MessageApproval ma = new MessageApproval();
				ma.answer = !FloocraftWorldData.forWorld(w).placeList.containsKey(message.name);
				PacketHandler.INSTANCE.sendTo(ma, player);
			}
		});
		return null;
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
