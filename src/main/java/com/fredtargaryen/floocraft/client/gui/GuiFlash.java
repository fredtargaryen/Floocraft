package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
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
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(DataReference.MODID+":tp"), 1.0F));
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
                this.mc.renderEngine.bindTexture(new ResourceLocation(DataReference.MODID+":textures/gui/flash.png"));
                this.drawTexturedModalRect(0, 0, 0, 0, this.mc.displayWidth, this.mc.displayHeight);
            }
            if(this.ticks > 89)
            {
                this.ticks = -1;
            }
        }
    }
}
