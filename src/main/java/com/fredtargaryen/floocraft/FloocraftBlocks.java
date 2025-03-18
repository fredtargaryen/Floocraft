package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.block.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FloocraftBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, DataReference.MODID);

    // Declare all blocks
    public static final DeferredHolder<Block, FlooCampfireBlock> FLOO_CAMPFIRE = BLOCKS.register("floo_campfire", () -> new FlooCampfireBlock(15));
    public static final DeferredHolder<Block, FlooFlamesBlock> FLOO_FLAMES = BLOCKS.register("floo_flames", FlooFlamesBlock::new);
    public static final DeferredHolder<Block, FlooSignBlock> FLOO_SIGN = BLOCKS.register("floo_sign", FlooSignBlock::new);
    public static final DeferredHolder<Block, FlooCampfireBlock> FLOO_SOUL_CAMPFIRE = BLOCKS.register("floo_soul_campfire", () -> new FlooCampfireBlock(10));
    public static final DeferredHolder<Block, FlooTorchBlock> FLOO_TORCH = BLOCKS.register("floo_torch", FlooTorchBlock::new);
    public static final DeferredHolder<Block, FlooWallTorchBlock> FLOO_WALL_TORCH = BLOCKS.register("floo_wall_torch", FlooWallTorchBlock::new);
    public static final DeferredHolder<Block, FloowerPotBlock> FLOOWER_POT = BLOCKS.register("floower_pot", () -> new FloowerPotBlock());

    public static final TagKey<Block> ARRIVAL_BLOCKS = BlockTags.create(DataReference.VALID_ARRIVAL_BLOCKS);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
