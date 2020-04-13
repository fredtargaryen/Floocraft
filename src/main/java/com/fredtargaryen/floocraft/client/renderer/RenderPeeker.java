package com.fredtargaryen.floocraft.client.renderer;

import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RenderPeeker extends EntityRenderer<PeekerEntity> {
    private static ResourceLocation NULL = new ResourceLocation("textures/entity/steve.png");

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
        matrixStackIn.push();
        matrixStackIn.rotate(new Quaternion(new Vector3f(0f, 1f, 0f), 180f - par1PeekerEntity.rotationYaw, true));
        //matrixStackIn.rotate(new Quaternion(new Vector3f(1f, 0f, 0f), 15f, true));
        Matrix4f pos = matrixStackIn.getLast().getMatrix();
        Matrix3f norm = matrixStackIn.getLast().getNormal();
        IVertexBuilder ivb = bufferIn.getBuffer(RenderType.getEntityAlpha(this.getEntityTexture(par1PeekerEntity), 0.6f));
        ivb.pos(pos, minx, miny, minz)
                .color(1f, 1f, 1f, 1f)
                .tex(maxu, maxv)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(packedLightIn)
                .normal(norm, 0f, 1f, 0f)
                .endVertex();
        ivb.pos(pos, minx, maxy, maxz)
                .color(1f, 1f, 1f, 1f)
                .tex(maxu, minv)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(packedLightIn)
                .normal(norm, 0f, 1f, 0f)
                .endVertex();
        ivb.pos(pos, maxx, maxy, maxz)
                .color(1f, 1f, 1f, 1f)
                .tex(minu, minv)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(packedLightIn)
                .normal(norm, 0f, 1f, 0f)
                .endVertex();
        ivb.pos(pos, maxx, miny, minz)
                .color(1f, 1f, 1f, 1f)
                .tex(minu, maxv)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(packedLightIn)
                .normal(norm, 0f, 1f, 0f)
                .endVertex();
        matrixStackIn.pop();
        super.render(par1PeekerEntity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    @Nonnull
    public ResourceLocation getEntityTexture(PeekerEntity entity) {
        ResourceLocation rl = entity.getTexture();
        return rl == null ? NULL : rl;
    }
}