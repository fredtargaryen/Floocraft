package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageEndPeek;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class GuiPeek extends GuiScreen {
    private boolean peekerSpawned;
    private String fireplaceName;
    private GuiButton doneBtn;
    private Entity player;
    private UUID peekerID;
    private boolean peekFailedOutOfRange;

    public GuiPeek(String name, UUID peekerID) {
        super();
        this.peekerSpawned = false;
        this.fireplaceName = name;
        this.peekerID = peekerID;
        MinecraftForge.EVENT_BUS.register(this);
        this.peekFailedOutOfRange = false;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.buttons.clear();
        this.mc.keyboardListener.enableRepeatEvents(true);
        if(!this.peekerSpawned) {
            EntityPeeker ep = (EntityPeeker) FloocraftBase.getEntityWithUUID(this.mc.world, this.peekerID);
            if(ep == null) {
                //Give up. Maybe one day someone can add peeking into chunks outside the view distance?
                this.peekFailedOutOfRange = true;
            }
            else {
                this.peekerSpawned = true;
                this.player = this.mc.getRenderViewEntity();
                this.mc.setRenderViewEntity(ep);
            }
        }
        this.addButton(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height - 40, 200, 20, "Mischief managed") {
            @Override
            public void onClick(double mouseX, double mouseY) {
                Minecraft.getInstance().displayGuiScreen(null);
            }
        });
        this.doneBtn.enabled = true;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
        this.mc.keyboardListener.enableRepeatEvents(false);
        MinecraftForge.EVENT_BUS.unregister(this);
        proxy.overrideTicker.start();
        this.mc.setRenderViewEntity(this.player);
        MessageEndPeek mep = new MessageEndPeek();
        mep.peekerUUID = this.peekerID;
        PacketHandler.INSTANCE.sendToServer(mep);
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    public boolean charTyped(char par1, int par2) {
        if (par2 == 1) {
            Minecraft.getInstance().displayGuiScreen(null);
        }
        return true;
    }

    /**
     * Draws the screen and all the components in it.
     */
    @OnlyIn(Dist.CLIENT)
    public void render(int mousex, int mousey, float partialticks) {
        this.drawCenteredString(this.fontRenderer,
                this.peekerSpawned ?
                        "Peeking into "+this.fireplaceName :
                        this.peekFailedOutOfRange ?
                                "Peek failed: you are looking somewhere beyond your render distance" :
                                "Waiting...",
                this.width / 2,
                15,
                16777215);
        super.render(mousex, mousey, partialticks);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame() {
        return false;
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent lhe) {
        if(lhe.getEntity() == this.player) {
            Minecraft.getInstance().displayGuiScreen(null);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent lde) {
        if(lde.getEntity() == this.player) {
            Minecraft.getInstance().displayGuiScreen(null);
        }
    }
}