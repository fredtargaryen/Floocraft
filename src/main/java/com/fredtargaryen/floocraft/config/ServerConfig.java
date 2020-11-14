package com.fredtargaryen.floocraft.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static ForgeConfigSpec.BooleanValue VILLAGERS_TELEPORT;

    public static ForgeConfigSpec.BooleanValue ITEMS_TELEPORT;

    public static ForgeConfigSpec.BooleanValue MISC_MOBS_TELEPORT;

    public static void init(ForgeConfigSpec.Builder serverBuilder) {
        VILLAGERS_TELEPORT = serverBuilder.comment("If true, villagers who wander into Floo fires MAY teleport to a random fireplace. Never consumes Floo Powder.")
                .define("tp.villagers", false);
        ITEMS_TELEPORT = serverBuilder.comment("If true, dropped items that touch Floo fires WILL teleport to a random fireplace. Never consumes Floo Powder.")
                .define("tp.items", false);
        MISC_MOBS_TELEPORT = serverBuilder.comment("As with villagers, but for Sheep, Cows, Spiders, Silverfish, Zombies etc. Never consumes Floo Powder.")
                .define("tp.miscmobs", false);
    }
}
