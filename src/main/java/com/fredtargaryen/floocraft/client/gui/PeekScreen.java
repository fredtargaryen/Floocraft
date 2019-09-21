package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.MessageEndPeek;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class PeekScreen extends Screen {
    private boolean peekerSpawned;
    private String fireplaceName;
    private Button doneBtn;
    private Entity player;
    private UUID peekerID;
    private boolean peekFailedOutOfRange;

    public PeekScreen(String name, UUID peekerID) {
        super(new StringTextComponent(name));
        this.peekerSpawned = false;
        this.fireplaceName = name;
        this.peekerID = peekerID;
        MinecraftForge.EVENT_BUS.register(this);
        this.peekFailedOutOfRange = false;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void init() {
        this.buttons.clear();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        if(!this.peekerSpawned) {
            PeekerEntity ep = (PeekerEntity) FloocraftBase.getEntityWithUUID(this.minecraft.world, this.peekerID);
            if(ep == null) {
                //Give up. TODO Maybe one day someone can add peeking into chunks outside the view distance?
                this.peekFailedOutOfRange = true;
            }
            else {
                this.peekerSpawned = true;
                this.player = this.minecraft.getRenderViewEntity();
                this.minecraft.setRenderViewEntity(ep);
            }
        }
        this.addButton(this.doneBtn = new Button(this.width / 2 - 100, this.height - 40, 200, 20, "Mischief managed",
                button -> {
                    PeekScreen.this.onClose();
                    Minecraft.getInstance().displayGuiScreen(null);
                }));
        this.doneBtn.active = true;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onClose() {
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        MinecraftForge.EVENT_BUS.unregister(this);
        proxy.overrideTicker.start();
        this.minecraft.setRenderViewEntity(this.player);
        MessageEndPeek mep = new MessageEndPeek();
        mep.peekerUUID = this.peekerID;
        MessageHandler.INSTANCE.sendToServer(mep);
        super.onClose();
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    public boolean charTyped(char par1, int par2) {
        if (par2 == 1) {
            Minecraft.getInstance().displayGuiScreen(null);
        }
        return true;
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(int mousex, int mousey, float partialticks) {
        this.drawCenteredString(this.font,
                this.peekerSpawned ?
                        I18n.format("gui.peek.peeking", this.fireplaceName) :// + " " + this.fireplaceName :
                        this.peekFailedOutOfRange ?
                                I18n.format("gui.peek.outofrange") :
                                I18n.format("gui.peek.waiting"),
                this.width / 2,
                15,
                16777215);
        super.render(mousex, mousey, partialticks);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean isPauseScreen() {
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