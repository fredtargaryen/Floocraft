package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Describes whether the location name on the Floo Sign will be added to the Floo Network.
 * Should only be sent when the user pressed the Connect to Network button in @link{FlooSignEditScreen},
 * sending a @link{FlooSignNameRequestMessage}.
 * Direction: server to client
 */
public class FlooSignNameResponseMessage {
    public boolean answer;

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> FloocraftBase.ClientModEvents.handleMessage(this));
        context.setPacketHandled(true);
    }

    public FlooSignNameResponseMessage(boolean answer) {
        this.answer = answer;
    }

    public FlooSignNameResponseMessage(FriendlyByteBuf buf) {
        this.answer = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.answer);
    }
}
