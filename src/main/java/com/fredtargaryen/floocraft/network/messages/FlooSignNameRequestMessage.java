package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.HelperFunctions;
import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import com.fredtargaryen.floocraft.network.FloocraftLevelData;
import com.fredtargaryen.floocraft.network.MessageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.nio.charset.Charset;

/**
 * Send the text to set on a Floo Sign.
 * If just using the sign for decoration it will always be set, but the sign won't be added to the Floo Network.
 * If actually connecting, the sign will only be approved if it doesn't share a name with any other sign in the Level.
 * If approved, the text will be set and the sign will be added to the Floo Network.
 * If not approved, a rejection will be sent to the client and the text won't be set.
 * Direction: client to server
 */
public class FlooSignNameRequestMessage {
    public BlockPos signPos;
    public boolean attemptingToConnect;
    public String[] name;
    private static final Charset defaultCharset = Charset.defaultCharset();

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer sp = context.getSender();
            assert sp != null;
            ServerLevel level = sp.serverLevel();
            FlooSignBlockEntity fsbe = (FlooSignBlockEntity) (level.getBlockEntity(this.signPos));
            if (fsbe != null && !fsbe.getConnected()) {
                if (this.attemptingToConnect) {
                    boolean approved = !FloocraftLevelData.getForLevel(level).placeList
                            .containsKey(HelperFunctions.convertArrayToLocationName(this.name));
                    FlooSignNameResponseMessage fsnrm = new FlooSignNameResponseMessage(approved);
                    MessageHandler.sendToPlayer(fsnrm, sp);
                    if (approved) {
                        fsbe.addLocation(this.name);
                        fsbe.setConnected(true);
                        fsbe.markUpdate();
                    } else {
                        fsbe.setConnected(false);
                    }
                } else {
                    fsbe.setNameOnSign(this.name);
                    fsbe.setConnected(false);
                    fsbe.markUpdate();
                }
            }
        });
        context.setPacketHandled(true);
    }

    public FlooSignNameRequestMessage() {
    }

    public FlooSignNameRequestMessage(FriendlyByteBuf buf) {
        this.signPos = buf.readBlockPos();
        this.attemptingToConnect = buf.readBoolean();
        this.name = new String[4];
        for (int i = 0; i < 4; ++i) {
            this.name[i] = buf.readBytes(buf.readInt()).toString(defaultCharset);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.signPos);
        buf.writeBoolean(this.attemptingToConnect);
        for (String s : this.name) {
            buf.writeInt(s.length());
            buf.writeBytes(s.getBytes());
        }
    }
}
