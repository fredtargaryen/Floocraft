package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesTemp;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class MessageTeleportEntity implements IMessage, IMessageHandler<MessageTeleportEntity, IMessage>
{
	public int initX, initY, initZ, destX, destY, destZ;
	@Override
	public IMessage onMessage(MessageTeleportEntity message, MessageContext ctx)
	{
        int initX = message.initX;
        int initY = message.initY;
        int initZ = message.initZ;
        int destX = message.destX;
        int destY = message.destY;
        int destZ = message.destZ;
		boolean tpApproved = false;
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;
		//Makes sure the destination block is fire, busy or idle flames, in a valid fireplace
		Block destBlock = world.getBlock(destX, destY, destZ);
		if(destBlock == Blocks.fire)
		{
            world.setBlock(destX, destY, destZ, FloocraftBase.greenFlamesTemp);
            GreenFlamesTemp gfit = (GreenFlamesTemp) world.getBlock(destX, destY, destZ);
            if(gfit.isInFireplace(world, destX, destY, destZ))
            {
                tpApproved = true;
            }
            else
            {
                world.setBlock(destX, destY, destZ, Blocks.fire);
                return null;
            }
		}
        if(destBlock instanceof GreenFlamesBase)
        {
            tpApproved = true;
        }
		//Makes sure the player going is in busy or idle flames
		Block initBlock = world.getBlock(initX, initY, initZ);
		if(!(initBlock instanceof GreenFlamesBase && initBlock != FloocraftBase.greenFlamesTemp))
		{
			tpApproved = false;
		}
        if(tpApproved)
		{
            PacketHandler.INSTANCE.sendTo(new MessageDoGreenFlash(), player);
			if(player.isRiding())
			{
				player.mountEntity(null);
			}
            player.playerNetServerHandler.setPlayerLocation(destX + 0.5D, destY, destZ + 0.5D, player.getRNG().nextFloat() * 360, player.rotationPitch);
    		player.fallDistance = 0.0F;
			int m = world.getBlockMetadata(initX, initY, initZ);
			if(m < 2)
			{
				world.setBlock(initX, initY, initZ, Blocks.fire);
			}
			else
			{
				world.setBlockMetadataWithNotify(initX, initY, initZ, m == 9 ? m : m - 1, 3);
			}
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
        this.initX = buf.readInt();
        this.initY = buf.readInt();
        this.initZ = buf.readInt();
		this.destX = buf.readInt();
		this.destY = buf.readInt();
		this.destZ = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
        buf.writeInt(initX);
        buf.writeInt(initY);
        buf.writeInt(initZ);
		buf.writeInt(destX);
		buf.writeInt(destY);
		buf.writeInt(destZ);
	}
}