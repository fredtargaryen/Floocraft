package com.fredtargaryen.floocraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FloocraftSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, DataReference.MODID);

    // Declare all sound events
    // When an entity is teleported by a Floo Torch
    public static final DeferredHolder<SoundEvent, SoundEvent> FLICK = SOUNDS.register("flick", () -> SoundEvent.createVariableRangeEvent(DataReference.FLICK_RL));
    // When a fire makes contact with Floo Powder
    public static final DeferredHolder<SoundEvent, SoundEvent> GREENED = SOUNDS.register("greened", () -> SoundEvent.createVariableRangeEvent(DataReference.GREENED_RL));
    // When teleporting
    public static final DeferredHolder<SoundEvent, SoundEvent> TP = SOUNDS.register("tp", () -> SoundEvent.createVariableRangeEvent(DataReference.TP_RL));

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}
