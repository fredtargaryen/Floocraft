package com.fredtargaryen.floocraft.client.renderer;

import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import cpw.mods.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class ViooRenderer
{
    private Minecraft mc;
    public EntityLivingBase cameraEntity;
    //public Framebuffer framebuffer;
    private float initX;
    private float initY;
    private float initZ;
    private float destX;
    private float destY;
    private float destZ;

    public ViooRenderer(Minecraft minecraft, int ix, int iy, int iz, int dx, int dy, int dz)
    {
        this.mc = minecraft;
        this.initX = (float)ix + 0.5F;
        this.initY = (float)iy + 0.5F;
        this.initZ = (float)iz + 0.5F;
        this.destX = (float)dx + 0.5F;
        this.destY = (float)dy + 0.5F;
        this.destZ = (float)dz + 0.5F;
        //this.framebuffer = RenderUtil.createFramebuffer(this.getCameraResolution(), this.getCameraResolution(), true);
    }

    //public int getCameraResolution()
    //{
        //return 256;
    //}

    private void orientCamera(float renderPartialTicks)
    {
        GL11.glTranslatef(-initX + destX, -initY + destY, -initZ + destZ);
    }

    private void setupCameraTransform(float renderPartialTicks, int p_78479_2_)
    {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        Project.gluPerspective(70F, 1F, 0.05F, 64);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        this.orientCamera(renderPartialTicks);
    }

    public void updateCameraAndRender(float renderPartialTicks)
    {
        if (this.mc.theWorld != null)
        {
            if (this.cameraEntity == null)
            {
                this.cameraEntity = this.mc.renderViewEntity;
            }

            GL11.glPushMatrix();
            {
               // this.framebuffer.bindFramebuffer(true);

                //GL11.glClear(17664);

                this.renderWorld(renderPartialTicks, System.currentTimeMillis());

                //this.framebuffer.unbindFramebuffer();
            }
            GL11.glPopMatrix();

            mc.getFramebuffer().bindFramebuffer(true);
        }
    }

    //public Framebuffer getFramebuffer()
    //{
      //  return framebuffer;
    //}

    public void setCameraEntity(EntityLivingBase cameraEntity)
    {
        this.cameraEntity = cameraEntity;
    }

    public EntityLivingBase getCameraEntity()
    {
        return this.cameraEntity;
    }

    public void renderWorld(float renderPartialTicks, long currentTimeMilliseconds)
    {
        RenderGlobal renderglobal = this.mc.renderGlobal;

        Frustrum frustrum = new Frustrum();
        frustrum.setPosition(this.destX, this.destY, this.destZ);

        this.setupCameraTransform(renderPartialTicks, 0);

        if (this.mc.gameSettings.ambientOcclusion != 0)
        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }

        renderglobal.clipRenderersByFrustum(frustrum, renderPartialTicks);

/** Terrain **/
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        renderglobal.sortAndRender(mc.renderViewEntity, 0, renderPartialTicks);

/** Entities **/
        GL11.glPushMatrix();
        {
            RenderHelper.enableStandardItemLighting();
            net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
            renderglobal.renderEntities(mc.renderViewEntity, frustrum, renderPartialTicks);
            RenderHelper.disableStandardItemLighting();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }
        GL11.glPopMatrix();

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_FOG);
    }
}