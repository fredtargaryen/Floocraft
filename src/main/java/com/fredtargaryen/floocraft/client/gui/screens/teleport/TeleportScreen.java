package com.fredtargaryen.floocraft.client.gui.screens.teleport;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.screens.PeekScreen;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class TeleportScreen extends Screen {
    private Screen lastScreen;

    private Component status;
    private Button refreshButton;
    private Button peekButton;
    private Button goButton;
    private Button cancelButton;

    private FireplaceSelectionList fireplaces;

    private List<Boolean> enabledList;

    private List<Boolean> canPeekList;

    private final BlockPos initPos;

    private List<String> placeList;

    // Localised text
    private static final MutableComponent TITLE = Component.translatable("gui.teleport.title");
    private static final MutableComponent REFRESH = Component.translatable("gui.teleport.refresh");
    private static final MutableComponent PEEK = Component.translatable("gui.teleport.peek");
    private static final MutableComponent TELEPORT = Component.translatable("gui.teleport.go");
    private static final MutableComponent CANCEL = Component.translatable("gui.teleport.cancel");
    private static final MutableComponent LOADING = Component.translatable("gui.teleport.loading");
    private static final MutableComponent PLACE_LIST_EMPTY = Component.translatable("gui.teleport.empty");

    // Constants for placing everything
    private static final int SECTION_HEIGHT = 30;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_PADDING = 8;
    private static final int BUTTON_PADDING_FROM_EDGE = 2;

    private int peekAttemptTimer;

    public TeleportScreen(Screen lastScreen, BlockPos pos) {
        super(Component.translatable("gui.teleport.title"));
        this.lastScreen = lastScreen;
        this.initPos = pos.immutable();
        this.placeList = new ArrayList<>();
        this.enabledList = new ArrayList<>();
        this.canPeekList = new ArrayList<>();
        this.peekAttemptTimer = 0;
        this.status = LOADING;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    protected void init() {
        StringWidget titleWidget = new StringWidget(TITLE, this.font);
        titleWidget.setPosition(this.width / 2 - titleWidget.getWidth() / 2, SECTION_HEIGHT / 2 - titleWidget.getHeight() / 2);
        this.addRenderableWidget(titleWidget);

        this.refreshButton = this.addRenderableWidget(
                Button.builder(REFRESH, button -> {
                            TeleportScreen.this.getPlaceList();
                            button.active = false;
                        })
                        .bounds(this.width - BUTTON_WIDTH - BUTTON_PADDING_FROM_EDGE,
                                BUTTON_PADDING_FROM_EDGE,
                                BUTTON_WIDTH,
                                BUTTON_HEIGHT)
                        .build());
        this.refreshButton.active = false;

        int bottomButtonY = this.height - 15 - BUTTON_HEIGHT / 2;

        this.peekButton = this.addRenderableWidget(
                Button.builder(PEEK, button -> {
                            this.fireplaces.getSelectedOpt().ifPresent(entry -> {
                                try {
                                    StartPeekRequestMessage message = new StartPeekRequestMessage(
                                            TeleportScreen.this.initPos, entry.placeName);
                                    MessageHandler.sendToServer(message);
                                    this.peekButton.active = false;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        })
                        .bounds(this.width / 2 - (int) (BUTTON_WIDTH * 1.5) - BUTTON_PADDING,
                                bottomButtonY,
                                BUTTON_WIDTH,
                                BUTTON_HEIGHT)
                        .build());
        this.peekButton.active = false;

        this.goButton = this.addRenderableWidget(
                Button.builder(TELEPORT, button -> {
                            this.fireplaces.getSelectedOpt().ifPresent(entry -> {
                                try {
                                    TeleportRequestMessage tm = new TeleportRequestMessage(
                                            this.initPos,
                                            entry.placeName);
                                    MessageHandler.sendToServer(tm);
                                    this.goButton.active = false;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                TeleportScreen.this.onClose();
                            });
                        })
                        .bounds(this.width / 2 - BUTTON_WIDTH / 2,
                                bottomButtonY,
                                BUTTON_WIDTH,
                                BUTTON_HEIGHT)
                        .build());
        this.goButton.active = false;

        this.cancelButton = this.addRenderableWidget(
                Button.builder(CANCEL, button -> {
                            TeleportScreen.this.onClose();
                        })
                        .bounds(this.width / 2 + BUTTON_WIDTH / 2 + 8,
                                bottomButtonY,
                                BUTTON_WIDTH,
                                BUTTON_HEIGHT)
                        .build());

        this.fireplaces = this.addRenderableWidget(
                new FireplaceSelectionList(this, this.minecraft,
                        SECTION_HEIGHT,
                        this.width,
                        this.height - SECTION_HEIGHT * 2)
        );

        this.getPlaceList();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onClose() {
        FloocraftBase.ClientModEvents.flashTicker.start();
        assert this.minecraft != null;
        this.minecraft.setScreen(this.lastScreen);
    }

//    /**
//     * Called from the main game loop to update the screen.
//     */
//    @Override
//    public void tick() {
//        super.tick();
//        if (this.receivedLists) {
//            //if the lists were received...
//            //if they are empty...
//            if (this.placeList.length != 0) {
//                this.status = PLACE_LIST_EMPTY;
//            } else {
//                if (this.peekAttemptTimer == 0) {
//                    this.status = Component.empty();
//                }
//            }
//        }
//        if (this.peekAttemptTimer > 0) {
//            --this.peekAttemptTimer;
//        }
//    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialticks) {
        super.render(graphics, mouseX, mouseY, partialticks);
        this.fireplaces.render(graphics, mouseX, mouseY, partialticks);
        graphics.drawCenteredString(
                this.font,
                this.status,
                this.width / 2,
                this.height / 4 + 48,
                13158600);
    }

    public void setButtonActivity(boolean enabled, boolean canPeek) {
        this.goButton.active = enabled;
        this.peekButton.active = canPeek;
    }

    private void getPlaceList() {
        this.placeList.clear();
        this.enabledList.clear();
        this.canPeekList.clear();
        this.status = LOADING;
        MessageHandler.sendToServer(new FireplaceListRequestMessage(this.initPos));
    }

    public void receiveFireplaceList(FireplaceListResponseMessage flrm) {
        try {
            this.placeList = flrm.places();
            this.enabledList = flrm.enabledList();
            this.canPeekList = flrm.canPeekList();
            this.fireplaces.receiveFireplaceList(flrm);
            this.status = this.placeList.isEmpty() ?
                    PLACE_LIST_EMPTY :
                    Component.empty();
            this.refreshButton.active = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onStartPeek(StartPeekResponseMessage message) {
        Optional<FireplaceListEntry> opt = this.fireplaces.getSelectedOpt();
        if (opt.isPresent()) {
            this.minecraft.setScreen(new PeekScreen(
                    opt.get().placeName,
                    new UUID(message.peekerMsb(), message.peekerLsb())));
        }
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        if (this.fireplaces != null) {
            this.fireplaces.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        }
        return true;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
