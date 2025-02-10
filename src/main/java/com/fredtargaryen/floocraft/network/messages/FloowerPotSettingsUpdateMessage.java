package com.fredtargaryen.floocraft.network.messages;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class FloowerPotSettingsUpdateMessage {
    public char range;
    public int amount;
    public BlockPos pos;

    public void handle(CustomPayloadEvent.Context context) {
//        context.enqueueWork(() -> {
//            ServerWorld sw = context.getSender().getServerWorld();
//            TileEntity te = sw.getTileEntity(FloowerPotSettingsUpdateMessage.this.pos);
//            if(te.getType() == FloocraftBase.POT_TYPE.get())
//                ((FloowerPotTileEntity) te).adjustPotRange(this.range, this.amount);
//        });
        context.setPacketHandled(true);
    }

    public FloowerPotSettingsUpdateMessage() {
    }

    public FloowerPotSettingsUpdateMessage(FriendlyByteBuf buf) {
        this.range = buf.readChar();
        this.amount = buf.readInt();
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeChar(this.range);
        buf.writeInt(this.amount);
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
    }
}
