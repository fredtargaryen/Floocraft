package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FloocraftBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DataReference.MODID);

    // Declare all blocks
    public static final RegistryObject<BlockEntityType<FlooSignBlockEntity>> FLOO_SIGN = BLOCK_ENTITY_TYPES.register("floo_sign", () -> BlockEntityType.Builder.of(FlooSignBlockEntity::new, FloocraftBlocks.FLOO_SIGN.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
