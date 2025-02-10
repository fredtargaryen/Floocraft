package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.nio.charset.Charset;

/**
 * Sends a list of all fireplaces in the current Level to the requesting player.
 * Direction: server to client
 */
public class FireplaceListResponseMessage {
    /**
     * The list of place names in the Level
     */
    public Object[] places;

    /**
     * Whether the place with the corresponding index in places can be teleported to
     */
    public boolean[] enabledList;

    /**
     * The index of the fireplace the player is trying to teleport from.
     * -1 if not teleporting from a fireplace connected to the Floo Network
     */
    public int playerPlaceIndex;
    private static final Charset defaultCharset = Charset.defaultCharset();

    public FireplaceListResponseMessage() {}

    public void encode(FriendlyByteBuf buf) {
        int y = this.places.length;
        buf.writeInt(y);
        int keyCount = 0;
        for (Object o : this.places) {
            String s = (String) o;
            buf.writeInt(s.length());
            buf.writeBytes(s.getBytes());
            buf.writeBoolean(this.enabledList[keyCount]);
            ++keyCount;
        }
        buf.writeInt(this.playerPlaceIndex);
    }

    public FireplaceListResponseMessage(FriendlyByteBuf buf) {
        this.places = new Object[]{};
        this.enabledList = new boolean[]{};
        int y = buf.readInt();
        if (y > 0) {
            this.places = new Object[y];
            this.enabledList = new boolean[y];
            for (int x = 0; x < y; ++x) {
                this.places[x] = buf.readBytes(buf.readInt()).toString(defaultCharset);
                this.enabledList[x] = buf.readBoolean();
            }
        }
        this.playerPlaceIndex = buf.readInt();
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> FloocraftBase.ClientModEvents.handleMessage(this));
        context.setPacketHandled(true);
    }
}
