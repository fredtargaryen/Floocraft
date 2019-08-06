package com.fredtargaryen.floocraft.client.renderer;

import com.fredtargaryen.floocraft.entity.PeekerEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderPeekerFactory implements IRenderFactory<PeekerEntity> {
    @Override
    public EntityRenderer<? super PeekerEntity> createRenderFor(EntityRendererManager manager) {
        return new RenderPeeker(manager);
    }
}