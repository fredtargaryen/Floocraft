package com.fredtargaryen.floocraft.client.renderer;

import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class RenderPeeker extends EntityRenderer<PeekerEntity> {
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
    private static final float minv = 0.125F;private static final float maxv = 0.25F;

    public RenderPeeker(EntityRendererManager rm) {
        super(rm);
    }

    public void doRender(PeekerEntity par1PeekerEntity, double x, double y, double z, float par8, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)x, (float)y, (float)z);
        GlStateManager.rotatef(180.0F - par1PeekerEntity.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.bindEntityTexture(par1PeekerEntity);
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.6F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(minx, miny, minz).tex(maxu, maxv).endVertex();
        vertexbuffer.pos(minx, maxy, maxz).tex(maxu, minv).endVertex();
        vertexbuffer.pos(maxx, maxy, maxz).tex(minu, minv).endVertex();
        vertexbuffer.pos(maxx, miny, minz).tex(minu, maxv).endVertex();
        tessellator.draw();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.popMatrix();
        super.doRender(par1PeekerEntity, x, y, z, par8, partialTicks);
    }

    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(PeekerEntity entity) {
        try {
            return entity.getTexture();
        }
        catch(Exception e) {
                return NULL;
        }
    }
}