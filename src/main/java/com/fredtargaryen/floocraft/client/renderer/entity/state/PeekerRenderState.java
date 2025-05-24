package com.fredtargaryen.floocraft.client.renderer.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class PeekerRenderState extends EntityRenderState {
    public Optional<PlayerSkin> skin = Optional.empty();
    public float yRot;
}
