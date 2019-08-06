package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.MessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class MessageFireplaceListRequest {
	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			World w = ctx.get().getSender().world;
			MessageFireplaceList mfl = FloocraftWorldData.forWorld(w).assembleNewFireplaceList(w);
			MessageHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), mfl);
		});
		ctx.get().setPacketHandled(true);
	}

	public MessageFireplaceListRequest () {}

	/**
	 * Effectively fromBytes from 1.12.2
	 */
	public MessageFireplaceListRequest(ByteBuf buf) {}

	public void toBytes(ByteBuf buf) {}
}
