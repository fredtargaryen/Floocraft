package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Begins the flashing and dizziness effect when received.
 * Direction: server to client
 */
public class TeleportFlashMessage {
    public boolean soul;

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> FloocraftBase.ClientModEvents.handleMessage(this));
        context.setPacketHandled(true);
    }

    public TeleportFlashMessage(boolean soul) {
        this.soul = soul;
    }

    public TeleportFlashMessage(FriendlyByteBuf buf) {
        this.soul = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.soul);
    }
}
