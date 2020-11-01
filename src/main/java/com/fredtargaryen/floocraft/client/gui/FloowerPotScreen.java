package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.inventory.container.FloowerPotContainer;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.MessagePotRange;
import com.fredtargaryen.floocraft.tileentity.FloowerPotTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class FloowerPotScreen extends ContainerScreen<FloowerPotContainer> {
    private Button hLess;
    private Button hMore;
    private Button vLess;
    private Button vMore;
    private FloowerPotTileEntity fpte;
    private int hRangeCache;
    private int vRangeCache;

    private static final StringTextComponent MINUS = new StringTextComponent("-");
    private static final StringTextComponent PLUS = new StringTextComponent("+");

    public FloowerPotScreen(FloowerPotContainer container, PlayerInventory inv, ITextComponent itc) {
        super(container, inv, itc);
        this.fpte = (FloowerPotTileEntity) container.getTileEntity();
        this.hRangeCache = DataReference.POT_MIN_H_RANGE;
        this.vRangeCache = DataReference.POT_MIN_V_RANGE;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void init() {
        super.init();
        this.buttons.clear();
        this.hLess = new Button(guiLeft + 8, guiTop + ySize - 96 - 30, 10, 10, MINUS, button -> {
            this.hRangeCache -= 1;
            FloowerPotScreen.this.sendPotRangeMessage('h', -1);
        });
        this.addButton(this.hLess);
        this.hMore = new Button(guiLeft + 28, guiTop + ySize - 96 - 30, 10, 10, PLUS, button -> {
            this.hRangeCache += 1;
            FloowerPotScreen.this.sendPotRangeMessage('h', 1);
        });
        this.addButton(this.hMore);
        this.vLess = new Button(guiLeft + 8, guiTop + ySize - 96 - 10, 10, 10, MINUS, button -> {
            this.vRangeCache -= 1;
            FloowerPotScreen.this.sendPotRangeMessage('v', -1);
        });
        this.addButton(this.vLess);
        this.vMore = new Button(guiLeft + 28, guiTop + ySize - 96 - 10, 10, 10, PLUS, button -> {
            this.vRangeCache += 1;
            FloowerPotScreen.this.sendPotRangeMessage('v', 1);
        });
        this.addButton(this.vMore);
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float partialTicks) {
        super.render(stack, x, y, partialTicks);
        this.renderHoveredTooltip(stack, x, y);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int param1, int param2) {
        if(this.fpte.justUpdated) {
            this.hRangeCache = this.fpte.getHRange();
            this.vRangeCache = this.fpte.getVRange();
            this.fpte.justUpdated = false;
        }
        this.hLess.active = this.hRangeCache != DataReference.POT_MIN_H_RANGE;
        this.hMore.active = this.hRangeCache != DataReference.POT_MAX_H_RANGE;
        this.vLess.active = this.vRangeCache != DataReference.POT_MIN_V_RANGE;
        this.vMore.active = this.vRangeCache != DataReference.POT_MAX_V_RANGE;
        //the parameters for drawString are: string, x, y, color
        this.font.drawString(stack, I18n.format("block.floocraftft.floowerpot"), 8, 6, 4210752);
        //draws "Inventory" or your regional equivalent
        this.font.drawString(stack, I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
        this.font.drawString(stack, I18n.format("gui.pot.range"), 8, ySize - 96 - 48, 4210752);
        this.font.drawString(stack, I18n.format("gui.pot.horizontal"), 8, ySize - 96 - 38, 4210752);
        this.font.drawString(stack, "" + this.hRangeCache, 20, ySize - 96 - 28, 4210752);
        this.font.drawString(stack, I18n.format("gui.pot.vertical"), 8, ySize - 96 - 18, 4210752);
        this.font.drawString(stack, "" + this.vRangeCache, 20, ySize - 96 - 8, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float par1, int par2, int par3) {
        //draw your Gui here, only thing you need to change is the path
        //May need to do getTexture first if this method causes trouble
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bindTexture(new ResourceLocation(DataReference.MODID, "textures/gui/guifloowerpot.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.blit(stack, x, y, 0, 0, xSize, ySize);
    }

    private void sendPotRangeMessage(char range, int amount) {
        MessagePotRange mpr = new MessagePotRange();
        mpr.range = range;
        mpr.amount = amount;
        BlockPos blockPos = this.fpte.getPos();
        mpr.pos = blockPos;
        MessageHandler.INSTANCE.sendToServer(mpr);
    }
}
