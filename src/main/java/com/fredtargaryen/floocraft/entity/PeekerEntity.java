package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.MessageEndPeek;
import com.fredtargaryen.floocraft.network.messages.MessagePlayerIDRequest;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PeekerEntity extends Entity {
    private UUID playerUUID;
    //For the client side
    private ResourceLocation texture;
    private boolean sentRequest;

    public PeekerEntity(World w) {
        super(FloocraftBase.PEEKER_TYPE.get(), w);
        MinecraftForge.EVENT_BUS.register(this);
        this.texture = null;
        this.sentRequest = false;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPeekerData(PlayerEntity player, BlockPos spawnPos, Direction direction) {
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
        this.playerUUID = player.getUniqueID();
        this.setLocationAndAngles(x, y, z, this.getYawFromDirection(direction), 0.0F);
    }

    public void setPlayerUUID(UUID uuid) {
        this.playerUUID = uuid;
    }

    private float getYawFromDirection(Direction ef) {
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

    @Override
    public void remove() {
        super.remove();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    /**
     * Gets called every tick from main Entity class
     * Kills itself if the player it is tied to is dead or not connected
     */
    @Override
    public void tick() {
        if (!this.world.isRemote) {
            PlayerEntity player = (PlayerEntity) ((ServerWorld)this.world).getEntityByUuid(this.playerUUID);
            if (player == null || !player.isAlive()) {
                this.remove();
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
                    MessageHandler.INSTANCE.sendToServer(mpidr);
                }
            }
            return null;
        }
        if (this.texture == null && this.world.isRemote) {
            AbstractClientPlayerEntity acpe = (AbstractClientPlayerEntity) this.world.getPlayerByUuid(this.playerUUID);
            // If we can't find the player with this UUID they probably don't exist now, so we shouldn't render their peeker
            this.texture = acpe == null ? null : acpe.getLocationSkin();
        }
        return this.texture;
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent lhe) {
        if (this.world != null && this.world.isRemote && this.playerUUID != null) {
            UUID hurtEntityUUID = lhe.getEntity().getUniqueID();
            if(hurtEntityUUID.equals(this.playerUUID)) {
                MessageEndPeek mep = new MessageEndPeek();
                mep.peekerUUID = this.getUniqueID();
                MessageHandler.INSTANCE.sendToServer(mep);
            }
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent lde) {
        if (this.world != null && this.world.isRemote && this.playerUUID != null && lde.getEntity().getUniqueID().equals(this.playerUUID)) {
            MessageEndPeek mep = new MessageEndPeek();
            mep.peekerUUID = this.getUniqueID();
            MessageHandler.INSTANCE.sendToServer(mep);
        }
    }

    @Override
    protected void registerData() {

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     *
     * @param compound
     */
    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.playerUUID = new UUID(compound.getLong("msb"), compound.getLong("lsb"));
        this.setRotation(compound.getFloat("yaw"), 0.0F);
    }

    /**
     * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
     * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
     *
     * @param compound the compound to be written into
     */
    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putLong("msb", this.playerUUID.getMostSignificantBits());
        compound.putLong("lsb", this.playerUUID.getLeastSignificantBits());
        compound.putFloat("yaw", this.rotationYaw);
    }

    @Override
    @Nonnull
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}