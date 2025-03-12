package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.PeekEndMessage;
import com.fredtargaryen.floocraft.network.messages.PeekerInfoRequestMessage;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;

import java.util.UUID;

public class PeekerEntity extends Entity {
    private UUID playerUUID;
    //For the client side
    private ResourceLocation texture;
    private boolean sentRequest;

    public PeekerEntity(EntityType<? extends PeekerEntity> entityType, Level level) {
        super(entityType, level);
        NeoForge.EVENT_BUS.register(this);
        this.texture = null;
        this.sentRequest = false;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public void setPeekerData(Player player, BlockPos spawnPos, Direction direction) {
        BlockPos landPos = spawnPos.relative(direction);
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
        this.playerUUID = player.getUUID();

        this.setPos(x, y, z);
        this.setRot(this.getYawFromDirection(direction), 0.0F);
    }

    public void setPlayerUUID(UUID uuid) {
        this.playerUUID = uuid;
    }

    private float getYawFromDirection(Direction ef) {
        return switch (ef) {
            case NORTH -> 180.0F;
            case EAST -> -90.0F;
            case SOUTH -> 0.0F;
            default -> 90.0F;
        };
    }

    @Override
    public void remove(RemovalReason reason) {
        NeoForge.EVENT_BUS.unregister(this);
        super.remove(reason);
    }

    /**
     * Gets called every tick from main Entity class
     * Kills itself if the player it is tied to is dead or not connected
     */
    @Override
    public void tick() {
        Level level = this.level();
        if (level != null && !level.isClientSide) {
            Player player = (Player) ((ServerLevel) level).getEntity(this.playerUUID);
            if (player == null || !player.isAlive()) {
                this.remove(RemovalReason.UNLOADED_WITH_PLAYER);
            }
        }
    }

    public ResourceLocation getTexture() {
        if (this.playerUUID == null) {
            if (this.level().isClientSide) {
                //Client; needs to send one request message. PlayerUUID will be set by MessagePlayerID
                if (!this.sentRequest) {
                    PeekerInfoRequestMessage message = new PeekerInfoRequestMessage(this.uuid.getMostSignificantBits(), this.uuid.getLeastSignificantBits());
                    MessageHandler.sendToServer(message);
                }
            }
            return null;
        }
        if (this.texture == null && this.level().isClientSide) {
            AbstractClientPlayer acp = (AbstractClientPlayer) this.level().getPlayerByUUID(this.playerUUID);
            // If we can't find the player with this UUID they probably don't exist now, so we shouldn't render their peeker
            this.texture = acp == null ? null : acp.getSkin().texture();
        }
        return this.texture;
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent lhe) {
        if (this.level() != null && this.level().isClientSide && this.playerUUID != null) {
            UUID hurtEntityUUID = lhe.getEntity().getUUID();
            if (hurtEntityUUID.equals(this.playerUUID)) {
                PeekEndMessage message = new PeekEndMessage(this.uuid.getMostSignificantBits(), this.uuid.getLeastSignificantBits());
                MessageHandler.sendToServer(message);
            }
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent lde) {
        if (this.level() != null && this.level().isClientSide && this.playerUUID != null && lde.getEntity().getUUID().equals(this.playerUUID)) {
            PeekEndMessage message = new PeekEndMessage(this.uuid.getMostSignificantBits(), this.uuid.getLeastSignificantBits());
            MessageHandler.sendToServer(message);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.playerUUID = new UUID(compound.getLong("playerMsb"), compound.getLong("playerLsb"));
        this.setRot(compound.getFloat("yaw"), 0.0F);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putLong("playerMsb", this.playerUUID.getMostSignificantBits());
        compound.putLong("playerLsb", this.playerUUID.getLeastSignificantBits());
        compound.putFloat("yaw", this.getYRot());
    }
}