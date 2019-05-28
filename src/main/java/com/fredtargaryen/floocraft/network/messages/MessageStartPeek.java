package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageStartPeek {
    public UUID peekerUUID;

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            GuiScreen s = Minecraft.getInstance().currentScreen;
             if(s instanceof GuiTeleport) {
                 ((GuiTeleport) s).onStartPeek(this);
             }
        });
        ctx.get().setPacketHandled(true);
    }

    public MessageStartPeek(UUID peekerUUID) {
        this.peekerUUID = peekerUUID;
    }

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageStartPeek(ByteBuf buf) {
        this.peekerUUID = new UUID(buf.readLong(), buf.readLong());
    }

    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.peekerUUID.getMostSignificantBits());
        buf.writeLong(this.peekerUUID.getLeastSignificantBits());
    }
}
