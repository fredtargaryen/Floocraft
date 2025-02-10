package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.block.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FloocraftBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DataReference.MODID);

    // Declare all blocks
    public static final RegistryObject<Block> FLOO_CAMPFIRE = BLOCKS.register("floo_campfire", () -> new FlooCampfireBlock(15));
    public static final RegistryObject<Block> FLOO_FLAMES = BLOCKS.register("floo_flames", FlooFlames::new);
    public static final RegistryObject<Block> FLOO_SIGN = BLOCKS.register("floo_sign", FlooSignBlock::new);
    public static final RegistryObject<Block> FLOO_SOUL_CAMPFIRE = BLOCKS.register("floo_soul_campfire", () -> new FlooCampfireBlock(10));
    public static final RegistryObject<Block> FLOO_TORCH = BLOCKS.register("floo_torch", FlooTorchBlock::new);
    public static final RegistryObject<Block> FLOO_WALL_TORCH = BLOCKS.register("floo_wall_torch", FlooWallTorchBlock::new);

    public static final TagKey<Block> ARRIVAL_BLOCKS = BlockTags.create(DataReference.VALID_ARRIVAL_BLOCKS);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
