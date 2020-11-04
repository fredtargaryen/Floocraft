package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.tileentity.FloowerPotTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePotRange {
    public char range;
    public int amount;
    public BlockPos pos;

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerWorld sw = ctx.get().getSender().getServerWorld();
            TileEntity te = sw.getTileEntity(MessagePotRange.this.pos);
            if(te.getType() == FloocraftBase.POT_TYPE.get())
                ((FloowerPotTileEntity) te).adjustPotRange(this.range, this.amount);
        });
        ctx.get().setPacketHandled(true);
    }

    public MessagePotRange() { }

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessagePotRange(ByteBuf buf) {
        this.range = buf.readChar();
        this.amount = buf.readInt();
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void toBytes(ByteBuf buf) {
        buf.writeChar(this.range);
        buf.writeInt(this.amount);
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
    }
}
