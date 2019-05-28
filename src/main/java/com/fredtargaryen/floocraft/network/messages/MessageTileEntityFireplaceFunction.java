package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageTileEntityFireplaceFunction {
	public int x, y, z;
    public boolean isConnected;

	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			final IThreadListener serverListener = ctx.get().getSender().getServerWorld();
            TileEntityFireplace tef = (TileEntityFireplace) ((WorldServer)serverListener).getTileEntity(new BlockPos(this.x, this.y, this.z));
            tef.setConnected(this.isConnected);
        });
		ctx.get().setPacketHandled(true);
	}

	public MessageTileEntityFireplaceFunction(){}

	/**
	 * Effectively fromBytes from 1.12.2
	 */
	public MessageTileEntityFireplaceFunction(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
		this.isConnected = buf.readBoolean();
	}

	public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeBoolean(this.isConnected);
	}
}