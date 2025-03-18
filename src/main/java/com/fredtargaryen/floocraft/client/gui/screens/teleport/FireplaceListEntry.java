package com.fredtargaryen.floocraft.client.gui.screens.teleport;

import com.fredtargaryen.floocraft.DataReference;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public final class FireplaceListEntry extends FireplaceSelectionList.Entry {
    private final Minecraft minecraft;
    private final TeleportScreen screen;
    private final FireplaceSelectionList list;
    private final Font font;

    public final String placeName;
    public final boolean enabled;
    public final boolean canPeek;
    public final boolean playerIsHere;

    private long lastClickTime;

    public FireplaceListEntry(Minecraft minecraft, final FireplaceSelectionList list, String placeName, boolean enabled, boolean canPeek, boolean playerIsHere) {
        this.minecraft = minecraft;
        this.list = list;
        this.screen = list.getScreen();
        this.font = this.minecraft.font;
        this.playerIsHere = playerIsHere;
        this.placeName = playerIsHere ?
                Component.translatable("gui.teleport.player_is_here", placeName).getString()
                : placeName;
        this.enabled = enabled;
        this.canPeek = canPeek;
    }

    public @Nonnull Component getNarration() {
        return Component.empty();
    }

    public void render(GuiGraphics graphics, int index, int rowTop, int p_282820_, int rowWidth, int p_281855_, int p_283204_, int p_283025_, boolean p_283396_, float partialTicks) {
        graphics.drawCenteredString(
                this.font,
                placeName,
                this.screen.width / 2,
                rowTop + 7,
                enabled ? DataReference.FLOO_GREEN_COLOUR : DataReference.RED_COLOUR);
    }

    public boolean mouseClicked(double p_101706_, double p_101707_, int p_101708_) {
//            if (!this.summary.primaryActionActive()) {
//                return true;
//            } else {
        this.list.setSelected(this);
        if (!(p_101706_ - (double) this.list.getRowLeft() <= 32.0) && Util.getMillis() - this.lastClickTime >= 250L) {
            this.lastClickTime = Util.getMillis();
            return super.mouseClicked(p_101706_, p_101707_, p_101708_);
        } else {
            if (this.canJoin()) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            //TeleportScreen.this.scrollWindow.setSelected(this);
            return true;
        }
        //}
    }

    public boolean canJoin() {
        return false;
    }
}