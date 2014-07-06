package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.block.GreenFlamesIdle;
import com.fredtargaryen.floocraft.block.GreenFlamesLowerBase;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class MessageTeleportEntity implements IMessage, IMessageHandler<MessageTeleportEntity, IMessage>
{
	public int destX, destY, destZ;
	@Override
	public IMessage onMessage(MessageTeleportEntity message, MessageContext ctx)
	{
        int X = message.destX;
        int Y = message.destY;
        int Z = message.destZ;
		boolean tpApproved;
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		World world = player.worldObj;
		Block destBlock = world.getBlock(X, Y, Z);
		if(destBlock instanceof BlockFire && !(destBlock instanceof GreenFlamesLowerBase))
		{
            GreenFlamesIdle g = new GreenFlamesIdle((BlockFire) destBlock);
            world.extinguishFire(player, X, Y, Z, 0);
            world.setBlock(X, Y, Z, g);
            tpApproved = g.approveOrDenyTeleport(world, X, Y, Z);
		}
		else
		{
			tpApproved = destBlock instanceof GreenFlamesLowerBase;
		}
		
		if(tpApproved)
		{
			int x = player.serverPosX;
			int y = player.serverPosY;
			int z = player.serverPosZ;
			if(player.isRiding())
			{
				player.mountEntity(null);
			}
			player.playerNetServerHandler.setPlayerLocation(X, Y, Z, player.rotationYaw, player.rotationPitch);
    		player.fallDistance = 0.0F;
    		world.setBlock(x, y, z, Blocks.fire); //Must change this line if I ever want green flames to last more than one tp
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