package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Sent when a Floo Sign is placed and opens the sign edit GUI
 * Direction: server to client
 */
public class OpenFlooSignEditScreenMessage {
    public BlockPos signPos;

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> FloocraftBase.ClientModEvents.handleMessage(this));
        context.setPacketHandled(true);
    }

    public OpenFlooSignEditScreenMessage() {
    }

    public OpenFlooSignEditScreenMessage(FriendlyByteBuf buf) {
        this.signPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.signPos);
    }
}
