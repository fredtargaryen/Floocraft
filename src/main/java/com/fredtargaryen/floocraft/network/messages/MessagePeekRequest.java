package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.network.ChunkManager;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class MessagePeekRequest {
    public int initX, initY, initZ;
    public String dest;
    private static final Charset defaultCharset = Charset.defaultCharset();

	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            int initX = this.initX;
            int initY = this.initY;
            int initZ = this.initZ;
            EntityPlayerMP player = ctx.get().getSender();
            World world = player.world;
            Block initBlock = world.getBlockState(new BlockPos(initX, initY, initZ)).getBlock();
            int[] destCoords = FloocraftWorldData.forWorld(world).placeList.get(this.dest);
            //Stop everything if the destination has the same coordinates as where the player is
            if(!(destCoords[0] == this.initX && destCoords[1] == this.initY && destCoords[2] == this.initZ)) {
                int destX = destCoords[0];
                int destY = destCoords[1];
                int destZ = destCoords[2];
                //Checks whether the player is currently in busy or idle green flames
                if (initBlock == FloocraftBase.GREEN_FLAMES_BUSY || initBlock == FloocraftBase.GREEN_FLAMES_IDLE) {
                    BlockPos dest = new BlockPos(destX, destY, destZ);
                    Block destBlock = world.getBlockState(dest).getBlock();
                    //Checks whether the destination is fire
                    if (destBlock == Blocks.FIRE || destBlock == FloocraftBase.GREEN_FLAMES_BUSY
                            || destBlock == FloocraftBase.GREEN_FLAMES_IDLE) {
                        EnumFacing direction = ((GreenFlamesBase) FloocraftBase.GREEN_FLAMES_TEMP).isInFireplace(world, dest);
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
                                    MessageStartPeek msp = new MessageStartPeek(peeker.getUniqueID());
                                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msp);
                                }
                                else {
                                    MessageDenyPeek mdp = new MessageDenyPeek();
                                    PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), mdp);
                                }
                            }
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public MessagePeekRequest() {}

    /**
     * Effectively fromBytes from 1.12.2
     */
	public MessagePeekRequest(ByteBuf buf) {
        this.initX = buf.readInt();
        this.initY = buf.readInt();
        this.initZ = buf.readInt();
        this.dest = buf.readBytes(buf.readInt()).toString(defaultCharset);
    }

	public void toBytes(ByteBuf buf) {
        buf.writeInt(initX);
        buf.writeInt(initY);
        buf.writeInt(initZ);
        buf.writeInt(dest.length());
        buf.writeBytes(dest.getBytes());
    }
}