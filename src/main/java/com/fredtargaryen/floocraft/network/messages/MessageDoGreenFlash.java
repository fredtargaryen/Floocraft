package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDoGreenFlash
{
	@Override
	public void onMessage(Supplier<NetworkEvent.Context> ctx)
	{
		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(() -> ((ClientProxy) FloocraftBase.proxy).flash.start());
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
