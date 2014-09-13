package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import java.lang.Math;

public class GuiFlash extends Gui
{
    private Minecraft mc;
    private short ticks;
	
    public GuiFlash(Minecraft mc){this.mc = mc;this.ticks = -1;}

    public void start(){
        if(this.ticks == -1){
            //this.ticks = 0;
            this.ticks = 90;
        }}

    @SubscribeEvent
    public void flash(TickEvent.RenderTickEvent event)
    {
        if(this.ticks > -1)
        {
            if(event.phase == TickEvent.Phase.END)
            {
                //if(this.ticks == 0)
                //{
                  //  this.ticks = 45;
                //}
                //else if(this.ticks == 45)
                //{
                  //  this.ticks = 90;
                //}
                //else
                //{
                    this.ticks += 5;
                //}
                GL11.glColor4f(1.0F, 1.0F, 1.0F, (float) Math.sin(Math.PI / 180 * this.ticks));
                GL11.glDisable(GL11.GL_LIGHTING);
                this.mc.renderEngine.bindTexture(new ResourceLocation(DataReference.MODID+":textures/gui/flash.png"));
                this.drawTexturedModalRect(0, 0, 0, 0, this.mc.displayWidth, this.mc.displayHeight);
            }
            if(this.ticks > 179)
            {
                this.ticks = -1;
            }
        }
    }
}
