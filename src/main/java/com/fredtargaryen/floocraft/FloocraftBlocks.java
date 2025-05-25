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
    public static final String FLOO_CAMPFIRE_RL = "floo_campfire";
    public static final DeferredHolder<Block, FlooCampfireBlock> FLOO_CAMPFIRE = BLOCKS.register(FLOO_CAMPFIRE_RL, () -> new FlooCampfireBlock(15, FLOO_CAMPFIRE_RL));
    public static final String FLOO_FLAMES_RL = "floo_flames";
    public static final DeferredHolder<Block, FlooFlamesBlock> FLOO_FLAMES = BLOCKS.register(FLOO_FLAMES_RL, FlooFlamesBlock::new);
    public static final String FLOO_SIGN_RL = "floo_sign";
    public static final DeferredHolder<Block, FlooSignBlock> FLOO_SIGN = BLOCKS.register(FLOO_SIGN_RL, FlooSignBlock::new);
    public static final String FLOO_SOUL_CAMPFIRE_RL = "floo_soul_campfire";
    public static final DeferredHolder<Block, FlooCampfireBlock> FLOO_SOUL_CAMPFIRE = BLOCKS.register(FLOO_SOUL_CAMPFIRE_RL, () -> new FlooCampfireBlock(10, FLOO_SOUL_CAMPFIRE_RL));
    public static final String FLOO_TORCH_RL = "floo_torch";
    public static final DeferredHolder<Block, FlooTorchBlock> FLOO_TORCH = BLOCKS.register(FLOO_TORCH_RL, FlooTorchBlock::new);
    public static final String FLOO_WALL_TORCH_RL = "floo_wall_torch";
    public static final DeferredHolder<Block, FlooWallTorchBlock> FLOO_WALL_TORCH = BLOCKS.register(FLOO_WALL_TORCH_RL, FlooWallTorchBlock::new);
    public static final String FLOOWER_POT_RL = "floower_pot";
    public static final DeferredHolder<Block, FloowerPotBlock> FLOOWER_POT = BLOCKS.register(FLOOWER_POT_RL, () -> new FloowerPotBlock());

    public static final TagKey<Block> ARRIVAL_BLOCKS = BlockTags.create(DataReference.VALID_ARRIVAL_BLOCKS);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
