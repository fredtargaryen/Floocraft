package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MessageFlooTorchTeleport {
    public int torchX, torchY, torchZ;

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            int torchX1 = this.torchX;
            int torchY1 = this.torchY;
            int torchZ1 = this.torchZ;
            ServerPlayerEntity player = ctx.get().getSender();
            World world = player.getServerWorld();
            int minx = torchX1 - 3;
            int maxx = torchX1 + 3;
            int minz = torchZ1 - 3;
            int maxz = torchZ1 + 3;
            List<BlockPos> coords = new ArrayList<>();
            for (int x = minx; x <= maxx; x++) {
                for (int z = minz; z <= maxz; z++) {
                    BlockPos nextPos = new BlockPos(x, torchY1, z);
                    if (world.isAirBlock(nextPos) && world.isAirBlock(nextPos.up())) {
                        coords.add(nextPos);
                    }
                }
            }
            if(coords.size() > 0)
            {
                BlockPos chosenCoord = coords.get(world.rand.nextInt(coords.size()));
                double x = chosenCoord.getX();
                if(x < torchX1)
                {
                    x += 0.5;
                }
                else if(x > torchX1) {
                    x -= 0.5;
                }
                double y = chosenCoord.getY();
                double z = chosenCoord.getZ();
                if(z < torchZ1)
                {
                    z += 0.5;
                }
                else if(z > torchZ1)
                {
                    z -= 0.5;
                }
                if(player.getRidingEntity() != null) {
                    player.stopRiding();
                }
                player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
                player.fallDistance = 0.0F;
                world.playSound(null, new BlockPos(torchX1, torchY1, torchZ1), FloocraftBase.FLICK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        });

        ctx.get().setPacketHandled(true);
    }

    public MessageFlooTorchTeleport() {}

    /**
     * Effectively fromBytes from 1.12.2
     */
    public MessageFlooTorchTeleport(ByteBuf buf) {
        this.torchX = buf.readInt();
        this.torchY = buf.readInt();
        this.torchZ = buf.readInt();
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.torchX);
        buf.writeInt(this.torchY);
        buf.writeInt(this.torchZ);
    }
}
