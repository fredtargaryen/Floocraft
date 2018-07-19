package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageEndPeek;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class GuiPeek extends GuiScreen {
    private boolean peekerSpawned;
    private String fireplaceName;
    private GuiButton doneBtn;
    private Entity player;
    private UUID peekerID;

    public GuiPeek(String name, UUID peekerID)
    {
        super();
        this.peekerSpawned = false;
        this.fireplaceName = name;
        this.peekerID = peekerID;
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        if(!this.peekerSpawned) {
            EntityPeeker ep = (EntityPeeker) FloocraftBase.getEntityWithUUID(this.mc.world, this.peekerID);
            if(ep != null) {
                this.peekerSpawned = true;
                this.player = this.mc.getRenderViewEntity();
                int chunkX = ep.chunkCoordX;
                int chunkZ = ep.chunkCoordZ;
                Chunk peekerChunk = this.mc.world.getChunkFromChunkCoords(chunkX, chunkZ);
                if(!peekerChunk.isLoaded()) {
                    for (int x = chunkX - 1; x < chunkX + 2; ++chunkX) {
                        for (int z = chunkZ - 1; z < chunkZ + 2; ++chunkZ) {
                            this.mc.world.getChunkProvider().loadChunk(chunkX, chunkZ);
                        }
                    }
                }
                this.mc.setRenderViewEntity(ep);
            }
        }
        this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height - 40, 200, 20, "Mischief managed"));
        this.doneBtn.enabled = true;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
        Keyboard.enableRepeatEvents(false);
        MinecraftForge.EVENT_BUS.unregister(this);
        proxy.overrideTicker.start();
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            if (par1GuiButton.id == 0)
            {
                ((ClientProxy) FloocraftBase.proxy).overrideTicker.start();
                this.mc.displayGuiScreen(null);
                this.mc.setRenderViewEntity(this.player);
                MessageEndPeek mep = new MessageEndPeek();
                mep.peekerUUID = this.peekerID;
                PacketHandler.INSTANCE.sendToServer(mep);
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char par1, int par2)
    {
        if (par2 == 1)
        {
            this.actionPerformed(this.doneBtn);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreen(int mousex, int mousey, float partialticks)
    {
        this.drawCenteredString(this.fontRendererObj,
                this.peekerSpawned ? "Peeking into "+this.fireplaceName : "Waiting...",
                this.width / 2,
                15,
                16777215);
        super.drawScreen(mousex, mousey, partialticks);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent lhe) {
        if(lhe.getEntity() == this.player) {
            this.actionPerformed(this.doneBtn);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent lde) {
        if(lde.getEntity() == this.player) {
            this.actionPerformed(this.doneBtn);
        }
    }
}
