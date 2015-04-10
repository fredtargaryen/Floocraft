package com.fredtargaryen.floocraft.tileentity.specialrenderer;

import com.fredtargaryen.floocraft.model.ModelFlooSign;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityFlooSignRenderer extends TileEntitySignRenderer
{
	private static final ResourceLocation floosigntexloc = new ResourceLocation("ftfloocraft","textures/entity/blockfloosign.png");
	
	/** The ModelFlooSign instance used by the TileEntityFlooSignRenderer */
    private final ModelFlooSign modelFlooSign = new ModelFlooSign();
    
    public void renderTileEntitySignAt(TileEntityFireplace par1TileEntityFireplace, double par2, double par4, double par6, float par8)
    {
        GL11.glPushMatrix();
        float f1 = 0.6666667F;
        float f2;

            int i = par1TileEntityFireplace.getBlockMetadata();
            f2 = 0.0F;

            if (i == 2)
            {
                f2 = 180.0F;
            }

            if (i == 4)
            {
                f2 = 90.0F;
            }

            if (i == 5)
            {
                f2 = -90.0F;
            }

            GL11.glTranslatef((float)par2 + 0.5F, (float)par4 + 0.75F * f1, (float)par6 + 0.5F);
            GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);

        bindTexture(floosigntexloc);
        GL11.glPushMatrix();
        GL11.glScalef(f1, -f1, -f1);
        modelFlooSign.renderSign();
        GL11.glPopMatrix();
        FontRenderer fontrenderer = this.getFontRenderer();
        f2 = 0.016666668F * f1;
        GL11.glTranslatef(0.0F, 0.5F * f1, 0.07F * f1);
        GL11.glScalef(f2, -f2, f2);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * f2);
        GL11.glDepthMask(false);
        byte b0 = 0;

        for (int j = 0; j < par1TileEntityFireplace.signText.length; ++j)
        {
            String s = par1TileEntityFireplace.signText[j].getUnformattedText();

            if (j == par1TileEntityFireplace.lineBeingEdited)
            {
                s = "> " + s + " <";
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - par1TileEntityFireplace.signText.length * 5, b0);
            }
            else
            {
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - par1TileEntityFireplace.signText.length * 5, b0);
            }
        }

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
    
    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.renderTileEntitySignAt((TileEntityFireplace)par1TileEntity, par2, par4, par6, par8);
    }
}
