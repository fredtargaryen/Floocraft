package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class MessageTileEntityFireplaceFunction implements IMessage, IMessageHandler<MessageTileEntityFireplaceFunction, IMessage>
{
	public int x, y, z;
    public boolean function;

	@Override
	public IMessage onMessage(MessageTileEntityFireplaceFunction message, MessageContext ctx)
	{
        World w = ctx.getServerHandler().playerEntity.worldObj;
		TileEntityFireplace tef = (TileEntityFireplace) w.getTileEntity(message.x, message.y, message.z);
        tef.setDecorative(message.function);
        return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
		this.function = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeBoolean(this.function);
	}
}