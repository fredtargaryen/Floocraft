package com.fredtargaryen.floocraft.config;

import com.fredtargaryen.floocraft.DataReference;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = DataReference.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue _DEPLETE_FLOO = BUILDER
            .comment("If true, fires from craftable Floo Powder will allow a limited number of teleports before reverting to normal fire.")
            .translation("config.common.deplete")
            .define("tp.deplete", true);

    private static final ModConfigSpec.BooleanValue _VILLAGERS_TELEPORT = BUILDER
            .comment("If true, villagers who wander into Floo fires MAY teleport to a random fireplace. Never consumes Floo Powder.")
            .translation("config.common.villager_tp")
            .define("tp.villagers", false);

    private static final ModConfigSpec.BooleanValue _ITEMS_TELEPORT = BUILDER
            .comment("If true, dropped items that touch Floo fires WILL teleport to a random fireplace. Never consumes Floo Powder.")
            .translation("config.common.item_tp")
            .define("tp.items", false);

    private static final ModConfigSpec.BooleanValue _MISC_MOBS_TELEPORT = BUILDER
            .comment("As with villagers, but for Sheep, Cows, Spiders, Silverfish, Zombies etc. Never consumes Floo Powder.")
            .translation("config.common.mob_tp")
            .define("tp.miscmobs", false);

    private static final ModConfigSpec.IntValue _FLOO_SAFETY_TIME = BUILDER
            .comment("When arriving in a normal fire, the fire converts to Floo fire temporarily. It will stay like that for this many ticks, before returning to normal.")
            .translation("config.common.safety")
            .defineInRange("tp.safety", 60, 1, 150);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean DEPLETE_FLOO;
    public static boolean VILLAGERS_TELEPORT;
    public static boolean ITEMS_TELEPORT;
    public static boolean MISC_MOBS_TELEPORT;
    public static int FLOO_SAFETY_TIME;

    @SubscribeEvent
    static void commonSetup(final FMLCommonSetupEvent event) {
        DEPLETE_FLOO = _DEPLETE_FLOO.get();
        VILLAGERS_TELEPORT = _VILLAGERS_TELEPORT.get();
        ITEMS_TELEPORT = _ITEMS_TELEPORT.get();
        MISC_MOBS_TELEPORT = _MISC_MOBS_TELEPORT.get();
        FLOO_SAFETY_TIME = _FLOO_SAFETY_TIME.get();
    }
}
