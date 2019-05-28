package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBusy;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
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

import java.nio.charset.Charset;

public class MessageTeleportEntity
{
	public int initX, initY, initZ;
    public String dest;
    private static final Charset defaultCharset = Charset.defaultCharset();
    
	@Override
	public void onMessage(Supplier<NetworkEvent.Context> ctx)
	{
		final EntityPlayerMP player = ctx.getServerHandler().player;
		final IThreadListener serverListener = player.getServerWorld();
		serverListener.addScheduledTask(() -> {
		    //Whether it is permitted for the player to travel to the named destination
            boolean validDest = false;
            WorldServer world = (WorldServer)serverListener;
            //The coordinates of the destination: [x, y, z]
            int[] destCoords = FloocraftWorldData.forWorld(world).placeList.get(message.dest);

            //Stop everything if the destination has the same coordinates as where the player is
            if(!(destCoords[0] == message.initX && destCoords[1] == message.initY && destCoords[2] == message.initZ))
            {
                BlockPos destBlockPos = new BlockPos(destCoords[0], destCoords[1], destCoords[2]);
                Block destBlock = world.getBlockState(destBlockPos).getBlock();

                //Checks whether the destination is fire and in a valid fireplace
                if (destBlock == Blocks.FIRE) {
                    validDest = ((GreenFlamesBase) FloocraftBase.greenFlamesTemp).isInFireplace(world, destBlockPos) != null;
                }
                //Checks whether the destination is busy or idle green flames (implying valid fireplace)
                else if (destBlock == FloocraftBase.greenFlamesBusy || destBlock == FloocraftBase.greenFlamesIdle) {
                    validDest = true;
                }

                BlockPos initBlockPos = new BlockPos(message.initX, message.initY, message.initZ);
                Block initBlock = world.getBlockState(initBlockPos).getBlock();

                //If destination is valid, checks whether the player is currently in busy or idle green flames
                if (validDest && (initBlock == FloocraftBase.greenFlamesBusy || initBlock == FloocraftBase.greenFlamesIdle)) {
                    //Get the fire ready...
                    if (destBlock == Blocks.FIRE) {
                        world.setBlockState(destBlockPos, FloocraftBase.greenFlamesTemp.getDefaultState());
                    }
                    //...then do the teleport...
                    PacketHandler.INSTANCE.sendTo(new MessageDoGreenFlash(), player);
                    if (player.isRiding()) {
                        player.dismountRidingEntity();
                    }
                    player.connection.setPlayerLocation(destCoords[0] + 0.5D, destCoords[1], destCoords[2] + 0.5D, player.getRNG().nextFloat() * 360, player.rotationPitch);
                    player.fallDistance = 0.0F;
                    //...then update the age of the fire.
                    int m = (Integer) world.getBlockState(initBlockPos).get(GreenFlamesBusy.AGE);
                    if (m < 2) {
                        world.setBlockState(initBlockPos, Blocks.FIRE.getDefaultState());
                    } else {
                        world.setBlockState(initBlockPos, FloocraftBase.greenFlamesBusy.getDefaultState().withProperty(GreenFlamesBusy.AGE, m == 9 ? 9 : m - 1), 2);
                    }
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
        this.dest = buf.readBytes(buf.readInt()).toString(defaultCharset);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
        buf.writeInt(initX);
        buf.writeInt(initY);
        buf.writeInt(initZ);
        buf.writeInt(dest.length());
        buf.writeBytes(dest.getBytes());
	}
}