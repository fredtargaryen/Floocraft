package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.DataReference;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Thanks to TheGreyGhost for original particle texture stitching code
 */
public class TextureStitcherBreathFX
{
    @SubscribeEvent
    public void stitcherEventPre(TextureStitchEvent.Pre event) { //TODO
        //ParticleGreenFlame.setFlameSprite(event.getMap().registerSprite(DataReference.FLAMERL));
    }
}