package com.fredtargaryen.floocraft.tileentity.specialrenderer;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class TileEntityPotRenderer extends TileEntitySpecialRenderer
{
    public TileEntityPotRenderer()
    {
        super();
    }

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
            r.startDrawingQuads();
            double level = (((float)stack.stackSize / 64) * 0.3125) + 0.0625;
            r.addVertexWithUV(0.625, level, 0.625, 1.0, 1.0);
            r.addVertexWithUV(0.625, level, 0.375, 1.0, 0.0);
            r.addVertexWithUV(0.375, level, 0.375, 0.0, 0.0);
            r.addVertexWithUV(0.375, level, 0.625, 0.0, 1.0);
            t.draw();
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
