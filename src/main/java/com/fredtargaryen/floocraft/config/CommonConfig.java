package com.fredtargaryen.floocraft.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static ForgeConfigSpec.BooleanValue DEPLETE_FLOO;

    public static ForgeConfigSpec.BooleanValue VILLAGERS_TELEPORT;

    public static ForgeConfigSpec.BooleanValue ITEMS_TELEPORT;

    public static ForgeConfigSpec.BooleanValue MISC_MOBS_TELEPORT;

    public static ForgeConfigSpec.IntValue FLOO_SAFETY_TIME;

    public static void init(ForgeConfigSpec.Builder commonBuilder) {
        DEPLETE_FLOO = commonBuilder.comment("If true, fires from craftable Floo Powder will allow a limited number of teleports before reverting to normal fire.")
                .define("tp.deplete", true);
        VILLAGERS_TELEPORT = commonBuilder.comment("If true, villagers who wander into Floo fires MAY teleport to a random fireplace. Never consumes Floo Powder.")
                .define("tp.villagers", false);
        ITEMS_TELEPORT = commonBuilder.comment("If true, dropped items that touch Floo fires WILL teleport to a random fireplace. Never consumes Floo Powder.")
                .define("tp.items", false);
        MISC_MOBS_TELEPORT = commonBuilder.comment("As with villagers, but for Sheep, Cows, Spiders, Silverfish, Zombies etc. Never consumes Floo Powder.")
                .define("tp.miscmobs", false);
        FLOO_SAFETY_TIME = commonBuilder.comment("When arriving in a normal fire, the fire converts to Floo fire temporarily. It will stay like that for this many ticks, before returning to normal.")
                .defineInRange("tp.safety", 50, 1, 100);
    }
}
