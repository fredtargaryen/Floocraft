package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.item.FlooPowderItem;
import com.fredtargaryen.floocraft.item.FlooSignItem;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FloocraftItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, DataReference.MODID);

    // Declare all items
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_1 = ITEMS.register("floo_powder_one", () -> new FlooPowderItem((byte) 1));
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_2 = ITEMS.register("floo_powder_two", () -> new FlooPowderItem((byte) 2));
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_4 = ITEMS.register("floo_powder_four", () -> new FlooPowderItem((byte) 4));
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_8 = ITEMS.register("floo_powder_eight", () -> new FlooPowderItem((byte) 8));
    public static final DeferredHolder<Item, FlooPowderItem> FLOO_POWDER_INFINITE = ITEMS.register("floo_powder_infinite", () -> new FlooPowderItem((byte) 0));
    public static final DeferredHolder<Item, FlooSignItem> FLOO_SIGN = ITEMS.register("floo_sign", () -> new FlooSignItem((new Item.Properties()).stacksTo(16)));
    public static final DeferredHolder<Item, Item> FLOO_TORCH = ITEMS.register("floo_torch", () -> new StandingAndWallBlockItem(FloocraftBlocks.FLOO_TORCH.get(), FloocraftBlocks.FLOO_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN));
    public static final DeferredHolder<Item, Item> FLOOWER_POT = ITEMS.register("floower_pot", () -> new BlockItem(FloocraftBlocks.FLOOWER_POT.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
