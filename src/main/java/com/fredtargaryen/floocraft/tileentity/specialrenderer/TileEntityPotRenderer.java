package com.fredtargaryen.floocraft.tileentity.specialrenderer;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityPotRenderer extends TileEntitySpecialRenderer
{
    //The MC flower pot is rendered in client/renderer/RenderBlocks
    public static final ResourceLocation powder = new ResourceLocation(DataReference.MODID+":textures/blocks/powderinpot.png");

    public TileEntityPotRenderer(){}

    @Override
    public void renderTileEntityAt(TileEntity t, double x, double y, double z, float var8)
    {
        float f = 0.25F;
        float f1 = f/2;
        double level;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        Tessellator tessellator = Tessellator.instance;
        ItemStack i = ((TileEntityFloowerPot)t).getStackInSlot(0);
        if(i != null && i.stackSize > 0)
        {
            this.bindTexture(powder);
            level = 0.125 + (f * (i.stackSize / 64));
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(0.5F - f1, level, 0.5F - f1, 0, 0);
            tessellator.addVertexWithUV(0.5F - f1, level, 0.5F + f1, 0, 1);
            tessellator.addVertexWithUV(0.5F + f1, level, 0.5F - f1, 1, 0);
            tessellator.addVertexWithUV(0.5F + f1, level, 0.5F + f1, 1, 1);
            tessellator.draw();
        }
        GL11.glPopMatrix();
    }
}
