package com.fredtargaryen.floocraft.client.renderer.blockentity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.block.FlooSignBlock;
import com.fredtargaryen.floocraft.block.entity.FlooSignText;
import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class FlooSignRenderer implements BlockEntityRenderer<FlooSignBlockEntity> {
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private static final float RENDER_SCALE = 0.6666667F;
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, 0.3333333432674408, 0.046666666865348816);
    private final Font font;
    public static final ModelLayerLocation FLOO_SIGN_MODEL_LOCATION = new ModelLayerLocation(
            DataReference.getResourceLocation("sign/floo_sign"),
            "main");
    private SignModel signModel;

    public FlooSignRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
        this.signModel = new SignModel(context.bakeLayer(FLOO_SIGN_MODEL_LOCATION));
    }

    @Override
    public void render(FlooSignBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {
        BlockState state = blockEntity.getBlockState();
        FlooSignBlock signBlock = (FlooSignBlock) state.getBlock();
        WoodType woodType = SignBlock.getWoodType(signBlock);
        SignModel model = this.signModel;
        this.renderSignWithText(blockEntity, poseStack, multiBufferSource, combinedLight, combinedOverlay, state, signBlock, woodType, model);
    }

    public float getSignModelRenderScale() {
        return 0.6666667F;
    }

    public float getSignTextRenderScale() {
        return 0.6666667F;
    }

    void renderSignWithText(FlooSignBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource p_279303_, int p_279396_, int p_279203_, BlockState p_279391_, SignBlock p_279224_, WoodType woodType, Model p_279444_) {
        poseStack.pushPose();
        this.translateSign(poseStack, -p_279224_.getYRotationDegrees(p_279391_), p_279391_);
        this.renderSign(poseStack, p_279303_, p_279396_, p_279203_, p_279444_);
        this.renderSignText(blockEntity.getBlockPos(), blockEntity.getText(), poseStack, p_279303_, p_279396_, FlooSignBlockEntity.TEXT_LINE_HEIGHT, FlooSignBlockEntity.MAX_TEXT_LINE_WIDTH, true);
        poseStack.popPose();
    }

    void translateSign(PoseStack p_278074_, float p_277875_, BlockState p_277559_) {
        p_278074_.translate(0.5F, 0.75F * this.getSignModelRenderScale(), 0.5F);
        p_278074_.mulPose(Axis.YP.rotationDegrees(p_277875_));
        if (!(p_277559_.getBlock() instanceof StandingSignBlock)) {
            p_278074_.translate(0.0F, -0.3125F, -0.4375F);
        }
    }

    void renderSign(PoseStack poseStack, MultiBufferSource multiBufferSource, int p_279494_, int p_279344_, Model model) {
        poseStack.pushPose();
        float renderScale = this.getSignModelRenderScale();
        poseStack.scale(renderScale, -renderScale, -renderScale);
        Objects.requireNonNull(model);
        VertexConsumer consumer = DataReference.SIGN_MATERIAL.buffer(multiBufferSource, model::renderType);
        this.renderSignModel(poseStack, p_279494_, p_279344_, model, consumer);
        poseStack.popPose();
    }

    void renderSignModel(PoseStack poseStack, int p_249399_, int p_249042_, Model model, VertexConsumer consumer) {
        SignModel signModel = (SignModel) model;
        signModel.root.render(poseStack, consumer, p_249399_, p_249042_);
    }

    void renderSignText(BlockPos blockPos, FlooSignText signText, PoseStack poseStack, MultiBufferSource multiBufferSource, int p_279300_, int unknown2, int p_279357_, boolean frontOrBack) {
        poseStack.pushPose();
        this.translateSignText(poseStack, frontOrBack, this.getTextOffset());
        int blackColour = DataReference.BLACK_COLOUR;
        int $$9 = 4 * unknown2 / 2;
        FormattedCharSequence[] $$10 = signText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (p_277227_) -> {
            List<FormattedCharSequence> $$2 = this.font.split(p_277227_, p_279357_);
            return $$2.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence) $$2.get(0);
        });
        int $$14;
        boolean $$15;
        int $$16;
        $$14 = blackColour;
        $$15 = false;
        $$16 = p_279300_;

        for (int $$17 = 0; $$17 < 4; ++$$17) {
            FormattedCharSequence $$18 = $$10[$$17];
            float $$19 = (float) (-this.font.width($$18) / 2);
            if ($$15) {
                this.font.drawInBatch8xOutline($$18, $$19, (float) ($$17 * unknown2 - $$9), $$14, blackColour, poseStack.last().pose(), multiBufferSource, $$16);
            } else {
                this.font.drawInBatch($$18, $$19, (float) ($$17 * unknown2 - $$9), $$14, false, poseStack.last().pose(), multiBufferSource, DisplayMode.POLYGON_OFFSET, 0, $$16);
            }
        }

        poseStack.popPose();
    }

    private void translateSignText(PoseStack poseStack, boolean frontOrBack, Vec3 unknown) {
        if (!frontOrBack) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }

        float $$3 = 0.015625F * this.getSignTextRenderScale();
        poseStack.translate(unknown.x, unknown.y, unknown.z);
        poseStack.scale($$3, -$$3, $$3);
    }

    Vec3 getTextOffset() {
        return TEXT_OFFSET;
    }

    public static LayerDefinition createSignLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        $$1.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), PartPose.ZERO);
        return LayerDefinition.create($$0, 64, 32);
    }

    @OnlyIn(Dist.CLIENT)
    public static final class SignModel extends Model {
        public final ModelPart root;

        public SignModel(ModelPart modelPart) {
            super(RenderType::entityCutoutNoCull);
            this.root = modelPart;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int colour) {
            this.root.render(poseStack, consumer, light, overlay, colour);
        }
    }
}
