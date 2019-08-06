package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.FlooSignScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageApproval {
	public boolean answer;

	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> FloocraftBase.proxy.onMessage(this));
		ctx.get().setPacketHandled(true);
	}

	public MessageApproval(boolean answer) {
		this.answer = answer;
	}

	/**
	 * Effectively fromBytes from 1.12.2
	 */
	public MessageApproval(ByteBuf buf) {
		this.answer = buf.readBoolean();
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(this.answer);
	}
}
