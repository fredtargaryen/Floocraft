package com.fredtargaryen.floocraft.tileentity.specialrenderer;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
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
        if(stack != null && stack.stackSize > 0)
        {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GlStateManager.translate(relativeX, relativeY, relativeZ);
            Tessellator t = Tessellator.getInstance();
            WorldRenderer r = t.getWorldRenderer();
            // set the key rendering flags appropriately...
            GL11.glDisable(GL11.GL_LIGHTING);     // turn off "item" lighting (face brightness depends on which direction it is facing)
            GL11.glDisable(GL11.GL_BLEND);        // turn off "alpha" transparency blending
            GL11.glDepthMask(true);               // quad is hidden behind other objects
            this.bindTexture(DataReference.TP_BACKGROUND);
            r.func_181668_a(GL11.GL_QUADS, DefaultVertexFormats.field_181707_g);
            double level = (((float)stack.stackSize / 64) * 0.3125) + 0.0625;
            r.func_181662_b(0.625, level, 0.625).func_181673_a(1.0, 1.0).func_181675_d();
            r.func_181662_b(0.625, level, 0.375).func_181673_a(1.0, 0.0).func_181675_d();
            r.func_181662_b(0.375, level, 0.375).func_181673_a(0.0, 0.0).func_181675_d();
            r.func_181662_b(0.375, level, 0.625).func_181673_a(0.0, 1.0).func_181675_d();
            t.draw();
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
