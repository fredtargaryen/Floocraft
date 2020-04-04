package com.fredtargaryen.floocraft.tileentity.renderer;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.tileentity.FireplaceTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallSignBlock;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityFlooSignRenderer extends TileEntityRenderer<FireplaceTileEntity> {
    private final FlooSignModel model = new FlooSignModel();

    public TileEntityFlooSignRenderer(TileEntityRendererDispatcher terd)
    {
        super(terd);
    }

    public void render(FireplaceTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockstate = tileEntityIn.getBlockState();
        //The position of matrixStackIn is currently the BlockPos.
        matrixStackIn.push();
        // Move to the middle of the BlockPos, to rotate around the middle according to the direction.
        matrixStackIn.translate(0.5D, 0.5D, 0.5D);
        float yAngle = -blockstate.get(WallSignBlock.FACING).getHorizontalAngle();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(yAngle));
        matrixStackIn.translate(0.0D, -0.3125D, -0.4375D);

        matrixStackIn.push();
        // Final transform to render the board correctly
        matrixStackIn.scale(0.6666667F, -0.6666667F, -0.6666667F);
        IVertexBuilder ivertexbuilder = FlooSignModel.MATERIAL.getBuffer(bufferIn, this.model::getRenderType);
        this.model.board.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        matrixStackIn.pop();

        // Render the text
        FontRenderer fontrenderer = this.renderDispatcher.getFontRenderer();
        matrixStackIn.translate(0.0D, (double)0.33333334F, (double)0.046666667F);
        matrixStackIn.scale(0.010416667F, -0.010416667F, 0.010416667F);
        // Makes black text
        int colour = 0;
        for(int i = 0; i < 4; ++i) {
            String s = tileEntityIn.getString(i);
            if (s != null) {
                // Draw the string with centred alignment
                float left = (float)(-fontrenderer.getStringWidth(s) / 2);
                fontrenderer.renderString(s, left, (float)(i * 10 - tileEntityIn.signText.length * 5), colour, false, matrixStackIn.getLast().getMatrix(), bufferIn, false, 0, combinedLightIn);
            }
        }

        matrixStackIn.pop();
    }

    @OnlyIn(Dist.CLIENT)
    public static final class FlooSignModel extends Model {
        public final ModelRenderer board = new ModelRenderer(64, 32, 0, 0);
        public static final Material MATERIAL = new Material(Atlases.SIGN_ATLAS, DataReference.SIGN_TEX_LOC);

        public FlooSignModel() {
            super(RenderType::getEntityCutoutNoCull);
            this.board.addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F, 0.0F);
        }

        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            this.board.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }
}
