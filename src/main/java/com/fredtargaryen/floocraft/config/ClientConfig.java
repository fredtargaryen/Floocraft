package com.fredtargaryen.floocraft.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static ForgeConfigSpec.BooleanValue ENABLE_DIZZY;

    public static ForgeConfigSpec.BooleanValue ENABLE_FLASH;

    public static void init(ForgeConfigSpec.Builder clientBuilder) {
        ENABLE_DIZZY = clientBuilder.comment("If true, there is a disorienting camera-wobbling effect when you teleport with Floo Powder.")
                .translation("config.client.dizzy")
                .define("effect.dizzy", true);
        ENABLE_FLASH = clientBuilder.comment("If true, there is a green or magenta flash when you teleport with Floo Powder.")
                .translation("config.client.flash")
                .define("effect.flash", true);
    }
}
