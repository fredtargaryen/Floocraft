package com.fredtargaryen.floocraft.client.gui.screens.inventory;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.inventory.FloowerPotMenu;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.FloowerPotSettingsUpdateMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FloowerPotScreen extends AbstractContainerScreen<FloowerPotMenu> {
    public static final ResourceLocation FLOOWER_POT_GUI_BACKGROUND_LOCATION = DataReference.getResourceLocation("textures/gui/floower_pot.png");
    private Button decrementHorizontalRangeButton;
    private Button incrementHorizontalRangeButton;
    private Button decrementVerticalRangeButton;
    private Button incrementVerticalRangeButton;
    private int hRangeCache;
    private int vRangeCache;

    private static final Component MINUS = Component.literal("-");
    private static final Component PLUS = Component.literal("+");
    private static final Component POT_RANGE = Component.translatable("gui.pot.range");
    private static final Component HORIZONTAL_TEXT = Component.translatable("gui.pot.horizontal");
    private static final Component VERTICAL_TEXT = Component.translatable("gui.pot.vertical");


    private static final boolean HORIZONTAL = false;
    private static final boolean VERTICAL = true;

    public FloowerPotScreen(FloowerPotMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.hRangeCache = DataReference.POT_MIN_H_RANGE;
        this.vRangeCache = DataReference.POT_MIN_V_RANGE;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        int ySize = this.getYSize();
        this.decrementHorizontalRangeButton = this.addRenderableWidget(
                Button.builder(MINUS, button -> {
                    if (this.hRangeCache > DataReference.POT_MIN_H_RANGE) {
                        this.hRangeCache--;
                        FloowerPotScreen.this.sendPotRangeMessage(HORIZONTAL, -1);
                    }
                })
                        .bounds(left + 8,
                                top + ySize - 96 - 30,
                                10,
                                10)
                        .build());

        this.incrementHorizontalRangeButton = this.addRenderableWidget(
                Button.builder(PLUS, button -> {
                    if (this.hRangeCache < DataReference.POT_MAX_H_RANGE) {
                        this.hRangeCache++;
                        FloowerPotScreen.this.sendPotRangeMessage(HORIZONTAL, 1);
                    }
                })
                        .bounds(left + 28,
                                top + ySize - 96 - 30,
                                10,
                                10)
                        .build());

        this.decrementVerticalRangeButton = this.addRenderableWidget(
                Button.builder(MINUS, button -> {
                    if (this.vRangeCache > DataReference.POT_MIN_V_RANGE) {
                        this.vRangeCache--;
                        FloowerPotScreen.this.sendPotRangeMessage(VERTICAL, -1);
                    }
                })
                        .bounds(left + 8,
                                top + ySize - 96 - 10,
                                10,
                                10)
                        .build());

        this.incrementVerticalRangeButton = this.addRenderableWidget(
                Button.builder(PLUS, button -> {
                    if (this.vRangeCache < DataReference.POT_MAX_V_RANGE) {
                        this.vRangeCache++;
                        FloowerPotScreen.this.sendPotRangeMessage(VERTICAL, 1);
                    }
                })
                        .bounds(left + 28,
                                top + ySize - 96 - 10,
                                10,
                                10)
                        .build());
    }

    @Override
    public void containerTick() {
        this.decrementHorizontalRangeButton.active = this.hRangeCache > DataReference.POT_MIN_H_RANGE;
        this.incrementHorizontalRangeButton.active = this.hRangeCache < DataReference.POT_MAX_H_RANGE;
        this.decrementVerticalRangeButton.active = this.vRangeCache > DataReference.POT_MIN_V_RANGE;
        this.incrementVerticalRangeButton.active = this.vRangeCache < DataReference.POT_MAX_V_RANGE;

//        if (this.fpbe.justUpdated) {
//            this.hRangeCache = this.fpbe.getHRange();
//            this.vRangeCache = this.fpbe.getVRange();
//            this.fpbe.justUpdated = false;
//        }
    }

    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param graphics the GuiGraphics object used for rendering.
     * @param x      the x-coordinate of the mouse cursor.
     * @param y      the y-coordinate of the mouse cursor.
     * @param partialTicks the partial tick time.
     */
    @Override
    public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
        super.render(graphics, x, y, partialTicks);
        this.renderTooltip(graphics, x, y);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        int ySize = this.getYSize();
        //graphics.drawString(this.font, I18n.format("block.floocraftft.floower_pot"), 8, 6, 4210752);
        //graphics.drawString(this.font, I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
        graphics.drawString(this.font, POT_RANGE, 8, ySize - 96 - 48, 4210752);
        graphics.drawString(this.font, HORIZONTAL_TEXT, 8, ySize - 96 - 38, 4210752);
        graphics.drawString(this.font, "" + this.hRangeCache, 20, ySize - 96 - 28, 4210752);
        graphics.drawString(this.font, VERTICAL_TEXT, 8, ySize - 96 - 18, 4210752);
        graphics.drawString(this.font, "" + this.vRangeCache, 20, ySize - 96 - 8, 4210752);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float par1, int par2, int par3) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(FLOOWER_POT_GUI_BACKGROUND_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void sendPotRangeMessage(boolean rangeToChange, int amount) {
        MessageHandler.sendToServer(
                new FloowerPotSettingsUpdateMessage(
                        rangeToChange,
                        amount,
                        new BlockPos(0, 0, 0)));
    }
}
