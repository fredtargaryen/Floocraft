package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageFireplaceList implements IMessage, IMessageHandler<MessageFireplaceList, IMessage>
{
	public Object[] places;
	public boolean[] enabledList;
	private static final Charset defaultCharset = Charset.defaultCharset();
	
	@Override
	public IMessage onMessage(final MessageFireplaceList message, MessageContext ctx)
	{
		final IThreadListener clientListener = Minecraft.getMinecraft();
		clientListener.addScheduledTask(() -> {
            GuiScreen s = ((Minecraft)clientListener).currentScreen;
            if(s instanceof GuiTeleport)
            {
                ((GuiTeleport) s).onFireplaceList(message);
            }
        });
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.places = new Object[]{};
		this.enabledList = new boolean[]{};
		int y = buf.readInt();
		if(y > 0)
		{
			this.places = new Object[y];
			this.enabledList = new boolean[y];
			for(int x = 0; x < y; ++x)
			{
				this.places[x] = buf.readBytes(buf.readInt()).toString(defaultCharset);
				this.enabledList[x] = buf.readBoolean();
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		int y = this.places.length;
		buf.writeInt(y);
		int keyCount = 0;
		for(Object o : this.places)
		{
			String s = (String) o;
			buf.writeInt(s.length());
	        buf.writeBytes(s.getBytes());
			buf.writeBoolean(this.enabledList[keyCount]);
			++keyCount;
		}
	}
}
