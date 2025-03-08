package com.fredtargaryen.floocraft.client.renderer.blockentity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.blockentity.FloowerPotBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3f;

@OnlyIn(Dist.CLIENT)
public class FloowerPotRenderer implements BlockEntityRenderer<FloowerPotBlockEntity> {
    private static final ResourceLocation POT_POWDER = DataReference.getResourceLocation("textures/block/pot_powder.png");

    public FloowerPotRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FloowerPotBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        int noOfPowders = blockEntity.getPowderLevel();
        if (noOfPowders > 0) {
            poseStack.pushPose(); // Pushes the current transform and normal matrices. Origin is the (0, 0, 0) corner of the block to be rendered
            // set the key rendering flags appropriately...
            //RenderSystem.disableLighting(); // turn off "item" lighting (face brightness depends on which direction it is facing)
            //RenderSystem.disableBlend(); // turn off "alpha" transparency blending
            RenderSystem.depthMask(true); // quad is hidden behind other objects
            PoseStack.Pose pose = poseStack.last();
            Matrix3f normal = pose.normal();
            VertexConsumer builder = multiBufferSource.getBuffer(RenderType.entitySolid(POT_POWDER));
            float level = noOfPowders / 64f * 0.3125f + 0.0625f;
            this.doAVertex(builder, pose, 0.625f, level, 0.625f, 1f, 1f, combinedLight, combinedOverlay);
            this.doAVertex(builder, pose, 0.625f, level, 0.375f, 1f, 0f, combinedLight, combinedOverlay);
            this.doAVertex(builder, pose, 0.375f, level, 0.375f, 0f, 0f, combinedLight, combinedOverlay);
            this.doAVertex(builder, pose, 0.375f, level, 0.625f, 0f, 1f, combinedLight, combinedOverlay);
            poseStack.popPose();
        }
    }

    private void doAVertex(VertexConsumer builder, PoseStack.Pose pose, float x, float y, float z, float u, float v, int combinedLightLevel, int combinedOverlayLevel) {
        builder.vertex(pose, x, y, z)
                .color(1f, 1f, 1f, 1f)
                .uv(u, v)
                .overlayCoords(combinedOverlayLevel)
                .uv2(combinedLightLevel)
                .normal(pose, 0f, 1f, 0f)
                .endVertex();
    }
}
