package com.fredtargaryen.floocraft.network.messages;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import com.fredtargaryen.floocraft.block.GreenFlamesIdle;
import com.fredtargaryen.floocraft.block.GreenFlamesLowerBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;

public class MessageTeleportEntity implements IMessage, IMessageHandler<MessageTeleportEntity, IMessage>
{
	public int destX, destY, destZ;
	@Override
	public IMessage onMessage(MessageTeleportEntity message, MessageContext ctx)
	{
		boolean tpApproved;
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;
		Block destBlock = world.getBlock(destX, destY, destZ);
		if(destBlock instanceof BlockFire && !(destBlock instanceof GreenFlamesLowerBase))
		{
			world.setBlock(destX, destY, destZ, new GreenFlamesIdle());
			GreenFlamesIdle g = (GreenFlamesIdle) world.getBlock(destX, destY, destZ);
			tpApproved = g.approveOrDenyTeleport(world, destX, destY, destZ);
			world.setBlock(destX, destY, destZ, destBlock);
		}
		else if(destBlock instanceof GreenFlamesLowerBase)
		{
			tpApproved = true;
		}
		else
		{
			tpApproved = false;
		}
		
		if(tpApproved)
		{
			int x = player.serverPosX;
			int y = player.serverPosY;
			int z = player.serverPosZ;
			if(player.isRiding())
			{
				player.mountEntity((Entity)null);
			}
			player.playerNetServerHandler.setPlayerLocation(destX, destY, destZ, player.rotationYaw, player.rotationPitch);
    		player.fallDistance = 0.0F;
    		world.setBlock(x, y, z, Blocks.fire);
    		return null;
		}
		return FloocraftWorldData.forWorld(world).assembleNewFireplaceList(world);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		destX = buf.readInt();
		destY = buf.readInt();
		destZ = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(destX);
		buf.writeInt(destY);
		buf.writeInt(destZ);
	}
}