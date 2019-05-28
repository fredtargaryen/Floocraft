package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiFlooSign;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageApproval
{
	public boolean answer;
	
	@Override
	public void onMessage(Supplier<NetworkEvent.Context> ctx)
	{
		final IThreadListener clientThread = Minecraft.getMinecraft();
		clientThread.addScheduledTask(() -> {
            GuiScreen s = ((Minecraft)clientThread).currentScreen;
            if(s instanceof GuiFlooSign)
            {
                ((GuiFlooSign) s).dealWithAnswer(message.answer);
            }
        });
	}

	public void fromBytes(ByteBuf buf)
	{
		this.answer = buf.getBoolean(0);
	}

	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(this.answer);
	}
}
