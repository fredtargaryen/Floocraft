package com.fredtargaryen.floocraft.config;

import com.fredtargaryen.floocraft.DataReference;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DataReference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue _ENABLE_DIZZY = BUILDER
            .comment("If true, there is a disorienting camera-wobbling effect when you teleport with Floo Powder.")
            .translation("config.client.dizzy")
            .define("effect.dizzy", true);

    private static final ForgeConfigSpec.BooleanValue _ENABLE_FLASH = BUILDER
            .comment("If true, there is a green or magenta flash when you teleport with Floo Powder.")
            .translation("config.client.flash")
            .define("effect.flash", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean ENABLE_DIZZY;
    public static boolean ENABLE_FLASH;

    @SubscribeEvent
    static void onLoad(final FMLClientSetupEvent event) {
        ENABLE_DIZZY = _ENABLE_DIZZY.get();
        ENABLE_FLASH = _ENABLE_FLASH.get();
    }
}
