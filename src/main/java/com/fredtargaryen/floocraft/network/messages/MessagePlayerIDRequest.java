package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessagePlayerIDRequest {
    public UUID peekerUUID;

    @Override
    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        final IThreadListener serverListener = player.getServerWorld();
        serverListener.addScheduledTask(() -> {
            EntityPeeker ep = (EntityPeeker) FloocraftBase.getEntityWithUUID((World) serverListener, message.peekerUUID);
            MessagePlayerID mpID = new MessagePlayerID();
            mpID.peekerUUID = message.peekerUUID;
            mpID.playerUUID = ep.getPlayerUUID();
            PacketHandler.INSTANCE.sendTo(mpID, player);
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
