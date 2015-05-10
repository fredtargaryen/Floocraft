package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.DataReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityGreenFlame extends EntityFX
{
    /** the scale of the flame FX */
    private float flameScale;
    private static final ResourceLocation flame = new ResourceLocation(DataReference.MODID, "textures/particle/torchflame.png");

    public EntityGreenFlame(World w, double x, double y, double z)
    {
        super(w, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX = this.motionX * 0.009999999776482582D;
        this.motionY = this.motionY * 0.009999999776482582D;
        this.motionZ = this.motionZ * 0.009999999776482582D;
        this.flameScale = this.particleScale;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
        this.noClip = true;
    }

    public int getBrightnessForRender(float p_70070_1_)
    {
        float f1 = ((float)this.particleAge + p_70070_1_) / (float)this.particleMaxAge;
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        int i = super.getBrightnessForRender(p_70070_1_);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f1 * 15.0F * 16.0F);

        if (j > 240)
        {
            j = 240;
        }

        return j | k << 16;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float p_70013_1_)
    {
        float f1 = ((float)this.particleAge + p_70013_1_) / (float)this.particleMaxAge;
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        float f2 = super.getBrightness(p_70013_1_);
        return f2 * f1 + (1.0F - f1);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    @Override
    public void func_180434_a(WorldRenderer wr, Entity e, float p3, float p4, float p5, float p6, float p7, float p8)
    {
        //HOW DO I SET THE PARTICLE TEXTURE??!!?!?!?!?!?!?!?!?!?!?
        Minecraft.getMinecraft().getTextureManager().bindTexture(flame);
        float f6 = ((float)this.particleAge + p3) / (float)this.particleMaxAge;
        this.particleScale = this.flameScale * (1.0F - f6 * f6 * 0.5F);
        super.func_180434_a(wr, e, p3, p4, p5, p6, p7, p8);
    }

    public int getFXLayer()
    {
        return 3;
    }
}