package com.fredtargaryen.floocraft.client.gui.screens.teleport;

import com.fredtargaryen.floocraft.network.messages.FireplaceListResponseMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class FireplaceSelectionList extends ObjectSelectionList<FireplaceSelectionList.Entry> {
    private final TeleportScreen parent;
    private final LoadingHeader loadingHeader;
    protected int topY;
    private static final int ENTRY_HEIGHT = 26; // Probably should be 36

    public FireplaceSelectionList(TeleportScreen mainScreen, Minecraft minecraft, int topY, int width, int height) {
        super(minecraft, width, height, topY, ENTRY_HEIGHT);
        this.parent = mainScreen;
        this.loadingHeader = new LoadingHeader(minecraft);
        this.topY = topY;
    }

    public void receiveFireplaceList(FireplaceListResponseMessage flrm) {
        this.clearEntries();
        List<String> places = flrm.places();
        List<Boolean> enabledList = flrm.enabledList();
        List<Boolean> canPeekList = flrm.canPeekList();
        int playerPlaceIndex = flrm.playerPlaceIndex();
        for (int i = 0; i < flrm.places().size(); i++) {
            this.addEntry(new FireplaceListEntry(
                    this.minecraft,
                    this,
                    places.get(i),
                    enabledList.get(i),
                    canPeekList.get(i),
                    i == playerPlaceIndex));
        }
        this.notifyListUpdated();
    }

    public void renderWidget(GuiGraphics graphics, int p_312182_, int p_312151_, float p_311062_) {
        super.renderWidget(graphics, p_312182_, p_312151_, p_311062_);
    }

    private void notifyListUpdated() {
        this.setScrollAmount(this.scrollAmount());
        this.parent.triggerImmediateNarration(true);
    }

    public int getRowWidth() {
        return 380;
    }

    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        if (entry instanceof FireplaceListEntry) {
            FireplaceListEntry fle = (FireplaceListEntry) entry;
            this.parent.setButtonActivity(fle.enabled, fle.canPeek);
        }
    }

    public Optional<FireplaceListEntry> getSelectedOpt() {
        Entry entry = this.getSelected();
        if (entry instanceof FireplaceListEntry FireplaceSelectionList$worldlistentry) {
            return Optional.of(FireplaceSelectionList$worldlistentry);
        } else {
            return Optional.empty();
        }
    }

    public TeleportScreen getScreen() {
        return this.parent;
    }

    public void updateWidgetNarration(NarrationElementOutput p_313204_) {
        if (this.children().contains(this.loadingHeader)) {
            this.loadingHeader.updateNarration(p_313204_);
        } else {
            super.updateWidgetNarration(p_313204_);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public abstract static class Entry extends ObjectSelectionList.Entry<Entry> implements AutoCloseable {
        public Entry() {
        }

        public void close() {
        }
    }
}
