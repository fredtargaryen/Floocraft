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
        BlockPos pos = te.getPos();
        GL11.glPushMatrix();
        GL11.glTranslated(pos.getX(), pos.getY(), pos.getZ());
        this.renderPot((TileEntityFloowerPot)te, pos);
        GL11.glPopMatrix();
    }

    public void renderPot(TileEntityFloowerPot tefp, BlockPos pos)
    {
        World world = tefp.getWorld();
        //This will make your block brightness dependent from surroundings lighting.
        float f = world.getBlockState(pos).getBlock().getMixedBrightnessForBlock(world, pos);
        WorldRenderer r = Tessellator.getInstance().getWorldRenderer();
        r.setColorOpaque_F(f, f, f);

        ItemStack stack = tefp.getStackInSlot(0);
        if(stack != null && stack.stackSize > 0)
        {
            double level = (((float)stack.stackSize / 64) * 0.3125) + 0.0625;
            r.startDrawingQuads();
            //Inner y-positive face (when there's powder)
            this.bindTexture(DataReference.powderRes);
            r.addVertexWithUV(0.625, level, 0.625, 1.0, 1.0);
            r.addVertexWithUV(0.625, level, 0.375, 1.0, 0.0);
            r.addVertexWithUV(0.375, level, 0.375, 0.0, 0.0);
            r.addVertexWithUV(0.375, level, 0.625, 0.0, 1.0);
            r.finishDrawing();
        }
    }
}
