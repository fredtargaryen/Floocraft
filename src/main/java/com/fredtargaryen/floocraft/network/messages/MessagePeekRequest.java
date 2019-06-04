package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.network.ChunkManager;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
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

import java.nio.charset.Charset;

public class MessagePeekRequest implements IMessage, IMessageHandler<MessagePeekRequest, IMessage> {
    public int initX, initY, initZ;
    public String dest;
    private static final Charset defaultCharset = Charset.defaultCharset();

    @Override
	public IMessage onMessage(final MessagePeekRequest message, MessageContext ctx) {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        final IThreadListener serverListener = player.getServerWorld();
        serverListener.addScheduledTask(() -> {
            int initX = message.initX;
            int initY = message.initY;
            int initZ = message.initZ;
            WorldServer world = (WorldServer)serverListener;
            Block initBlock = world.getBlockState(new BlockPos(initX, initY, initZ)).getBlock();
            int[] destCoords = FloocraftWorldData.forWorld(world).placeList.get(message.dest);
            //Stop everything if the destination has the same coordinates as where the player is
            if(!(destCoords[0] == message.initX && destCoords[1] == message.initY && destCoords[2] == message.initZ)) {
                int destX = destCoords[0];
                int destY = destCoords[1];
                int destZ = destCoords[2];
                //Checks whether the player is currently in busy or idle green flames
                if (initBlock == FloocraftBase.greenFlamesBusy || initBlock == FloocraftBase.greenFlamesIdle) {
                    BlockPos dest = new BlockPos(destX, destY, destZ);
                    Block destBlock = world.getBlockState(dest).getBlock();
                    //Checks whether the destination is fire
                    if (destBlock == Blocks.FIRE || destBlock == FloocraftBase.greenFlamesBusy
                            || destBlock == FloocraftBase.greenFlamesIdle) {
                        EnumFacing direction = ((GreenFlamesBase) FloocraftBase.greenFlamesTemp).isInFireplace(world, dest);
                        if (direction != null) {
                            EnumFacing.Axis axis = direction.getAxis();
                            if (axis == EnumFacing.Axis.X || axis == EnumFacing.Axis.Z) {
                                //Create peeker
                                EntityPeeker peeker = new EntityPeeker(world);
                                peeker.setPeekerData(player, dest, direction);
                                //Force the peek chunks to load. They stay loaded until the peeker dies.
                                if(ChunkManager.loadChunks(world, dest, peeker, direction)) {
                                    //Spawn the peeker (in one of the forced chunks)
                                    world.spawnEntity(peeker);
                                    //Create message
                                    MessageStartPeek msp = new MessageStartPeek();
                                    msp.peekerUUID = peeker.getUniqueID();
                                    PacketHandler.INSTANCE.sendTo(msp, player);
                                }
                                else {
                                    MessageDenyPeek mdp = new MessageDenyPeek();
                                    PacketHandler.INSTANCE.sendTo(mdp, player);
                                }
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
        this.dest = buf.readBytes(buf.readInt()).toString(defaultCharset);
    }

        	@Override
	public void toBytes(ByteBuf buf) {
        buf.writeInt(initX);
        buf.writeInt(initY);
        buf.writeInt(initZ);
        buf.writeInt(dest.length());
        buf.writeBytes(dest.getBytes());
    }
}