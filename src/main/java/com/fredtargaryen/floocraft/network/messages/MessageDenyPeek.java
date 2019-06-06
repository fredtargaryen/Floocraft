package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageDenyPeek {
    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GuiScreen s = Minecraft.getInstance().currentScreen;
            if(s instanceof GuiTeleport) {
                ((GuiTeleport) s).onPeekDenied(this);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public MessageDenyPeek() {}

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageDenyPeek(ByteBuf buf){}

    public void toBytes(ByteBuf buf) { }
}
