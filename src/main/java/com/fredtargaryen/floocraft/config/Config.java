package com.fredtargaryen.floocraft.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

/**
 * Replaces the Configuration system of Forge 1.12.2.
 */
@Mod.EventBusSubscriber
public class Config {
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec SERVER_CONFIG_SPEC;

    static {
        GeneralConfig.init(SERVER_BUILDER);
        SERVER_CONFIG_SPEC = SERVER_BUILDER.build();
    }

    public static void loadConfig(Path path)
    {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        SERVER_CONFIG_SPEC.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading loadEvent) {}

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {}
}
