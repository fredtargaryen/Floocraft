package com.fredtargaryen.floocraft.client.particle;

import com.fredtargaryen.floocraft.DataReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;

/**
 * ALL CODE HERE GRABBED FROM MinecraftByExample BY TheGreyGhost (and very slightly adjusted). THANK YOU!
 */
public class GreenFlameParticle extends SpriteTexturedParticle {
    /**
     * Construct a new FlameFX at the given [x,y,z] position with the given initial velocity.
     */
    public GreenFlameParticle(World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);

        this.particleAlpha = 0.99F;  // a value less than 1 turns on alpha blending. Otherwise, alpha blending is off
        // and the particle won't be transparent.

        //the vanilla EntityFX constructor added random variation to our starting velocity.  Undo it!
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;

        // set the texture to the flame texture, which we have previously added using TextureStitchEvent
        //   (see TextureStitcher)
        //this.setTexture(Minecraft.getInstance().getTextureMap().getAtlasSprite(DataReference.FLAMERL.toString()));
    }

    // can be used to change the brightness of the rendered EntityFX.
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
        super.tick();
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        move(motionX, 0.001, motionZ);
        this.setSize(this.width * 0.99F, this.height * 0.99F);
        if (this.maxAge-- <= 0) {
            this.setExpired();
        }
    }

    @Override
    public void renderParticle(BufferBuilder bufferBuilder, ActiveRenderInfo activeRenderInfo, float v, float v1, float v2, float v3, float v4, float v5) {
        super.renderParticle(bufferBuilder, activeRenderInfo, v, v1, v2, v3, v4, v5);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}