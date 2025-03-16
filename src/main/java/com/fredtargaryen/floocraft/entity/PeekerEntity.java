package com.fredtargaryen.floocraft.entity;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
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

import java.util.Optional;
import java.util.UUID;

public class PeekerEntity extends Entity {
    // Synced data
    private static final EntityDataAccessor<Optional<UUID>> PEEKER_ID = SynchedEntityData.defineId(PeekerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> PLAYER_ID = SynchedEntityData.defineId(PeekerEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    // Only used on the client side
    private Optional<ResourceLocation> texture;

    public PeekerEntity(EntityType<? extends PeekerEntity> entityType, Level level) {
        super(entityType, level);
        this.texture = Optional.empty();
    }

    public Optional<UUID> getPeekerUUID() {
        return this.entityData.get(PEEKER_ID);
    }

    public Optional<UUID> getPlayerUUID() {
        return this.entityData.get(PLAYER_ID);
    }

    public void setPeekerData(Player player, BlockPos spawnPos, Direction direction) {
        this.entityData.set(PEEKER_ID, Optional.of(this.uuid));
        this.entityData.set(PLAYER_ID, Optional.of(player.getUUID()));

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
        this.setPos(x, y, z);
        this.setRot(this.getYawFromDirection(direction), 0.0F);
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
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level().isClientSide) NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (!this.level().isClientSide) NeoForge.EVENT_BUS.unregister(this);
    }

    /**
     * Gets called every tick from main Entity class
     * Kills itself if the player it is tied to is dead or not connected
     */
    @Override
    public void tick() {
        Level level = this.level();
        if (level != null && !level.isClientSide) {
            this.getPlayerUUID().ifPresent(playerUUID -> {
                Player player = (Player) ((ServerLevel) level).getEntity(playerUUID);
                if (player == null || !player.isAlive()) this.remove(RemovalReason.UNLOADED_WITH_PLAYER);
            });
        }
    }

    public Optional<ResourceLocation> getTexture() {
        if (this.level().isClientSide && this.texture.isEmpty()) {
            this.getPlayerUUID().ifPresent(playerUUID -> {
                AbstractClientPlayer acp = (AbstractClientPlayer) this.level().getPlayerByUUID(playerUUID);
                // If we can't find the player with this UUID they probably don't exist now, so we shouldn't render their peeker
                this.texture = acp == null ? Optional.empty() : Optional.of(acp.getSkin().texture());
            });
        }
        return this.texture;
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent lhe) {
        if (this.level() != null && !this.level().isClientSide && this.getPlayerUUID().isPresent()) {
            UUID hurtEntityUUID = lhe.getEntity().getUUID();
            if (hurtEntityUUID.equals(this.getPlayerUUID().get())) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent lde) {
        if (this.level() != null && !this.level().isClientSide && this.getPlayerUUID().isPresent() && lde.getEntity().getUUID().equals(this.getPlayerUUID().get())) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(PEEKER_ID, Optional.of(this.uuid));
        builder.define(PLAYER_ID, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }
}