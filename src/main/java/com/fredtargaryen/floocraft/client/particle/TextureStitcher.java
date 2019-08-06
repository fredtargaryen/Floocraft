package com.fredtargaryen.floocraft.client.particle;

import com.fredtargaryen.floocraft.DataReference;
import com.sun.prism.TextureMap;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Thanks to TheGreyGhost for original particle texture stitching code
 */
@Mod.EventBusSubscriber(modid = DataReference.MODID, value = Dist.CLIENT)
public class TextureStitcher {
    @SubscribeEvent
    public void stitch(TextureStitchEvent.Pre event) {
//        TextureMap textureMap = event.getMap();
//        textureMap.registerSprite(Minecraft.getInstance().getResourceManager(), DataReference.FLAMERL);
    }
}