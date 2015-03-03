package com.fredtargaryen.floocraft.client.renderer;

import com.fredtargaryen.floocraft.block.GreenFlamesBusyLower;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class FireRenderer implements ISimpleBlockRenderingHandler
{
    private int renderID;

    public FireRenderer(int id)
    {
        this.renderID = id;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        Tessellator t = Tessellator.instance;
        t.addTranslation(x, y, z);
        IIcon iicon = ((GreenFlamesBusyLower)block).getFireIcon(0);
        IIcon iicon1 = ((GreenFlamesBusyLower)block).getFireIcon(1);

        t.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        t.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess,x, y, z));

        //THIS IS WHERE THE BOTTOM BLOCK GETS RENDERED
        double b = iicon.getMaxV();
        double top = iicon.getMinV();
        double l = iicon.getMinU();
        double r = iicon.getMaxU();

        //Outer z-negative face
        t.addVertexWithUV(0.0,    0.0,   0.0, r, b);
        t.addVertexWithUV(0.0,    1.0,   0.0, r, top);
        t.addVertexWithUV(1.0,    1.0,   0.0, l, top);
        t.addVertexWithUV(1.0,    0.0,   0.0, l, b);

        //Outer z-positive face
        t.addVertexWithUV(1.0,   0.0,   1.0, r, b);
        t.addVertexWithUV(1.0,   1.0,   1.0, r, top);
        t.addVertexWithUV(0.0,   1.0,   1.0, l, top);
        t.addVertexWithUV(0.0,   0.0,   1.0, l, b);

        //Outer x-negative face
        t.addVertexWithUV(0.0,   0.0,   1.0, r, b);
        t.addVertexWithUV(0.0,   1.0,   1.0, r, top);
        t.addVertexWithUV(0.0,   1.0,   0.0, l, top);
        t.addVertexWithUV(0.0,   0.0,   0.0, l, b);

        //Outer x-positive face
        t.addVertexWithUV(1.0,   0.0,   0.0, r, b);
        t.addVertexWithUV(1.0,   1.0,   0.0, r, top);
        t.addVertexWithUV(1.0,   1.0,   1.0, l, top);
        t.addVertexWithUV(1.0, 0.0, 1.0, l, b);

        //Crossed face
        t.addVertexWithUV(1.0, 0.0, 0.0, r, b);
        t.addVertexWithUV(1.0, 1.0, 0.0, r, top);
        t.addVertexWithUV(0.0, 1.0, 1.0, l, top);
        t.addVertexWithUV(0.0, 0.0, 1.0, l, b);
        //Crossed face
        t.addVertexWithUV(0.0, 0.0, 1.0, r, b);
        t.addVertexWithUV(0.0, 1.0, 1.0, r, top);
        t.addVertexWithUV(1.0, 1.0, 0.0, l, top);
        t.addVertexWithUV(1.0, 0.0, 0.0, l, b);
        //Crossed face
        t.addVertexWithUV(0.0, 0.0, 0.0, r, b);
        t.addVertexWithUV(0.0, 1.0, 0.0, r, top);
        t.addVertexWithUV(1.0, 1.0, 1.0, l, top);
        t.addVertexWithUV(1.0, 0.0, 1.0, l, b);
        //Crossed face
        t.addVertexWithUV(1.0, 0.0, 1.0, r, b);
        t.addVertexWithUV(1.0, 1.0, 1.0, r, top);
        t.addVertexWithUV(0.0, 1.0, 0.0, l, top);
        t.addVertexWithUV(0.0, 0.0, 0.0, l, b);


        //THIS IS WHERE THE TOP BLOCK IS RENDERED
        b = iicon1.getMaxV();
        top = iicon1.getMinV();
        l = iicon1.getMinU();
        r = iicon1.getMaxU();

        //Outer z-negative face
        t.addVertexWithUV(0.0,    1.0,   0.0, r, b);
        t.addVertexWithUV(0.0,    2.0,   0.0, r, top);
        t.addVertexWithUV(1.0,    2.0,   0.0, l, top);
        t.addVertexWithUV(1.0,    1.0,   0.0, l, b);

        //Outer z-positive face
        t.addVertexWithUV(1.0,   1.0,   1.0, r, b);
        t.addVertexWithUV(1.0,   2.0,   1.0, r, top);
        t.addVertexWithUV(0.0,   2.0,   1.0, l, top);
        t.addVertexWithUV(0.0,   1.0,   1.0, l, b);

        //Outer x-negative face
        t.addVertexWithUV(0.0,   1.0,   1.0, r, b);
        t.addVertexWithUV(0.0,   2.0,   1.0, r, top);
        t.addVertexWithUV(0.0,   2.0,   0.0, l, top);
        t.addVertexWithUV(0.0,   1.0,   0.0, l, b);

        //Outer x-positive face
        t.addVertexWithUV(1.0,   1.0,   0.0, r, b);
        t.addVertexWithUV(1.0,   2.0,   0.0, r, top);
        t.addVertexWithUV(1.0,   2.0,   1.0, l, top);
        t.addVertexWithUV(1.0,   1.0, 1.0, l, b);

        //Crossed face
        t.addVertexWithUV(1.0, 1.0, 0.0, r, b);
        t.addVertexWithUV(1.0, 2.0, 0.0, r, top);
        t.addVertexWithUV(0.0, 2.0, 1.0, l, top);
        t.addVertexWithUV(0.0, 1.0, 1.0, l, b);
        //Crossed face
        t.addVertexWithUV(0.0, 1.0, 1.0, r, b);
        t.addVertexWithUV(0.0, 2.0, 1.0, r, top);
        t.addVertexWithUV(1.0, 2.0, 0.0, l, top);
        t.addVertexWithUV(1.0, 1.0, 0.0, l, b);
        //Crossed face
        t.addVertexWithUV(0.0, 1.0, 0.0, r, b);
        t.addVertexWithUV(0.0, 2.0, 0.0, r, top);
        t.addVertexWithUV(1.0, 2.0, 1.0, l, top);
        t.addVertexWithUV(1.0, 1.0, 1.0, l, b);
        //Crossed face
        t.addVertexWithUV(1.0, 1.0, 1.0, r, b);
        t.addVertexWithUV(1.0, 2.0, 1.0, r, top);
        t.addVertexWithUV(0.0, 2.0, 0.0, l, top);
        t.addVertexWithUV(0.0, 1.0, 0.0, l, b);

        t.addTranslation(-x, -y, -z);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId()
    {
        return this.renderID;
    }
}