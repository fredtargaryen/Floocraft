package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class MessageApproveName
{
	public String name;
	private static final Charset defaultCharset = Charset.defaultCharset();
	
	@Override
	public void onMessage(Supplier<NetworkEvent.Context> ctx)
	{
		final EntityPlayerMP player = ctx.getServerHandler().player;
		final IThreadListener serverListener = player.getServerWorld();
		serverListener.addScheduledTask(() -> {
			World w = (World) serverListener;
			MessageApproval ma = new MessageApproval();
			ma.answer = !FloocraftWorldData.forWorld(w).placeList.containsKey(message.name);
			PacketHandler.INSTANCE.sendTo(ma, player);
		});
		return null;
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
