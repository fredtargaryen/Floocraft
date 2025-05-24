package com.fredtargaryen.floocraft.client.renderer.blockentity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.blockentity.FloowerPotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;

import static com.fredtargaryen.floocraft.HelperFunctions.getElapsedPartialTicks;

@OnlyIn(Dist.CLIENT)
public class FloowerPotRenderer implements BlockEntityRenderer<FloowerPotBlockEntity> {
    private static final ResourceLocation POT_POWDER = DataReference.getResourceLocation("textures/block/pot_powder.png");

    /**
     * How many cycles of the animation texture have been completed
     */
    private double time;

    /**
     * For calculating time
     */
    private float previousPartialTicks;

    /**
     * Controls how long the animation takes to do one loop of the 32 textures.
     * By default textures are animated at 1 frame per client tick
     */
    private static final double intervalLengthSeconds = 1.6; // 32 client ticks

    /**
     * The top V coordinate to sample the pot powder texture with on this frame
     */
    private float minV;

    public FloowerPotRenderer(BlockEntityRendererProvider.Context context) {
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public void render(FloowerPotBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        int noOfPowders = blockEntity.getPowderLevel();
        if (noOfPowders > 0) {
            poseStack.pushPose(); // Pushes the current transform and normal matrices. Origin is the (0, 0, 0) corner of the block to be rendered
            PoseStack.Pose pose = poseStack.last();
            VertexConsumer builder = multiBufferSource.getBuffer(RenderType.entitySolid(POT_POWDER));
            float level = noOfPowders / 64f * 0.3125f + 0.0625f;
            float maxV = this.minV + 0.03125f;
            this.doAVertex(builder, pose, 0.625f, level, 0.625f, 1f, maxV, packedLight, packedOverlay);
            this.doAVertex(builder, pose, 0.625f, level, 0.375f, 1f, this.minV, packedLight, packedOverlay);
            this.doAVertex(builder, pose, 0.375f, level, 0.375f, 0f, this.minV, packedLight, packedOverlay);
            this.doAVertex(builder, pose, 0.375f, level, 0.625f, 0f, maxV, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    /**
     * Calculate the section of pot powder texture to display this frame.
     * This is a good time to remind myself that each BlockEntityRenderer is a singleton per client
     */
    @SubscribeEvent
    public void onClientTick(RenderFrameEvent.Pre event) {
        float partialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        this.time += getElapsedPartialTicks(this.previousPartialTicks, partialTicks) / 20.0 / intervalLengthSeconds;
        this.previousPartialTicks = partialTicks;
        this.minV = ((int) ((this.time - (int) this.time) * 32)) // Get the frame number
                / 32f; // Convert into 0-1 v coord
    }

    private void doAVertex(VertexConsumer builder, PoseStack.Pose pose, float x, float y, float z, float u, float v, int combinedLightLevel, int combinedOverlayLevel) {
        builder.addVertex(pose, x, y, z)
                .setColor(1f, 1f, 1f, 1f)
                .setUv(u, v)
                .setOverlay(combinedOverlayLevel)
                .setLight(combinedLightLevel)
                .setNormal(pose, 0f, 1f, 0f);
    }
}
