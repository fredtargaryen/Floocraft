package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.WorldServer;

public class MessageAddFireplace implements IMessage, IMessageHandler<MessageAddFireplace, IMessage>
{
	public String name;
	//LOCation and BLocK XYZ
	public int locX, locY, locZ, blkX, blkY, blkZ;
	
	@Override
	public IMessage onMessage(MessageAddFireplace message, MessageContext ctx)
	{
		WorldServer world = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		((TileEntityFireplace)(world.getTileEntity(message.blkX, message.blkY, message.blkZ))).setY(message.locY);
		FloocraftWorldData.forWorld(world).addLocation(message.name, message.locX, message.locY, message.locZ);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int nameLength = buf.readInt();
        this.name = new String(buf.readBytes(nameLength).array());
        this.blkX = buf.readInt();
        this.blkY = buf.readInt();
        this.blkZ = buf.readInt();
		this.locX = buf.readInt();
		this.locY = buf.readInt();
		this.locZ = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(name.length());
        buf.writeBytes(name.getBytes());
		buf.writeInt(this.blkX);
		buf.writeInt(this.blkY);
		buf.writeInt(this.blkZ);
		buf.writeInt(this.locX);
		buf.writeInt(this.locY);
		buf.writeInt(this.locZ);
	}
}
