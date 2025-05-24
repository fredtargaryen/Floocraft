package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.entity.PeekerEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FloocraftEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, DataReference.MODID);

    // Declare all entity types
    public static final DeferredHolder<EntityType<?>, EntityType<PeekerEntity>> PEEKER = ENTITY_TYPES.register("peeker",
            () -> EntityType.Builder.of(PeekerEntity::new, MobCategory.MISC)
                    .sized(1f, 0.6f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, DataReference.getResourceLocation("peeker"))));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
