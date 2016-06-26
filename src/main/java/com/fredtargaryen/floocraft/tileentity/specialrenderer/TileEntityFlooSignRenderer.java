package com.fredtargaryen.floocraft.tileentity.specialrenderer;

import com.fredtargaryen.floocraft.model.ModelFlooSign;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityFlooSignRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation floosigntexloc = new ResourceLocation("ftfloocraft", "assets/ftfloocraft/textures/entity/blockfloosign.png");
	
	/** The ModelFlooSign instance used by the TileEntityFlooSignRenderer */
    private final ModelFlooSign modelFlooSign = new ModelFlooSign();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        TileEntityFireplace sign = (TileEntityFireplace) te; 
        GL11.glPushMatrix();
        float f1 = 0.6666667F;
        float f2;

        int i = sign.getBlockMetadata();
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

        GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F * f1, (float)z + 0.5F);
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

        for (int j = 0; j < sign.signText.length; ++j)
        {
            String s = sign.signText[j].getUnformattedText();

            if (j == sign.lineBeingEdited)
            {
                s = "> " + s + " <";
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - sign.signText.length * 5, b0);
            }
            else
            {
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - sign.signText.length * 5, b0);
            }
        }

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
