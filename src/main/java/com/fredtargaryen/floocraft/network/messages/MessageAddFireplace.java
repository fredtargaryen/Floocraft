package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;

import java.nio.charset.Charset;

public class MessageAddFireplace
{
	public String name;
	public BlockPos signPos;
	public BlockPos locationPos;
	private static final Charset defaultCharset = Charset.defaultCharset();
	
	@Override
	public void onMessage(Supplier<NetworkEvent.Context> ctx)
	{
		final IThreadListener serverWorld = ctx.getServerHandler().player.getServerWorld();
		serverWorld.addScheduledTask(() -> {
            WorldServer castedServerWorld = (WorldServer)serverWorld;
            TileEntityFireplace flooSign = (TileEntityFireplace)castedServerWorld.getTileEntity(message.signPos);
            flooSign.setY(message.locationPos.getY());
            FloocraftWorldData.forWorld(castedServerWorld).addLocation(message.name, message.locationPos);
        });
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int nameLength = buf.readInt();
        this.name = buf.readBytes(nameLength).toString(defaultCharset);
        this.locationPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		this.signPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(name.length());
        buf.writeBytes(name.getBytes());
		buf.writeInt(this.locationPos.getX());
		buf.writeInt(this.locationPos.getY());
		buf.writeInt(this.locationPos.getZ());
		buf.writeInt(this.signPos.getX());
		buf.writeInt(this.signPos.getY());
		buf.writeInt(this.signPos.getZ());
	}
}
