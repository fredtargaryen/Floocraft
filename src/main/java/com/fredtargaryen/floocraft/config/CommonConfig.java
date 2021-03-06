package com.fredtargaryen.floocraft.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static ForgeConfigSpec.BooleanValue DEPLETE_FLOO;

    public static ForgeConfigSpec.BooleanValue VILLAGERS_TELEPORT;

    public static ForgeConfigSpec.BooleanValue ITEMS_TELEPORT;

    public static ForgeConfigSpec.BooleanValue MISC_MOBS_TELEPORT;

    public static void init(ForgeConfigSpec.Builder commonBuilder) {
        DEPLETE_FLOO = commonBuilder.comment("If true, teleporting will decrease the number of remaining teleports in a Floo fire until it reverts to normal fire.")
                .define("tp.deplete", true);
        VILLAGERS_TELEPORT = commonBuilder.comment("If true, villagers who wander into Floo fires MAY teleport to a random fireplace. Never consumes Floo Powder.")
                .define("tp.villagers", false);
        ITEMS_TELEPORT = commonBuilder.comment("If true, dropped items that touch Floo fires WILL teleport to a random fireplace. Never consumes Floo Powder.")
                .define("tp.items", false);
        MISC_MOBS_TELEPORT = commonBuilder.comment("As with villagers, but for Sheep, Cows, Spiders, Silverfish, Zombies etc. Never consumes Floo Powder.")
                .define("tp.miscmobs", false);
    }
}
