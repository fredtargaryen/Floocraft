package com.fredtargaryen.floocraft.client;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

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
            NeoForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event) {
        if (this.ticks > -1) {
            this.ticks++;
            if (this.ticks >= this.maxTicks) {
                this.ticks = -1;
                NeoForge.EVENT_BUS.unregister(this);
            }
        }
    }

    public boolean isRunning() {
        return this.ticks > -1;
    }
}