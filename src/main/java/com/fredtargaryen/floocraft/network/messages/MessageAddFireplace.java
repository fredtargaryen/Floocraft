package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class MessageAddFireplace {
	public String name;
	public BlockPos signPos;
	public BlockPos locationPos;
	private static final Charset defaultCharset = Charset.defaultCharset();

	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
            WorldServer serverWorld = ctx.get().getSender().getServerWorld();
            TileEntityFireplace flooSign = (TileEntityFireplace)serverWorld.getTileEntity(this.signPos);
            flooSign.setY(this.locationPos.getY());
            FloocraftWorldData.forWorld(serverWorld).addLocation(this.name, this.locationPos);
        });
		ctx.get().setPacketHandled(true);
	}

	public MessageAddFireplace() {}

	/**
	 * Effectively fromBytes from 1.12.2
	 */
	public MessageAddFireplace(ByteBuf buf) {
		int nameLength = buf.readInt();
        this.name = buf.readBytes(nameLength).toString(defaultCharset);
        this.locationPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		this.signPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	public void toBytes(ByteBuf buf) {
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
