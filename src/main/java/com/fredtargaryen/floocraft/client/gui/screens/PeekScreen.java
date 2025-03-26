package com.fredtargaryen.floocraft.client.gui.screens;

import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.EndPeekMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class PeekScreen extends Screen {
    private boolean peekerSpawned;
    private Button doneBtn;
    private Entity player;
    private int peekerNetworkID;
    private boolean peekFailedOutOfRange;

    private static Component TITLE;
    private static final Component PEEK_DONE = Component.translatable("gui.peek.done");

    public PeekScreen(String name, int peekerNetworkID) {
        super(Component.translatable("gui.peek.peeking", name));
        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;
        TITLE = Component.translatable("gui.peek.peeking", name);
        this.peekerSpawned = false;
        this.peekerNetworkID = peekerNetworkID;
        NeoForge.EVENT_BUS.register(this);
        this.peekFailedOutOfRange = false;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    protected void init() {
        this.doneBtn = this.addRenderableWidget(
                Button.builder(PEEK_DONE, button -> {
                            PeekScreen.this.onClose();
                        })
                        .bounds(this.width / 2 - 100,
                                this.height - 40,
                                200,
                                20)
                        .build());
        this.doneBtn.active = true;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onClose() {
        NeoForge.EVENT_BUS.unregister(this);
        this.minecraft.setCameraEntity(this.player);
        EndPeekMessage message = new EndPeekMessage(this.peekerNetworkID);
        MessageHandler.sendToServer(message);
        super.onClose();
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    public boolean charTyped(char par1, int par2) {
        if (par2 == 1) {
            Minecraft.getInstance().setScreen(null);
        }
        return true;
    }

    @Override
    public void tick() {
        if (!this.peekerSpawned && this.minecraft.level != null) {
            Entity peekerCandidate = this.minecraft.level.getEntity(this.peekerNetworkID);
            if (peekerCandidate == null) {
                this.peekFailedOutOfRange = true;
                return;
            }
            if (peekerCandidate instanceof PeekerEntity) {
                this.peekerSpawned = true;
                this.player = this.minecraft.getCameraEntity();
                this.minecraft.setCameraEntity(peekerCandidate);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialticks) {
        super.render(graphics, mouseX, mouseY, partialticks);
        graphics.drawCenteredString(
                this.font,
                this.peekerSpawned ? TITLE :
                        this.peekFailedOutOfRange ? Component.translatable("gui.peek.out_of_range") :
                                Component.translatable("gui.peek.waiting"),
                this.width / 2,
                15,
                16777215);

    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @SubscribeEvent
    public void onHurt(LivingDamageEvent.Pre lde) {
        if (lde.getEntity() == this.player) {
            Minecraft.getInstance().setScreen(null);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent lde) {
        if (lde.getEntity() == this.player) {
            Minecraft.getInstance().setScreen(null);
        }
    }
}