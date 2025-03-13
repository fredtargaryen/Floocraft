package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import com.fredtargaryen.floocraft.blockentity.FloowerPotBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FloocraftBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DataReference.MODID);

    // Declare all block entity types
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FlooSignBlockEntity>> FLOO_SIGN = BLOCK_ENTITY_TYPES.register("floo_sign", () -> BlockEntityType.Builder.of(FlooSignBlockEntity::new, FloocraftBlocks.FLOO_SIGN.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FloowerPotBlockEntity>> FLOOWER_POT = BLOCK_ENTITY_TYPES.register("floower_pot", () -> BlockEntityType.Builder.of(FloowerPotBlockEntity::new, FloocraftBlocks.FLOOWER_POT.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
