package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.DataReference;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Thanks to TheGreyGhost for original particle texture stitching code
 */
public class TextureStitcherBreathFX
{
    @SubscribeEvent
    public void stitcherEventPre(TextureStitchEvent.Pre event) {
        ParticleGreenFlame.setFlameSprite(event.getMap().registerSprite(DataReference.FLAMERL));
    }
}