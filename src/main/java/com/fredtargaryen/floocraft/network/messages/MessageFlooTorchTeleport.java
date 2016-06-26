package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.DataReference;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

import static com.fredtargaryen.floocraft.FloocraftBase.flick;

public class MessageFlooTorchTeleport implements IMessage, IMessageHandler<MessageFlooTorchTeleport, IMessage>
{
    public int torchX, torchY, torchZ;

    @Override
    public IMessage onMessage(final MessageFlooTorchTeleport message, MessageContext ctx)
    {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        final IThreadListener serverListener = (WorldServer)player.worldObj;
        serverListener.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                int torchX = message.torchX;
                int torchY = message.torchY;
                int torchZ = message.torchZ;
                WorldServer world = (WorldServer) serverListener;
                int minx = torchX - 3;
                int maxx = torchX + 3;
                int minz = torchZ - 3;
                int maxz = torchZ + 3;
                int blocky = torchY;
                List<BlockPos> coords = new ArrayList<BlockPos>();
                for (int x = minx; x <= maxx; x++)
                {
                    for (int z = minz; z <= maxz; z++)
                    {
                        BlockPos nextPos = new BlockPos(x, blocky, z);
                        if (world.isAirBlock(nextPos) && world.isAirBlock(nextPos.up()))
                        {
                            coords.add(nextPos);
                        }
                    }
                }
                if(coords.size() > 0)
                {
                    BlockPos chosenCoord = coords.get(world.rand.nextInt(coords.size()));
                    double x = chosenCoord.getX();
                    if(x < torchX)
                    {
                        x += 0.5;
                    }
                    else if(x > torchX) {
                        x -= 0.5;
                    }
                    double y = chosenCoord.getY();
                    double z = chosenCoord.getZ();
                    if(z < torchZ)
                    {
                        z += 0.5;
                    }
                    else if(z > torchZ)
                    {
                        z -= 0.5;
                    }
                    if(player.isRiding())
                    {
                        player.dismountRidingEntity();
                    }
                    player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
                    player.fallDistance = 0.0F;
                    world.playSound(null, new BlockPos(torchX, torchY, torchZ), flick, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        });

        return null;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.torchX = buf.readInt();
        this.torchY = buf.readInt();
        this.torchZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.torchX);
        buf.writeInt(this.torchY);
        buf.writeInt(this.torchZ);
    }
}
