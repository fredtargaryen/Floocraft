package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiFlooSign;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageApproval {
	public boolean answer;

	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
            GuiScreen s = Minecraft.getInstance().currentScreen;
            if(s instanceof GuiFlooSign) {
                ((GuiFlooSign) s).dealWithAnswer(this.answer);
            }
        });
	}

	public MessageApproval(boolean answer) {
		this.answer = answer;
	}

	/**
	 * Effectively fromBytes from 1.12.2
	 */
	public MessageApproval(ByteBuf buf) {
		this.answer = buf.getBoolean(0);
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(this.answer);
	}
}
