package com.fredtargaryen.floocraft.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Ticker {
    private byte ticks;
    private byte maxTicks;

    public Ticker(byte maxTicks) {
        this.ticks = -1;
        this.maxTicks = maxTicks;
    }

    public void start() {
        if (!this.isRunning()) {
            this.ticks++;
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.ticks > -1) {
            if (event.phase == TickEvent.Phase.START) {
                this.ticks++;
            }
            if (this.ticks >= this.maxTicks) {
                this.ticks = -1;
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    public boolean isRunning() {
        return this.ticks > -1;
    }
}