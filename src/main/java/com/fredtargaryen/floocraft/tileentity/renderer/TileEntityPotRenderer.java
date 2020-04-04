package com.fredtargaryen.floocraft.tileentity.renderer;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.tileentity.FloowerPotTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class TileEntityPotRenderer extends TileEntityRenderer<FloowerPotTileEntity>
{
    public TileEntityPotRenderer(TileEntityRendererDispatcher terd)
    {
        super(terd);
    }

    /**
     *
     * @param te
     * @param partialTicks
     * @param matrixStackIn the current view transformations
     * @param bufferIn A map from RenderTypes to buffers
     * @param combinedLightIn The "block light" and "sky light" packed into the space of an int
     * @param combinedOverlayIn Points to an overlay texture that modifies the bound texture
     */
    @Override
    public void render(FloowerPotTileEntity te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack stack = te.getStackInSlot(0);
        if(stack != null && stack.getCount() > 0) {
            matrixStackIn.push(); // Pushes the current transform and normal matrices. Origin is the (0, 0, 0) corner of the block to be rendered
            IVertexBuilder ivb = bufferIn.getBuffer(RenderType.getEntitySolid(DataReference.TP_BACKGROUND));
            // set the key rendering flags appropriately...
            RenderSystem.disableLighting(); // turn off "item" lighting (face brightness depends on which direction it is facing)
            RenderSystem.disableBlend(); // turn off "alpha" transparency blending
            RenderSystem.depthMask(true); // quad is hidden behind other objects
            Matrix4f pos = matrixStackIn.getLast().getMatrix();
            Matrix3f norm = matrixStackIn.getLast().getNormal();
            float level = (((float)stack.getCount() / 64f) * 0.3125f) + 0.0625f;
            this.doAVertex(ivb, pos, norm, 0.625f, level, 0.625f, 1f, 1f, combinedLightIn);
            this.doAVertex(ivb, pos, norm, 0.625f, level, 0.375f, 1f, 0f, combinedLightIn);
            this.doAVertex(ivb, pos, norm, 0.375f, level, 0.375f, 0f, 0f, combinedLightIn);
            this.doAVertex(ivb, pos, norm, 0.375f, level, 0.625f, 0f, 1f, combinedLightIn);
            matrixStackIn.pop();
        }
    }

    private void doAVertex(IVertexBuilder ivb, Matrix4f pos, Matrix3f norm, float x, float y, float z, float u, float v, int lightLevel) {
        ivb.pos(pos, x, y, z)
                .color(1f, 1f, 1f, 1f)
                .tex(u, v)
                .overlay(OverlayTexture.NO_OVERLAY)
                .lightmap(lightLevel)
                .normal(norm, 0f, 1f, 0f)
                .endVertex();
    }
}
