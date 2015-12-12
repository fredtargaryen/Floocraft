package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageFireplaceList implements IMessage, IMessageHandler<MessageFireplaceList, IMessage>
{
	public HashMap<String, int[]> placeList;
	public List<Boolean> enabledList;
	
	@Override
	public IMessage onMessage(final MessageFireplaceList message, MessageContext ctx)
	{
		final IThreadListener clientListener = Minecraft.getMinecraft();
		clientListener.addScheduledTask(new Runnable() {
			@Override
			public void run()
			{
				GuiScreen s = ((Minecraft)clientListener).currentScreen;
				if(s instanceof GuiTeleport)
				{
					((GuiTeleport) s).onMessageReceived(message);
				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.placeList = new HashMap<String, int[]>();
		this.enabledList = new ArrayList<Boolean>();
		int y = buf.readInt();
		if(y > 0)
		{
			for(int x = 0; x < y; x++)
			{
				int nameLength = buf.readInt();
				String name = new String(buf.readBytes(nameLength).array());
				int[] coords = new int[]{buf.readInt(), buf.readInt(), buf.readInt()};
				this.placeList.put(name, coords);
				this.enabledList.add(buf.readBoolean());
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		int y = this.placeList.size();
		buf.writeInt(y);
		int keyCount = 0;
		for(String s : this.placeList.keySet())
		{
			buf.writeInt(s.length());
	        buf.writeBytes(s.getBytes());
			int[] coords = this.placeList.get(s);
			buf.writeInt(coords[0]);
			buf.writeInt(coords[1]);
			buf.writeInt(coords[2]);
			buf.writeBoolean(this.enabledList.get(keyCount));
			keyCount++;
		}
	}
}
