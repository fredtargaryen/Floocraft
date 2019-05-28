package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

public class GuiFlash extends Gui
{
    private final Minecraft mc;
    private byte ticks;
	
    public GuiFlash(Minecraft mc){this.mc = mc;this.ticks = -1;}

    public void start()
    {
        if(this.ticks == -1)
        {
            this.ticks = 0;
            MinecraftForge.EVENT_BUS.register(this);
            this.mc.getSoundHandler().play(SimpleSound.getMasterRecord(FloocraftBase.TP, 1.0F));
        }
    }

    @SubscribeEvent
    public void flash(TickEvent.RenderTickEvent event)
    {
        if(this.ticks > -1)
        {
            if(event.phase == TickEvent.Phase.END)
            {
                this.ticks += 5;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, (float) Math.cos(Math.PI / 180 * this.ticks));
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.getTextureManager().bindTexture(new ResourceLocation(DataReference.MODID, "textures/gui/flash.png"));
                this.drawTexturedModalRect(0, 0, 0, 0, this.mc.mainWindow.getWidth(), this.mc.mainWindow.getHeight());
            }
            if(this.ticks > 89)
            {
                this.ticks = -1;
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }
}
