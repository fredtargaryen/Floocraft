package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageDoGreenFlash {

	public boolean soul;

	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> FloocraftBase.proxy.onMessage(this));
		ctx.get().setPacketHandled(true);
	}

	public MessageDoGreenFlash(boolean soul) { this.soul = soul; }

	/**
	 * Effectively fromBytes from 1.12.2
	 */
	public MessageDoGreenFlash(ByteBuf buf) { this.soul = buf.readBoolean(); }

	public void toBytes(ByteBuf buf) { buf.writeBoolean(this.soul); }
}
