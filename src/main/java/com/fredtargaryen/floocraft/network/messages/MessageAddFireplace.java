package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageAddFireplace implements IMessage, IMessageHandler<MessageAddFireplace, IMessage>
{
	public String name;
	public int x, y, z;
	
	@Override
	public IMessage onMessage(final MessageAddFireplace message, MessageContext ctx)
	{
		final IThreadListener serverWorld = (WorldServer)ctx.getServerHandler().playerEntity.worldObj;
		serverWorld.addScheduledTask(new Runnable(){
			@Override
			public void run()
			{
				FloocraftWorldData.forWorld((WorldServer)serverWorld).addLocation(message.name, message.x, message.y, message.z);
			}
		});
		FloocraftWorldData.forWorld(ctx.getServerHandler().playerEntity.worldObj).addLocation(message.name, message.x, message.y, message.z);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int nameLength = buf.readInt();
        this.name = new String(buf.readBytes(nameLength).array());
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(name.length());
        buf.writeBytes(name.getBytes());
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
}
