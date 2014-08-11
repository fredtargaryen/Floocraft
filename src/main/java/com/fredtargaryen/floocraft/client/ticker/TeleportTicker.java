package com.fredtargaryen.floocraft.client.ticker;

import com.fredtargaryen.floocraft.DataReference;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class TeleportTicker
{
    private byte ticks = 0;

    public TeleportTicker()
    {
        this.ticks = -1;
    }

    public void start()
    {
        this.ticks = 0;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(this.ticks > -1)
        {
            if (event.phase == TickEvent.Phase.START)
            {
                if(this.ticks < 20)
                {
                    event.player.setRotationYawHead(event.player.rotationYawHead + 6.0F);
                }
                else if(this.ticks < 30)
                {
                    event.player.setRotationYawHead(event.player.rotationYawHead + 12.0F);
                }
                else if(this.ticks < 35)
                {
                    event.player.setRotationYawHead(event.player.rotationYawHead + 24.0F);
                }
                else if(this.ticks < 41)
                {
                    Minecraft mc = Minecraft.getMinecraft();
                    ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    int k = scaledresolution.getScaledWidth();
                    int l = scaledresolution.getScaledHeight();
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glDepthMask(false);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                    mc.getTextureManager().bindTexture(DataReference.TELEPORT_TEXTURE);
                    Tessellator tessellator = Tessellator.instance;
                    tessellator.startDrawingQuads();
                    tessellator.addVertexWithUV(0.0D, (double)l, -90.0D, 0.0D, 1.0D);
                    tessellator.addVertexWithUV((double)k, (double)l, -90.0D, 1.0D, 1.0D);
                    tessellator.addVertexWithUV((double)k, 0.0D, -90.0D, 1.0D, 0.0D);
                    tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
                    tessellator.draw();
                    GL11.glDepthMask(true);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)(Math.sqrt(1-((this.ticks-35)*(this.ticks-35)))+3));
                    GL11.glDisable(GL11.GL_BLEND);
                }
                else
                {
                    this.ticks = 0;
                }
            }
            this.ticks++;
        }
    }
}
