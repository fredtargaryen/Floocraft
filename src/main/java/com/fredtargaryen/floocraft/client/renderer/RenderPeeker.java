package com.fredtargaryen.floocraft.client.renderer;

import com.fredtargaryen.floocraft.entity.EntityPeeker;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderPeeker extends Render<EntityPeeker> {
    private static ResourceLocation NULL = new ResourceLocation("minecraft:blocks/glass");

    private static final float minx = -0.25F;
    private static final float maxx = 0.25F;
    private static final float miny = 0.0F;
    private static final float maxy = 0.5F;
    private static final float minz = -0.001F;
    private static final float maxz = 0.0F;
    //UV of player head on skin texture in texels is from (8, 8) to (16, 16). Divided by 64 to normalise coords
    private static final float minu = 0.125F;
    private static final float maxu = 0.25F;
    private static final float minv = 0.125F;
    private static final float maxv = 0.25F;

    public RenderPeeker(RenderManager rm) {
        super(rm);
    }

    public void doRender(EntityPeeker par1EntityPeeker, double x, double y, double z, float par8, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(180.0F - par1EntityPeeker.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.bindEntityTexture(par1EntityPeeker);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.6F);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(minx, miny, minz).tex(maxu, maxv).endVertex();
        vertexbuffer.pos(minx, maxy, maxz).tex(maxu, minv).endVertex();
        vertexbuffer.pos(maxx, maxy, maxz).tex(minu, minv).endVertex();
        vertexbuffer.pos(maxx, miny, minz).tex(minu, maxv).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
        super.doRender(par1EntityPeeker, x, y, z, par8, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPeeker entity) {
        try {
            return entity.getTexture();
        }
        catch(Exception e) {
            return NULL;
        }
    }
}