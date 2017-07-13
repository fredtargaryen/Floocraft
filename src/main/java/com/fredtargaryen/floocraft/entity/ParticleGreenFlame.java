package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.DataReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;

/**
 * ALL CODE HERE GRABBED FROM MinecraftByExample BY TheGreyGhost (and very slightly adjusted). THANK YOU!
 */
public class ParticleGreenFlame extends Particle
{
    private static TextureAtlasSprite SPRITE;

    /**
     * Construct a new FlameFX at the given [x,y,z] position with the given initial velocity.
     */
    public ParticleGreenFlame(World world, double x, double y, double z)
    {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);

        this.particleAlpha = 0.99F;  // a value less than 1 turns on alpha blending. Otherwise, alpha blending is off
        // and the particle won't be transparent.

        //the vanilla EntityFX constructor added random variation to our starting velocity.  Undo it!
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;

        // set the texture to the flame texture, which we have previously added using TextureStitchEvent
        //   (see TextureStitcherBreathFX)
        //SPRITE = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(DataReference.FLAMERL.toString());
        SPRITE = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(DataReference.FLAMERL.toString());
        this.setParticleTexture(SPRITE);  // initialise the icon to our custom texture
    }

    /**
     * Used to control what texture and lighting is used for the EntityFX.
     * Returns 1, which means "use a texture from the blocks + items texture sheet"
     * The vanilla layers are:
     * normal particles: ignores world brightness lighting map
     *   Layer 0 - uses the particles texture sheet (textures\particle\particles.png)
     *   Layer 1 - uses the blocks + items texture sheet
     * lit particles: changes brightness depending on world lighting i.e. block light + sky light
     *   Layer 3 - uses the blocks + items texture sheet (I think)
     */
    @Override
    public int getFXLayer()
    {
        return 1;
    }

    // can be used to change the brightness of the rendered EntityFX.
    @Override
    public int getBrightnessForRender(float partialTick)
    {
        //Full brightness
        return 0xf000f0;
    }

    /**
     * call once per tick to update the EntityFX position, calculate collisions, remove when max lifetime is reached, etc
     */
    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        move(motionX, 0.001, motionZ);  // simple linear motion.  You can change speed by changing motionX, motionY,
        // motionZ every tick.  For example - you can make the particle accelerate downwards due to gravity by
        // final double GRAVITY_ACCELERATION_PER_TICK = -0.02;
        // motionY += GRAVITY_ACCELERATION_PER_TICK;
        this.particleScale *= 0.95;
        if (this.particleMaxAge-- <= 0) {
            this.setExpired();
        }
    }

    public static void setFlameSprite(TextureAtlasSprite tas)
    {
        SPRITE = tas;
    }
}