package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class MessageTileEntityFireplaceFunction implements IMessage, IMessageHandler<MessageTileEntityFireplaceFunction, IMessage>
{
	public int x, y, z;
    public boolean isConnected;

	@Override
	public IMessage onMessage(MessageTileEntityFireplaceFunction message, MessageContext ctx)
	{
        World w = ctx.getServerHandler().playerEntity.worldObj;
		TileEntityFireplace tef = (TileEntityFireplace) w.getTileEntity(new BlockPos(message.x, message.y, message.z));
        tef.setConnected(message.isConnected);
        return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
		this.isConnected = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeBoolean(this.isConnected);
	}
}