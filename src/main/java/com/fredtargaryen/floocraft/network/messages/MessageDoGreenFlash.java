package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageDoGreenFlash {
	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> ((ClientProxy) FloocraftBase.proxy).flash.start());
		ctx.get().setPacketHandled(true);
	}

	public MessageDoGreenFlash() {}

	/**
	 * Effectively fromBytes from 1.12.2
	 */
	public MessageDoGreenFlash(ByteBuf buf) {
	}

	public void toBytes(ByteBuf buf) {
	}
}
