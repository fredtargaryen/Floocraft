package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.item.FlooPowderItem;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FloocraftSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DataReference.MODID);

    // Declare all sound events
    // When an entity is teleported by a Floo Torch
    public static final RegistryObject<SoundEvent> FLICK = SOUNDS.register("flick", () -> SoundEvent.createVariableRangeEvent(DataReference.FLICK_RL));
    // When a fire makes contact with Floo Powder
    public static final RegistryObject<SoundEvent> GREENED = SOUNDS.register("greened", () -> SoundEvent.createVariableRangeEvent(DataReference.GREENED_RL));
    // When teleporting
    public static final RegistryObject<SoundEvent> TP = SOUNDS.register("tp", () -> SoundEvent.createVariableRangeEvent(DataReference.TP_RL));

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}
