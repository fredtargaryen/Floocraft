package com.fredtargaryen.floocraft.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class GreenFlameParticle extends SpriteTexturedParticle {
    private GreenFlameParticle(World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite spriteSet) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSprite(spriteSet.get(0, 50));
        this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
    }

    public float getScale(float p_217561_1_) {
        float f = ((float)this.age + p_217561_1_) / (float)this.maxAge;
        return this.particleScale * (1.0F - f * f * 0.5F);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        //Full brightness
        return 0xf000f0;
    }

    /**
     * call once per tick to update the EntityFX position, calculate collisions, remove when max lifetime is reached, etc
     */
    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder bufferBuilder, ActiveRenderInfo activeRenderInfo, float v, float v1, float v2, float v3, float v4, float v5) {
        super.renderParticle(bufferBuilder, activeRenderInfo, v, v1, v2, v3, v4, v5);
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        private IAnimatedSprite sprites;

        public Factory(IAnimatedSprite sprites) {
            this.sprites = sprites;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new GreenFlameParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}