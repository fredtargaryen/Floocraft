package com.fredtargaryen.floocraft.client.renderer;

import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

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
    private static final float minv = 0.125F;
    private static final float maxv = 0.25F;

    public RenderPeeker(EntityRendererManager rm) {
        super(rm);
    }

    @Override
    public void render(PeekerEntity par1PeekerEntity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
//        GlStateManager.pushMatrix();
//        GlStateManager.translatef((float)x, (float)y, (float)z);
//        GlStateManager.rotatef(180.0F - par1PeekerEntity.rotationYaw, 0.0F, 1.0F, 0.0F);
//        this.bindEntityTexture(par1PeekerEntity);
//        GlStateManager.enableAlphaTest();
//        GlStateManager.enableBlend();
//        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.6F);
//        Tessellator tessellator = Tessellator.getInstance();
//        BufferBuilder vertexbuffer = tessellator.getBuffer();
//        vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//        vertexbuffer.pos(minx, miny, minz).tex(maxu, maxv).endVertex();
//        vertexbuffer.pos(minx, maxy, maxz).tex(maxu, minv).endVertex();
//        vertexbuffer.pos(maxx, maxy, maxz).tex(minu, minv).endVertex();
//        vertexbuffer.pos(maxx, miny, minz).tex(minu, maxv).endVertex();
//        tessellator.draw();
//        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        GlStateManager.disableBlend();
//        GlStateManager.disableAlphaTest();
//        GlStateManager.popMatrix();
        matrixStackIn.push();
        matrixStackIn.rotate(new Quaternion(new Vector3f(0f, 1f, 0f), 180f - par1PeekerEntity.rotationYaw, true));
        IVertexBuilder ivb = bufferIn.getBuffer(RenderType.getEntityAlpha(this.getEntityTexture(par1PeekerEntity), 0.6f));
        ivb.pos(minx, miny, minz).tex(maxu, maxv).endVertex();
        ivb.pos(minx, maxy, maxz).tex(maxu, minv).endVertex();
        ivb.pos(maxx, maxy, maxz).tex(minu, minv).endVertex();
        ivb.pos(maxx, miny, minz).tex(minu, maxv).endVertex();
        matrixStackIn.pop();
        super.render(par1PeekerEntity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(PeekerEntity entity) {
        try {
            return entity.getTexture();
        }
        catch(Exception e) {
                return NULL;
        }
    }
}