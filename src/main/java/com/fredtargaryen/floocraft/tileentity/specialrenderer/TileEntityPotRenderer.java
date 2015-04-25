package com.fredtargaryen.floocraft.tileentity.specialrenderer;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityPotRenderer extends TileEntitySpecialRenderer
{
    public TileEntityPotRenderer()
    {
        super();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double z, double something, float somethingElse, int somethingElser)
    {
        TileEntityFloowerPot tefp = (TileEntityFloowerPot) te;
        BlockPos pos = tefp.getPos();
        ItemStack stack = tefp.getStackInSlot(0);
        if(stack != null && stack.stackSize > 0)
        {
            WorldRenderer r = Tessellator.getInstance().getWorldRenderer();
            GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();
            r.setTranslation(pos.getX(), pos.getY() + (((float)stack.stackSize / 64) * 0.3125) + 0.0625, pos.getZ());
            r.startDrawingQuads();
            //Inner y-positive face (when there's powder)
            this.bindTexture(DataReference.powderRes);
            r.addVertexWithUV(0.625, 0, 0.625, 1.0, 1.0);
            r.addVertexWithUV(0.625, 0, 0.375, 1.0, 0.0);
            r.addVertexWithUV(0.375, 0, 0.375, 0.0, 0.0);
            r.addVertexWithUV(0.375, 0, 0.625, 0.0, 1.0);
            r.finishDrawing();
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        }
    }
}
