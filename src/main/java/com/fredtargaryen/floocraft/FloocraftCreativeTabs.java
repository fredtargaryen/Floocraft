package com.fredtargaryen.floocraft;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.fredtargaryen.floocraft.FloocraftItems.*;

public class FloocraftCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DataReference.MODID);

    // Declare all creative tabs
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("floocraft", () -> CreativeModeTab.builder()
            .title(Component.translatable("gui.floocraft_tab"))
            .icon(() -> FLOO_POWDER_1.get().getDefaultInstance())
            .displayItems((parameters, output) -> { // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(FLOO_POWDER_1.get());
                output.accept(FLOO_POWDER_2.get());
                output.accept(FLOO_POWDER_4.get());
                output.accept(FLOO_POWDER_8.get());
                output.accept(FLOO_POWDER_INFINITE.get());
                output.accept(FLOO_SIGN.get());
                output.accept(FLOO_TORCH.get());
            }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
