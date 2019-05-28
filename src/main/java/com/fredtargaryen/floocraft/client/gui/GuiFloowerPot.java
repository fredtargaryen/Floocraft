package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.inventory.container.ContainerFloowerPot;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
class GuiFloowerPot extends GuiContainer {
    public GuiFloowerPot(ContainerFloowerPot container) {
        //the container is instantiated and passed to the superclass for handling
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2)
    {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color
        this.fontRenderer.drawString(I18n.format("tile.floowerpot.name"), 8, 6, 4210752);
        //draws "Inventory" or your regional equivalent
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        //draw your Gui here, only thing you need to change is the path
        //May need to do getTexture first if this method causes trouble
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.textureManager.bindTexture(new ResourceLocation(DataReference.MODID, "textures/gui/guifloowerpot.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
