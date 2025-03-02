package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.config.ClientConfig;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

public class TeleportEffects {
    private Minecraft minecraft;

    private float time;
    private float previousPartialTicks;

    private float yawDirectionStrength;
    private float pitchDirectionStrength;
    private float rollDirectionStrength;

    private int flashColour;
    private boolean running;

    public TeleportEffects() {
        this.running = false;
    }

    /**
     * In ticks, how long the flash effect should last
     */
    private static final float FLASH_DURATION = 1.5f * 20;

    /**
     * In ticks, how long the dizzy effect should last
     */
    private static final float DIZZY_DURATION = 3f * 20;

    public void start(boolean soul) {
        if (!this.running) {
            this.flashColour = soul ? DataReference.FLOO_SOUL_COLOUR : DataReference.FLOO_GREEN_COLOUR;
            this.minecraft = Minecraft.getInstance();
            this.startAndRegister();

            //Determine roll parameters
            Level level = this.minecraft.level;
            if (level == null) {
                this.stopAndUnregister();
            } else {
                RandomSource rand = level.random;
                this.yawDirectionStrength = (rand.nextBoolean() ? 1f : -1f) * (45f + rand.nextFloat() * 30f);
                this.pitchDirectionStrength = (rand.nextBoolean() ? 1f : -1f) * (45f + rand.nextFloat() * 30f);
                this.rollDirectionStrength = (rand.nextBoolean() ? 1f : -1f) * (45f + rand.nextFloat() * 30f);
            }
        }
    }

    private void startAndRegister() {
        this.running = true;
        this.time = 0f;
        NeoForge.EVENT_BUS.register(this);
    }

    private void stopAndUnregister() {
        this.running = false;
        this.time = 0f;
        NeoForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void flash(RenderGuiEvent.Pre event) {
        if (ClientConfig.ENABLE_FLASH && this.time <= FLASH_DURATION) {
            float progress = this.time / FLASH_DURATION;
            float alpha = (float) Math.cos(progress * Math.PI / 2.0);
            GuiGraphics graphics = event.getGuiGraphics();
            PoseStack stack = graphics.pose();
            stack.pushPose();
            Window window = this.minecraft.getWindow();
            graphics.fill(
                    0, 0,
                    window.getScreenWidth(), window.getScreenHeight(),
                    8192, // Puts the flash in the foreground, even over the hotbar
                    (int) (alpha * 255) << 24 | this.flashColour // Apply alpha to the flash rectangle
            );
            stack.popPose();
        }
    }

    @SubscribeEvent
    public void dizzy(ViewportEvent.ComputeCameraAngles event) {
        float pt = event.getCamera().getPartialTickTime();
        this.time += getElapsedPartialTicks(this.previousPartialTicks, pt);
        this.previousPartialTicks = pt;
        if (ClientConfig.ENABLE_DIZZY && this.time <= DIZZY_DURATION) {
            float progress = this.time / DIZZY_DURATION;
            float angle = progress * (float) Math.PI * 2;
            float strength = (1 - progress) * 1.1f;
            if (this.minecraft.player != null) {
                event.setYaw((float) (this.minecraft.player.getYRot() + this.yawDirectionStrength * strength * Math.sin(angle)));
                event.setPitch((float) (this.minecraft.player.getXRot() + this.pitchDirectionStrength * strength * Math.sin(angle)));
                event.setRoll((float) (this.rollDirectionStrength * strength * Math.sin(angle)));
            }
        }
        if (this.time >= DIZZY_DURATION) {
            this.stopAndUnregister();
        }
    }

    private float getElapsedPartialTicks(float oldPt, float newPt) {
        if (newPt > oldPt) return newPt - oldPt;
        if (newPt == oldPt) return 0f;
        return (1f - oldPt) + newPt;
    }
}
