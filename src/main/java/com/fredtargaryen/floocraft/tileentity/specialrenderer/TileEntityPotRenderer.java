package com.fredtargaryen.floocraft.tileentity.specialrenderer;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class TileEntityPotRenderer extends TileEntitySpecialRenderer
{
    @Override
    //"Relative" means distance on this axis from block to player's eye
    public void renderTileEntityAt(TileEntity te, double relativeX, double relativeY, double relativeZ, float partialTicks, int blockDamage)
    {
        ItemStack stack = ((TileEntityFloowerPot)te).getStackInSlot(0);
        if(stack != null && stack.getCount() > 0)
        {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GlStateManager.translate(relativeX, relativeY, relativeZ);
            Tessellator t = Tessellator.getInstance();
            VertexBuffer r = t.getBuffer();
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
