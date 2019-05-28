package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageStartPeek {
    public UUID peekerUUID;

    @Override
    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        final IThreadListener clientThread = Minecraft.getMinecraft();
        clientThread.addScheduledTask(() -> {
            GuiScreen s = ((Minecraft)clientThread).currentScreen;
             if(s instanceof GuiTeleport) {
                 ((GuiTeleport) s).onStartPeek(message);
             }
        });
        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.peekerUUID.getMostSignificantBits());
        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
    }
}
