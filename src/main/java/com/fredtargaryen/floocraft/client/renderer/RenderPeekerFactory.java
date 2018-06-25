package com.fredtargaryen.floocraft.client.renderer;

import com.fredtargaryen.floocraft.entity.EntityPeeker;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderPeekerFactory implements IRenderFactory<EntityPeeker>
{
    @Override
    public Render<? super EntityPeeker> createRenderFor(RenderManager manager)
    {
        return new RenderPeeker(manager);
    }
}