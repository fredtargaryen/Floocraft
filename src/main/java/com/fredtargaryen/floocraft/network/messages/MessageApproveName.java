package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class MessageApproveName {
	public String name;
	private static final Charset defaultCharset = Charset.defaultCharset();

	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			World w = ctx.get().getSender().world;
			MessageApproval ma = new MessageApproval(!FloocraftWorldData.forWorld(w).placeList.containsKey(this.name));
			PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), ma);
		});
		ctx.get().setPacketHandled(true);
	}

	public MessageApproveName(String name) {
		this.name = name;
	}

	/**
	 * Effectively fromBytes from 1.12.2
	 */
	public MessageApproveName(ByteBuf buf) {
		int nameLength = buf.readInt();
        this.name = buf.readBytes(nameLength).toString(defaultCharset);
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(name.length());
        buf.writeBytes(name.getBytes());
	}
}
