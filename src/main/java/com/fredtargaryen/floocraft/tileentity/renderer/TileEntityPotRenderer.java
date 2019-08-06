package com.fredtargaryen.floocraft.tileentity.renderer;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.tileentity.FloowerPotTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class TileEntityPotRenderer extends TileEntityRenderer<FloowerPotTileEntity>
{
    @Override
    //"Relative" means distance on this axis from block to player's eye
    public void render(FloowerPotTileEntity te, double relativeX, double relativeY, double relativeZ, float partialTicks, int destroyStage) {
        ItemStack stack = te.getStackInSlot(0);
        if(stack != null && stack.getCount() > 0) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GlStateManager.translated(relativeX, relativeY, relativeZ);
            Tessellator t = Tessellator.getInstance();
            BufferBuilder r = t.getBuffer();
            // set the key rendering flags appropriately...
            GL11.glDisable(GL11.GL_LIGHTING);     // turn off "item" lighting (face brightness depends on which direction it is facing)
            GL11.glDisable(GL11.GL_BLEND);        // turn off "alpha" transparency blending
            GL11.glDepthMask(true);               // quad is hidden behind other objects
            this.bindTexture(DataReference.TP_BACKGROUND);
            r.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            double level = (((float)stack.getCount() / 64) * 0.3125) + 0.0625;
            r.pos(0.625, level, 0.625).tex(1.0, 1.0).endVertex();
            r.pos(0.625, level, 0.375).tex(1.0, 0.0).endVertex();
            r.pos(0.375, level, 0.375).tex(0.0, 0.0).endVertex();
            r.pos(0.375, level, 0.625).tex(0.0, 1.0).endVertex();
            t.draw();
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
