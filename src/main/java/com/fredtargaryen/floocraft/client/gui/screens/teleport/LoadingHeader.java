package com.fredtargaryen.floocraft.client.gui.screens.teleport;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class LoadingHeader extends FireplaceSelectionList.Entry {
    private static final Component LOADING_LABEL = Component.translatable("gui.teleport.loading");
    ;
    private final Minecraft minecraft;

    public LoadingHeader(Minecraft p_233222_) {
        this.minecraft = p_233222_;
    }

    public void render(GuiGraphics p_282319_, int p_283207_, int p_281352_, int p_283332_, int p_282400_, int p_282912_, int p_282760_, int p_281344_, boolean p_283655_, float p_283696_) {
        int i = (this.minecraft.screen.width - this.minecraft.font.width(LOADING_LABEL)) / 2;
        int j = p_281352_ + (p_282912_ - 9) / 2;
        p_282319_.drawString(this.minecraft.font, LOADING_LABEL, i, j, 16777215, false);
        String s = LoadingDotsText.get(Util.getMillis());
        int k = (this.minecraft.screen.width - this.minecraft.font.width(s)) / 2;
        int l = j + 9;
        p_282319_.drawString(this.minecraft.font, s, k, l, -8355712, false);
    }

    public @Nonnull Component getNarration() {
        return LOADING_LABEL;
    }
}