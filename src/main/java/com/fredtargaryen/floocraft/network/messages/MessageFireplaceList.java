package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class MessageFireplaceList implements IMessage, IMessageHandler<MessageFireplaceList, IMessage>
{
	public List<String>placenamelist = new ArrayList<String>();
	public List<Integer>xcoordlist = new ArrayList<Integer>();
	public List<Integer>ycoordlist = new ArrayList<Integer>();
	public List<Integer>zcoordlist = new ArrayList<Integer>();
	public List<Boolean>enabledlist = new ArrayList<Boolean>();
	
	@Override
	public IMessage onMessage(MessageFireplaceList message, MessageContext ctx)
	{
		GuiScreen s = Minecraft.getMinecraft().currentScreen;
		if(s instanceof GuiTeleport)
		{
			((GuiTeleport) s).onMessageReceived(message);
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int y = buf.readInt();
		if(y > 0)
		{
			for(int x = 0; x < y; x++)
			{
				int nameLength = buf.readInt();
				this.placenamelist.add(new String(buf.readBytes(nameLength).array()));
				this.xcoordlist.add(buf.readInt());
				this.ycoordlist.add(buf.readInt());
				this.zcoordlist.add(buf.readInt());
				this.enabledlist.add(buf.readBoolean());
			}
		}
		else
		{
			this.placenamelist.clear();
			this.xcoordlist.clear();
			this.ycoordlist.clear();
			this.zcoordlist.clear();
			this.enabledlist.clear();
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		int y = this.placenamelist.size();
		buf.writeInt(y);
		for(int x = 0; x < y; x++)
		{
			String s = this.placenamelist.get(x);
			buf.writeInt(s.length());
	        buf.writeBytes(s.getBytes());
			buf.writeInt(this.xcoordlist.get(x));
			buf.writeInt(this.ycoordlist.get(x));
			buf.writeInt(this.zcoordlist.get(x));
			buf.writeBoolean(this.enabledlist.get(x));
		}
	}
}
