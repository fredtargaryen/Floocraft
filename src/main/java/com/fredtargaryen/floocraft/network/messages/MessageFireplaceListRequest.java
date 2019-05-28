package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageFireplaceListRequest {
	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ctx.get().getSender());
		ctx.get().setPacketHandled(true);
		final EntityPlayerMP player = ctx.getServerHandler().player;
		final IThreadListener serverListener = player.getServerWorld();
		serverListener.addScheduledTask(() -> {
			World w = (World) serverListener;
			MessageFireplaceList mfl = FloocraftWorldData.forWorld(w).assembleNewFireplaceList(w);
			PacketHandler.INSTANCE.sendTo(mfl, player);
		});
	}

	public MessageFireplaceListRequest () {}

	/**
	 * Effectively fromBytes from 1.12.2
	 */
	public MessageFireplaceListRequest(ByteBuf buf) {}

	public void toBytes(ByteBuf buf) {}
}
