package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBusy;
import com.fredtargaryen.floocraft.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageTeleportEntity implements IMessage, IMessageHandler<MessageTeleportEntity, IMessage>
{
	public int initX, initY, initZ, destX, destY, destZ;
	@Override
	public IMessage onMessage(final MessageTeleportEntity message, MessageContext ctx)
	{
		final EntityPlayerMP player = ctx.getServerHandler().player;
		final IThreadListener serverListener = player.getServerWorld();
		serverListener.addScheduledTask(() -> {
            int initX1 = message.initX;
            int initY1 = message.initY;
            int initZ1 = message.initZ;
            int destX1 = message.destX;
            int destY1 = message.destY;
            int destZ1 = message.destZ;
            boolean validDest = false;
            WorldServer world = (WorldServer)serverListener;
            BlockPos dest = new BlockPos(destX1, destY1, destZ1);
            Block destBlock = world.getBlockState(dest).getBlock();
            //Checks whether the destination is fire
            if(destBlock == Blocks.FIRE)
            {
                validDest = ((GreenFlamesBase) FloocraftBase.greenFlamesTemp).isInFireplace(world, dest);
            }
            //Checks whether the destination is busy or idle green flames
            else if(destBlock == FloocraftBase.greenFlamesBusy || destBlock == FloocraftBase.greenFlamesIdle)
            {
                validDest = true;
            }

            Block initBlock = world.getBlockState(new BlockPos(initX1, initY1, initZ1)).getBlock();
            //Checks whether the player is currently in busy or idle green flames
            if(validDest && (initBlock == FloocraftBase.greenFlamesBusy || initBlock == FloocraftBase.greenFlamesIdle))
            {
                //Get the fire ready...
                if(destBlock == Blocks.FIRE)
                {
                    world.setBlockState(dest, FloocraftBase.greenFlamesTemp.getDefaultState());
                }
                //...then do the teleport
                PacketHandler.INSTANCE.sendTo(new MessageDoGreenFlash(), player);
                if(player.isRiding())
                {
                    player.dismountRidingEntity();
                }
                player.connection.setPlayerLocation(destX1 + 0.5D, destY1, destZ1 + 0.5D, player.getRNG().nextFloat() * 360, player.rotationPitch);
                player.fallDistance = 0.0F;
                BlockPos init = new BlockPos(initX1, initY1, initZ1);
                int m = (Integer)world.getBlockState(init).getValue(GreenFlamesBusy.AGE);
                if(m < 2)
                {
                    world.setBlockState(init, Blocks.FIRE.getDefaultState());
                }
                else
                {
                    world.setBlockState(init, FloocraftBase.greenFlamesBusy.getDefaultState().withProperty(GreenFlamesBusy.AGE, m == 9 ? 9 : m - 1), 2);
                }
            }
        });

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