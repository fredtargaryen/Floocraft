package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.item.FlooPowderItem;
import com.fredtargaryen.floocraft.item.FlooSignItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FloocraftItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DataReference.MODID);

    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    //public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // Declare all items
    public static final RegistryObject<Item> FLOO_POWDER_1 = ITEMS.register("floo_powder_one", () -> new FlooPowderItem((byte) 1));
    public static final RegistryObject<Item> FLOO_POWDER_2 = ITEMS.register("floo_powder_two", () -> new FlooPowderItem((byte) 2));
    public static final RegistryObject<Item> FLOO_POWDER_4 = ITEMS.register("floo_powder_four", () -> new FlooPowderItem((byte) 4));
    public static final RegistryObject<Item> FLOO_POWDER_8 = ITEMS.register("floo_powder_eight", () -> new FlooPowderItem((byte) 8));
    public static final RegistryObject<Item> FLOO_POWDER_INFINITE = ITEMS.register("floo_powder_infinite", () -> new FlooPowderItem((byte) 9));
    public static final RegistryObject<Item> FLOO_SIGN = ITEMS.register("floo_sign", () -> new FlooSignItem((new Item.Properties()).stacksTo(16)));
    public static final RegistryObject<Item> FLOO_TORCH = ITEMS.register("floo_torch", () -> new StandingAndWallBlockItem(FloocraftBlocks.FLOO_TORCH.get(), FloocraftBlocks.FLOO_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
