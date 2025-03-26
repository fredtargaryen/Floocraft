package com.fredtargaryen.floocraft.client.renderer.entity;

import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;

public class PeekerRenderer extends EntityRenderer<PeekerEntity> {
    private static final ResourceLocation PLACEHOLDER = ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");

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

    public PeekerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(PeekerEntity peeker, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(peeker, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - peeker.getYRot()));
        PoseStack.Pose normalPose = poseStack.last();
        Matrix4f pos = normalPose.pose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(peeker), true));
        this.doAVertex(consumer, pos, minx, miny, minz, maxu, maxv, OverlayTexture.NO_OVERLAY, packedLight, normalPose);
        this.doAVertex(consumer, pos, minx, maxy, maxz, maxu, minv, OverlayTexture.NO_OVERLAY, packedLight, normalPose);
        this.doAVertex(consumer, pos, maxx, maxy, maxz, minu, minv, OverlayTexture.NO_OVERLAY, packedLight, normalPose);
        this.doAVertex(consumer, pos, maxx, miny, minz, minu, maxv, OverlayTexture.NO_OVERLAY, packedLight, normalPose);
        poseStack.popPose();
    }

    private void doAVertex(VertexConsumer consumer, Matrix4f pos, float x, float y, float z, float u, float v, int overlay, int light, PoseStack.Pose normalPose) {
        consumer.addVertex(pos, x, y, z)
                .setColor(1f, 1f, 1f, 0.375f)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(normalPose, 0f, 1f, 0f);
    }

    @Override
    @Nonnull
    public ResourceLocation getTextureLocation(PeekerEntity peeker) {
        return peeker.getTexture().orElse(PLACEHOLDER);
    }
}