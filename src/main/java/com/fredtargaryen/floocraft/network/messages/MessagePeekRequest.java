package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePeekRequest implements IMessage, IMessageHandler<MessagePeekRequest, IMessage>
{
	public int initX, initY, initZ, destX, destY, destZ;
	@Override
	public IMessage onMessage(final MessagePeekRequest message, MessageContext ctx)
	{
		final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		final IThreadListener serverListener = player.getServerWorld();
		serverListener.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				int initX = message.initX;
				int initY = message.initY;
				int initZ = message.initZ;
				int destX = message.destX;
				int destY = message.destY;
				int destZ = message.destZ;
				WorldServer world = (WorldServer)serverListener;
				Block initBlock = world.getBlockState(new BlockPos(initX, initY, initZ)).getBlock();
				//Checks whether the player is currently in busy or idle green flames
				if(initBlock == FloocraftBase.greenFlamesBusy || initBlock == FloocraftBase.greenFlamesIdle) {
				    BlockPos dest = new BlockPos(destX, destY, destZ);
				    Block destBlock = world.getBlockState(dest).getBlock();
				    //Checks whether the destination is fire
				    if(destBlock == Blocks.FIRE || destBlock == FloocraftBase.greenFlamesBusy
                            || destBlock == FloocraftBase.greenFlamesIdle) {
                        EnumFacing direction = ((GreenFlamesBase) FloocraftBase.greenFlamesTemp).isInFireplace(world, dest);
                        if (direction != null) {
                            EnumFacing.Axis axis = direction.getAxis();
                            if (axis == EnumFacing.Axis.X || axis == EnumFacing.Axis.Z) {
                                //Create peeker
								EntityPeeker peeker = new EntityPeeker(world);
								peeker.setPeekerData(player, dest, direction);
								world.spawnEntity(peeker);
								//Create message
								MessageStartPeek msp = new MessageStartPeek();
								msp.peekerUUID = peeker.getUniqueID();
                                PacketHandler.INSTANCE.sendTo(msp, player);
                            }
                        }
                    }
				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
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