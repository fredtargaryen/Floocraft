package com.fredtargaryen.floocraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class EntityGreenFlame extends EntityFX
{
	/** the scale of the flame FX */
    private final float flameScale;
    
	private static final ResourceLocation texture = new ResourceLocation("ftfloocraft", "textures/particle/torchflame.png");
	
	public EntityGreenFlame(World world, double x, double y, double z)
	{
		super(world, x, y, z);
		this.flameScale = this.particleScale;
		this.noClip = true;
	}
	
	public void renderParticle(Tessellator t, float partialTicks, float par3, float par4, float par5, float par6, float par7)
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		glDepthMask(false);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glAlphaFunc(GL_GREATER, 0.003921569F);
		t.startDrawingQuads();
		t.setBrightness(getBrightnessForRender(partialTicks));
		float f6 = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge;
        this.particleScale = this.flameScale * (1.0F - f6 * f6 * 0.5F);
		float scale = 0.1F * particleScale;
		float x = (float)(prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float y = (float)(prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float z = (float)(prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
		t.addVertexWithUV(x - par3 * scale - par6 * scale, y - par4 * scale, z - par5 * scale - par7 * scale, 1, 1);
		t.addVertexWithUV(x - par3 * scale + par6 * scale, y + par4 * scale, z - par5 * scale + par7 * scale, 1, 0);
		t.addVertexWithUV(x + par3 * scale + par6 * scale, y + par4 * scale, z + par5 * scale + par7 * scale, 0, 0);
		t.addVertexWithUV(x + par3 * scale - par6 * scale, y - par4 * scale, z + par5 * scale - par7 * scale, 0, 1);
		t.draw();
		glDisable(GL_BLEND);
		glDepthMask(true);
		glAlphaFunc(GL_GREATER, 0.1F);
	}
	
	public int getFXLayer()
	{
		return 3;
	}
	
	public EntityGreenFlame setMaxAge(int maxAge)
	{
		particleMaxAge = maxAge;
		return this;
	}
	
	public EntityGreenFlame setGravity(float gravity)
	{
		particleGravity = gravity;
		return this;
	}
	
	public EntityGreenFlame setScale(float scale)
	{
		particleScale = scale;
		return this;
	}
	
	/**
     * Gets how bright this entity is.
     */
    public float getBrightness(float par1)
    {
        float f1 = ((float)this.particleAge + par1) / (float)this.particleMaxAge;

        if (f1 < 0.0F)
        {
            f1 = 0.0F;
        }

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        float f2 = super.getBrightness(par1);
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
    
    public int getBrightnessForRender(float par1)
    {
        float f1 = ((float)this.particleAge + par1) / (float)this.particleMaxAge;

        if (f1 < 0.0F)
        {
            f1 = 0.0F;
        }

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        int i = super.getBrightnessForRender(par1);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f1 * 15.0F * 16.0F);

        if (j > 240)
        {
            j = 240;
        }

        return j | k << 16;
    }
}
