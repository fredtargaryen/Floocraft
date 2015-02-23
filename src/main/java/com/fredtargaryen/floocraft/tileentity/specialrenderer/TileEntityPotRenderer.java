package com.fredtargaryen.floocraft.tileentity.specialrenderer;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityPotRenderer extends TileEntitySpecialRenderer
{
    public TileEntityPotRenderer()
    {
        super();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        TileEntityFloowerPot tefp = (TileEntityFloowerPot)te;
        this.renderPot(tefp, tefp.getWorldObj(), tefp.xCoord, tefp.yCoord, tefp.zCoord, FloocraftBase.floowerPot);
        GL11.glPopMatrix();
    }
    public void renderPot(TileEntityFloowerPot tefp, World world, int x, int y, int z, Block block)
    {
        Tessellator t = Tessellator.instance;
        //This will make your block brightness dependent from surroundings lighting.
        float f = block.getLightValue(world, x, y, z);
        int l = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
        int l1 = l % 65536;
        int l2 = l / 65536;
        t.setColorOpaque_F(f, f, f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);
        t.startDrawingQuads();
        this.bindTexture(DataReference.potRes);

        //Outer z-negative face
        t.addVertexWithUV(0.3125,   0,      0.3125, 0.6875, 1.0);
        t.addVertexWithUV(0.3125,   0.375,  0.3125, 0.6875, 0.625);
        t.addVertexWithUV(0.6875,   0.375,  0.3125, 0.3125, 0.625);
        t.addVertexWithUV(0.6875, 0, 0.3125, 0.3125, 1.0);

        //Inner z-negative face
        t.addVertexWithUV(0.3125,   0,      0.625, 0.6875, 1.0);
        t.addVertexWithUV(0.3125,   0.375,  0.625, 0.6875, 0.625);
        t.addVertexWithUV(0.6875,   0.375,  0.625, 0.3125, 0.625);
        t.addVertexWithUV(0.6875,   0,      0.625, 0.3125, 1.0);

        //Outer z-positive face
        t.addVertexWithUV(0.6875,   0,      0.6875, 0.6875, 1.0);
        t.addVertexWithUV(0.6875,   0.375,  0.6875, 0.6875, 0.625);
        t.addVertexWithUV(0.3125,   0.375,  0.6875, 0.3125, 0.625);
        t.addVertexWithUV(0.3125,   0,      0.6875, 0.3125, 1.0);

        //Inner z-positive face
        t.addVertexWithUV(0.6875,   0,      0.375, 0.6875, 1.0);
        t.addVertexWithUV(0.6875,   0.375,  0.375, 0.6875, 0.625);
        t.addVertexWithUV(0.3125,   0.375,  0.375, 0.3125, 0.625);
        t.addVertexWithUV(0.3125,   0,      0.375, 0.3125, 1.0);

        //Outer x-negative face
        t.addVertexWithUV(0.3125,   0,      0.6875, 0.6875, 1.0);
        t.addVertexWithUV(0.3125,   0.375,  0.6875, 0.6875, 0.625);
        t.addVertexWithUV(0.3125,   0.375,  0.3125, 0.3125, 0.625);
        t.addVertexWithUV(0.3125,   0,      0.3125, 0.3125, 1.0);

        //Inner x-negative face
        t.addVertexWithUV(0.625,   0,      0.6875, 0.6875, 1.0);
        t.addVertexWithUV(0.625,   0.375,  0.6875, 0.6875, 0.625);
        t.addVertexWithUV(0.625,   0.375,  0.3125, 0.3125, 0.625);
        t.addVertexWithUV(0.625,   0,      0.3125, 0.3125, 1.0);

        //Outer x-positive face
        t.addVertexWithUV(0.6875,   0,      0.3125, 0.6875, 1.0);
        t.addVertexWithUV(0.6875,   0.375,  0.3125, 0.6875, 0.625);
        t.addVertexWithUV(0.6875,   0.375,  0.6875, 0.3125, 0.625);
        t.addVertexWithUV(0.6875,   0,      0.6875, 0.3125, 1.0);

        //Inner x-positive face
        t.addVertexWithUV(0.375,   0,      0.3125, 0.6875, 1.0);
        t.addVertexWithUV(0.375,   0.375,  0.3125, 0.6875, 0.625);
        t.addVertexWithUV(0.375,   0.375,  0.6875, 0.3125, 0.625);
        t.addVertexWithUV(0.375,   0,      0.6875, 0.3125, 1.0);

        //Outer y-positive face (the rim)
        t.addVertexWithUV(0.6875,   0.375,      0.6875, 0.6875, 0.6875);
        t.addVertexWithUV(0.6875,   0.375,      0.3125, 0.6875, 0.3125);
        t.addVertexWithUV(0.3125,   0.375,      0.3125, 0.3125, 0.3125);
        t.addVertexWithUV(0.3125,   0.375,      0.6875, 0.3125, 0.6875);

        //Inner y-positive face (when empty)
        t.addVertexWithUV(0.6875,   0.0625,      0.6875, 0.6875, 1.0);
        t.addVertexWithUV(0.6875,   0.0625,      0.3125, 0.6875, 0.625);
        t.addVertexWithUV(0.3125,   0.0625,      0.3125, 0.3125, 0.625);
        t.addVertexWithUV(0.3125,   0.0625,      0.6875, 0.3125, 1.0);

        t.draw();

        ItemStack stack = tefp.getStackInSlot(0);
        if(stack != null && stack.stackSize > 0)
        {
            double level = (((float)stack.stackSize / 64) * 0.3125) + 0.0625;
            t.startDrawingQuads();
            //Inner y-positive face (when there's powder)
            this.bindTexture(DataReference.powderRes);
            t.addVertexWithUV(0.625, level, 0.625, 1.0, 1.0);
            t.addVertexWithUV(0.625, level, 0.375, 1.0, 0.0);
            t.addVertexWithUV(0.375, level, 0.375, 0.0, 0.0);
            t.addVertexWithUV(0.375, level, 0.625, 0.0, 1.0);
            t.draw();
        }
    }
}
