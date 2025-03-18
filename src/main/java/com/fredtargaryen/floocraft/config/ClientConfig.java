package com.fredtargaryen.floocraft.config;

import com.fredtargaryen.floocraft.DataReference;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = DataReference.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue _ENABLE_DIZZY = BUILDER
            .comment("If true, there is a disorienting camera-wobbling effect when you teleport with Floo Powder.")
            .translation("config.client.dizzy")
            .define("effect.dizzy", true);

    private static final ModConfigSpec.BooleanValue _ENABLE_FLASH = BUILDER
            .comment("If true, there is a green or magenta flash when you teleport with Floo Powder.")
            .translation("config.client.flash")
            .define("effect.flash", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean ENABLE_DIZZY;
    public static boolean ENABLE_FLASH;

    @SubscribeEvent
    static void onLoad(final FMLClientSetupEvent event) {
        ENABLE_DIZZY = _ENABLE_DIZZY.get();
        ENABLE_FLASH = _ENABLE_FLASH.get();
    }
}
