package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.opengl.GL11;

public class FlashScreen extends Screen {
    private byte ticks;
	
    public FlashScreen(Minecraft mc){
        super(new StringTextComponent(""));
        this.ticks = -1;
    }

    public void start() {
        if(this.ticks == -1) {
            this.ticks = 0;
            MinecraftForge.EVENT_BUS.register(this);
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(FloocraftBase.TP, 1.0F));
        }
    }

    @SubscribeEvent
    public void flash(TickEvent.RenderTickEvent event) {
        if(this.ticks > -1) {
            if(event.phase == TickEvent.Phase.END) {
                this.ticks += 5;
                GlStateManager.disableLighting();
                GlStateManager.enableBlend();
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);//(float) Math.cos(Math.PI / 180 * (this.ticks - 10)));
                Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(DataReference.MODID, "textures/gui/flash.png"));
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(1.0, 1.0, 0.0).tex(1.0, 1.0).endVertex();
                bufferbuilder.pos(1.0, 0.0, 0.0).tex(1.0, 0.0).endVertex();
                bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).endVertex();
                bufferbuilder.pos(0.0, 1.0, 0.0).tex(0.0, 1.0).endVertex();
                tessellator.draw();
                GlStateManager.disableBlend();
                GlStateManager.enableLighting();
            }
            if(this.ticks > 89) {
                this.ticks = -1;
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }
}
