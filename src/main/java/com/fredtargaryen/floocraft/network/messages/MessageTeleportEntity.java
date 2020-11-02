package com.fredtargaryen.floocraft.network.messages;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.FlooFlamesBase;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.MessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class MessageTeleportEntity {
	public int initX, initY, initZ;
    public String dest;
    private static final Charset defaultCharset = Charset.defaultCharset();

	public void onMessage(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
		    //Whether it is permitted for the player to travel to the named destination
            boolean validDest = false;
            ServerPlayerEntity player = ctx.get().getSender();
            World world = player.world;
            //The coordinates of the destination: [x, y, z]
            int[] destCoords = FloocraftWorldData.forWorld(world).placeList.get(this.dest);

            //Stop everything if the destination has the same coordinates as where the player is
            if(!(destCoords[0] == this.initX && destCoords[1] == this.initY && destCoords[2] == this.initZ))
            {
                BlockPos destBlockPos = new BlockPos(destCoords[0], destCoords[1], destCoords[2]);
                Block destBlock = world.getBlockState(destBlockPos).getBlock();

                //Checks whether the destination is fire and in a valid fireplace
                if (destBlock.isIn(BlockTags.FIRE)) {
                    validDest = ((FlooFlamesBase) FloocraftBase.GREEN_FLAMES_TEMP).isInFireplace(world, destBlockPos) != null;
                }
                //Checks whether the destination is busy or idle green flames (implying valid fireplace)
                else if (this.isBusyOrIdleFlooBlock(destBlock)) {
                    validDest = true;
                }

                BlockPos initBlockPos = new BlockPos(this.initX, this.initY, this.initZ);
                BlockState initBlockState = world.getBlockState(initBlockPos);
                Block initBlock = initBlockState.getBlock();

                //If destination is valid, checks whether the player is currently in busy or idle green flames
                if (validDest && (this.isBusyOrIdleFlooBlock(initBlock))) {
                    boolean initSoul = SoulFireBlock.shouldLightSoulFire(world.getBlockState(initBlockPos.down()).getBlock());
                    boolean destSoul = SoulFireBlock.shouldLightSoulFire(world.getBlockState(destBlockPos.down()).getBlock());
                    //Get the fire ready...
                    world.setBlockState(destBlockPos, destSoul ? FloocraftBase.MAGENTA_FLAMES_TEMP.getDefaultState() : FloocraftBase.GREEN_FLAMES_TEMP.getDefaultState());
                    //...then do the teleport...
                    MessageHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageDoGreenFlash(initSoul));
                    if (player.getRidingEntity() != null) {
                        player.stopRiding();
                    }
                    player.connection.setPlayerLocation(destCoords[0] + 0.5D, destCoords[1], destCoords[2] + 0.5D, player.getRNG().nextFloat() * 360, player.rotationPitch);
                    player.fallDistance = 0.0F;
                    //...then update the age of the fire.
                    int m = initBlockState.get(BlockStateProperties.AGE_0_15);
                    if (m < 2) {
                        world.setBlockState(initBlockPos, initSoul ? Blocks.SOUL_FIRE.getDefaultState() : Blocks.FIRE.getDefaultState());
                    } else {
                        world.setBlockState(initBlockPos, initBlockState.with(BlockStateProperties.AGE_0_15, m == 9 ? 9 : m - 1), 2);
                    }
                }
            }
        });

		ctx.get().setPacketHandled(true);
	}

	public MessageTeleportEntity() {}

    /**
     * Effectively fromBytes from 1.12.2
     */
	public MessageTeleportEntity(ByteBuf buf) {
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

	private boolean isBusyOrIdleFlooBlock(Block b)
    {
        return b == FloocraftBase.GREEN_FLAMES_BUSY
                || b == FloocraftBase.GREEN_FLAMES_IDLE
                || b == FloocraftBase.MAGENTA_FLAMES_BUSY
                || b == FloocraftBase.MAGENTA_FLAMES_IDLE;
    }
}