package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.network.ChunkManager;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageEndPeek;
import com.fredtargaryen.floocraft.network.messages.MessagePlayerIDRequest;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class EntityPeeker extends Entity {
    private UUID playerUUID;
    //For the client side
    private ResourceLocation texture;
    private boolean sentRequest;

    public EntityPeeker(World w) {
        super(w);
        this.setSize(0.5F, 0.5F);
        this.isImmuneToFire = true;
        MinecraftForge.EVENT_BUS.register(this);
        this.texture = null;
        this.sentRequest = false;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPeekerData(EntityPlayerMP player, BlockPos spawnPos, EnumFacing direction) {
        BlockPos landPos = spawnPos.offset(direction);
        float x = landPos.getX() + 0.5F;
        float y = landPos.getY();
        float z = landPos.getZ() + 0.5F;
        switch (direction) {
            case NORTH:
                z += 0.5F;
                break;
            case SOUTH:
                z -= 0.5F;
                break;
            case EAST:
                x -= 0.5F;
                break;
            default:
                x += 0.5;
                break;
        }
        this.playerUUID = player.getPersistentID();
        this.setLocationAndAngles(x, y, z, this.getYawFromDirection(direction), 0.0F);
    }

    public void setPlayerUUID(UUID uuid) {
        this.playerUUID = uuid;
    }

    private float getYawFromDirection(EnumFacing ef) {
        switch (ef) {
            case NORTH:
                return 180.0F;
            case EAST:
                return -90.0F;
            case SOUTH:
                return 0.0F;
            default:
                return 90.0F;
        }
    }

    public void setDead() {
        super.setDead();
        ChunkManager.releaseTicket(this);
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    protected void entityInit() {
    }

    /**
     * Gets called every tick from main Entity class
     * Kills itself if the player it is tied to is dead or not connected
     */
    @Override
    public void onEntityUpdate() {
        if (!this.world.isRemote) {
            EntityPlayer player = this.world.getPlayerEntityByUUID(this.playerUUID);
            if (player == null || player.isDead) {
                this.setDead();
            }
        }
    }

    public ResourceLocation getTexture() {
        if (this.playerUUID == null) {
            if (this.world.isRemote) {
                //Client; needs to send one request message. PlayerUUID will be set by MessagePlayerID
                if (!this.sentRequest) {
                    MessagePlayerIDRequest mpidr = new MessagePlayerIDRequest();
                    mpidr.peekerUUID = this.getUniqueID();
                    PacketHandler.INSTANCE.sendToServer(mpidr);
                }
            }
            return null;
        }
        if (this.texture == null && this.world.isRemote) {
            this.texture = ((AbstractClientPlayer) this.world.getPlayerEntityByUUID(this.playerUUID)).getLocationSkin();
        }
        return this.texture;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.playerUUID = new UUID(compound.getLong("msb"), compound.getLong("lsb"));
        this.setRotation(compound.getFloat("yaw"), 0.0F);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setLong("msb", this.playerUUID.getMostSignificantBits());
        compound.setLong("lsb", this.playerUUID.getLeastSignificantBits());
        compound.setFloat("yaw", this.rotationYaw);
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent lhe) {
        if (this.world != null && this.world.isRemote && this.playerUUID != null) {
            UUID hurtEntityUUID = lhe.getEntity().getUniqueID();
            if(hurtEntityUUID.equals(this.playerUUID)) {
                MessageEndPeek mep = new MessageEndPeek();
                mep.peekerUUID = this.getUniqueID();
                PacketHandler.INSTANCE.sendToServer(mep);
            }
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent lde) {
        if (this.world != null && this.world.isRemote && this.playerUUID != null && lde.getEntity().getUniqueID().equals(this.playerUUID)) {
            MessageEndPeek mep = new MessageEndPeek();
            mep.peekerUUID = this.getUniqueID();
            PacketHandler.INSTANCE.sendToServer(mep);
        }
    }
}