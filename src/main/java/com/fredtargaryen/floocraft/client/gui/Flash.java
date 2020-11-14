package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.config.ClientConfig;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class Flash {
    private double ticks;
    private Minecraft minecraft;
    private static final ResourceLocation texLoc = new ResourceLocation(DataReference.MODID, "textures/gui/flash.png");
    private TextureManager textureManager;
    private long startTime;

    private float yawDirectionStrength;
    private float pitchDirectionStrength;
    private float rollDirectionStrength;

    public Flash() {
        this.ticks = -1;
    }

    public void start() {
        if (this.ticks == -1) {
            this.ticks = 0;
            this.minecraft = Minecraft.getInstance();
            this.textureManager = this.minecraft.getTextureManager();
            MinecraftForge.EVENT_BUS.register(this);
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(FloocraftBase.TP, 1.0F));
            this.startTime = System.currentTimeMillis();

            //Determine roll parameters
            Random rand = this.minecraft.world.rand;
            this.yawDirectionStrength = (rand.nextBoolean() ? 1f : -1f) * (45f + rand.nextFloat() * 30f);
            this.pitchDirectionStrength = (rand.nextBoolean() ? 1f : -1f) * (45f + rand.nextFloat() * 30f);
            this.rollDirectionStrength = (rand.nextBoolean() ? 1f : -1f) * (45f + rand.nextFloat() * 30f);
        }
    }

    @SubscribeEvent
    public void flash(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.ticks = System.currentTimeMillis() - this.startTime;
            if (ClientConfig.ENABLE_FLASH.get()) {
                GlStateManager.disableAlphaTest();
                GlStateManager.disableDepthTest();
                GlStateManager.depthMask(false);
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, (float) Math.cos(Math.toRadians(this.ticks * 90 / 1000.0)));
                this.textureManager.bindTexture(texLoc);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                double width = this.minecraft.getMainWindow().getScaledWidth();
                double height = this.minecraft.getMainWindow().getScaledHeight();
                bufferbuilder.pos(width, height, -90.0).tex(1f, 1f).endVertex();
                bufferbuilder.pos(width, 0.0, -90.0).tex(1f, 0f).endVertex();
                bufferbuilder.pos(0.0, 0.0, -90.0).tex(0f, 0f).endVertex();
                bufferbuilder.pos(0.0, height, -90.0).tex(0f, 1f).endVertex();
                tessellator.draw();
                GlStateManager.depthMask(true);
                GlStateManager.enableDepthTest();
                GlStateManager.enableAlphaTest();
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        if (this.ticks > 2999) {
            this.ticks = -1;
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    @SubscribeEvent
    public void dizzy(EntityViewRenderEvent.CameraSetup event) {
        if (ClientConfig.ENABLE_DIZZY.get()) {
            float angle = (float) ((this.ticks / 3000.0) * Math.PI * 3);
            this.yawDirectionStrength *= 0.995f;
            this.pitchDirectionStrength *= 0.995f;
            this.rollDirectionStrength *= 0.995f;
            event.setYaw((float) (this.minecraft.player.rotationYaw + this.yawDirectionStrength * Math.sin(angle)));
            event.setPitch((float) (this.minecraft.player.rotationPitch + this.pitchDirectionStrength * Math.sin(angle)));
            event.setRoll((float) (this.rollDirectionStrength * Math.sin(angle)));
        }
    }
}
