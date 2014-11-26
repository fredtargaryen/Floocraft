package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.inventory.container.ContainerFloowerPot;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiFloowerPot extends GuiContainer {

    public GuiFloowerPot (InventoryPlayer inventoryPlayer, TileEntityFloowerPot tileEntity)
    {
        //the container is instanciated and passed to the superclass for handling
        super(new ContainerFloowerPot(inventoryPlayer, tileEntity));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2)
    {
        //draw text and stuff here
        //the parameters for drawString are: string, x, y, color
        this.fontRendererObj.drawString(StatCollector.translateToLocal("tile.floowerpot.name"), 8, 6, 4210752);
        //draws "Inventory" or your regional equivalent
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        //draw your Gui here, only thing you need to change is the path
        //May need to do getTexture first if this method causes trouble
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(new ResourceLocation("ftfloocraft","textures/gui/guifloowerpot.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
