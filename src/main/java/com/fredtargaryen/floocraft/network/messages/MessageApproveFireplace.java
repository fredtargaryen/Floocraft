package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.tileentity.FireplaceTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class MessageApproveFireplace {
    public int x, y, z;
    public boolean attemptingToConnect;
    public String[] name;
    private static final Charset defaultCharset = Charset.defaultCharset();

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity spe = ctx.get().getSender();
            ServerWorld w = spe.getServerWorld();
            FireplaceTileEntity fte = (FireplaceTileEntity) (w.getTileEntity(new BlockPos(this.x, this.y, this.z)));
            fte.setConnected(this.attemptingToConnect);
            if(this.attemptingToConnect) {
                boolean approved = !FloocraftWorldData.forWorld(w).placeList.containsKey(FireplaceTileEntity.getSignTextAsLine(this.name));
                MessageApproval ma = new MessageApproval(approved);
                MessageHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> spe), ma);
                if(approved) {
                    fte.setString(0, this.name[0]);
                    fte.setString(1, this.name[1]);
                    fte.setString(2, this.name[2]);
                    fte.setString(3, this.name[3]);
                    fte.addLocation();
                }
            }
            else {
                MessageApproval ma = new MessageApproval(true);
                MessageHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> spe), ma);
                fte.setString(0, this.name[0]);
                fte.setString(1, this.name[1]);
                fte.setString(2, this.name[2]);
                fte.setString(3, this.name[3]);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public MessageApproveFireplace(){}

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageApproveFireplace(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.attemptingToConnect = buf.readBoolean();
        this.name = new String[4];
        for(int i = 0; i < 4; ++i) {
            this.name[i] = buf.readBytes(buf.readInt()).toString(defaultCharset);
        }
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeBoolean(this.attemptingToConnect);
        for(String s : this.name) {
            buf.writeInt(s.length());
            buf.writeBytes(s.getBytes());
        }
    }
}
