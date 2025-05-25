package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.item.FlooPowderItem;
import com.fredtargaryen.floocraft.item.FlooSignItem;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FloocraftItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, DataReference.MODID);

    // Declare all items
    public static final String FLOO_POWDER_1_RL = "floo_powder_one";
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_1 = ITEMS.register(FLOO_POWDER_1_RL, () -> new FlooPowderItem((byte) 1, FLOO_POWDER_1_RL));
    public static final String FLOO_POWDER_2_RL = "floo_powder_two";
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_2 = ITEMS.register(FLOO_POWDER_2_RL, () -> new FlooPowderItem((byte) 2, FLOO_POWDER_2_RL));
    public static final String FLOO_POWDER_4_RL = "floo_powder_four";
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_4 = ITEMS.register(FLOO_POWDER_4_RL, () -> new FlooPowderItem((byte) 4, FLOO_POWDER_4_RL));
    public static final String FLOO_POWDER_8_RL = "floo_powder_eight";
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_8 = ITEMS.register(FLOO_POWDER_8_RL, () -> new FlooPowderItem((byte) 8, FLOO_POWDER_8_RL));
    public static final String FLOO_POWDER_INF_RL = "floo_powder_infinite";
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_INFINITE = ITEMS.register(FLOO_POWDER_INF_RL, () -> new FlooPowderItem((byte) 0, FLOO_POWDER_INF_RL));
    public static final String FLOO_SIGN_RL = "floo_sign";
    public static final DeferredHolder<Item, FlooSignItem> FLOO_SIGN = ITEMS.register(FLOO_SIGN_RL, () -> new FlooSignItem((new Item.Properties()).stacksTo(16)));
    public static final String FLOO_TORCH_RL = "floo_torch";
    public static final DeferredHolder<Item, Item> FLOO_TORCH = ITEMS.register(FLOO_TORCH_RL, () -> new StandingAndWallBlockItem(FloocraftBlocks.FLOO_TORCH.get(), FloocraftBlocks.FLOO_WALL_TORCH.get(), Direction.DOWN,
            new Item.Properties()
                    .useBlockDescriptionPrefix()
                    .setId(ResourceKey.create(Registries.ITEM, DataReference.getResourceLocation(FloocraftItems.FLOO_TORCH_RL)))));
    public static final String FLOOWER_POT_RL = "floower_pot";
    public static final DeferredHolder<Item, Item> FLOOWER_POT = ITEMS.register(FLOOWER_POT_RL, () -> new BlockItem(FloocraftBlocks.FLOOWER_POT.get(),
            new Item.Properties()
                    .useBlockDescriptionPrefix()
                    .setId(ResourceKey.create(Registries.ITEM, DataReference.getResourceLocation(FloocraftItems.FLOOWER_POT_RL)))));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
