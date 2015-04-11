package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlames;
import com.fredtargaryen.floocraft.block.GreenFlamesTemp;
import com.fredtargaryen.floocraft.network.PacketHandler;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Random;

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
        BlockPos dest = new BlockPos(destX, destY, destZ);
		Block destBlock = world.getBlockState(new BlockPos(destX, destY, destZ)).getBlock();
		if(destBlock == Blocks.fire)
		{
            world.setBlockState(new BlockPos(destX, destY, destZ), FloocraftBase.greenFlamesTemp.getDefaultState());
            GreenFlamesTemp gfit = (GreenFlamesTemp) world.getBlockState(new BlockPos(destX, destY, destZ));
            if(gfit.approveOrDenyTeleport(world, dest))
            {
                tpApproved = true;
            }
            else
            {
                world.setBlockState(new BlockPos(destX, destY, destZ), Blocks.fire.getDefaultState());
                return null;
            }
		}
        if(destBlock instanceof GreenFlames)
        {
            tpApproved = true;
        }
        if(tpApproved)
		{
            PacketHandler.INSTANCE.sendTo(new MessageDoGreenFlash(), player);
			if(player.isRiding())
			{
				player.mountEntity(null);
			}
            Random rand = new Random();
            player.playerNetServerHandler.setPlayerLocation(destX + 0.5D, destY, destZ + 0.5D, rand.nextFloat() * 360, player.rotationPitch);
    		player.fallDistance = 0.0F;
            BlockPos pos = new BlockPos(initX, initY, initZ);
            int m = (Integer)world.getBlockState(pos).getValue(GreenFlames.AGE);
    		world.setBlockState(pos, FloocraftBase.greenFlames.getDefaultState().withProperty(GreenFlames.AGE, m == 9 ? m : m - 1), 2);
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