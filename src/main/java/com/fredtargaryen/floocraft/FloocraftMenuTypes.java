package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.inventory.FloowerPotMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FloocraftMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, DataReference.MODID);

    // Declare all particle types
    public static final DeferredHolder<MenuType<?>, MenuType<FloowerPotMenu>> FLOOWER_POT = MENU_TYPES.register("floower_pot", () -> new MenuType<>(FloowerPotMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
