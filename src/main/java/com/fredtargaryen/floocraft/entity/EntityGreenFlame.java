package com.fredtargaryen.floocraft.entity;

import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityGreenFlame extends EntityFlameFX
{
    private static final ResourceLocation flame = new ResourceLocation("ftfloocraft", "textures/particle/torchflame.png");

    public EntityGreenFlame(World w, double x, double y, double z)
    {
        super(w, x, y, z, 0.0D, 0.0D, 0.0D);
        this.setParticleTextureIndex(0);
    }

    public void func_180434_a(WorldRenderer wr, Entity e, float p3, float p4, float p5, float p6, float p7, float p8)
    {
        //HOW DO I SET THE PARTICLE TEXTURE??!!?!?!?!?!?!?!?!?!?!?
        super.func_180434_a(wr, e, p3, p4, p5, p6, p7, p8);
    }
}