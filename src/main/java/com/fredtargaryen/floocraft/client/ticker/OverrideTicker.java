package com.fredtargaryen.floocraft.client.ticker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class OverrideTicker
{
	private byte ticks;

	public OverrideTicker()
	{
		this.ticks = -1;
	}

	public void start()
	{
		if(this.ticks == -1)
        {
            this.ticks++;
            MinecraftForge.EVENT_BUS.register(this);
        }
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
		if(this.ticks > -1)
		{
			if (event.phase == TickEvent.Phase.START)
			{
				this.ticks++;
            }
			if (this.ticks > 94)
			{
                this.ticks = -1;
                MinecraftForge.EVENT_BUS.unregister(this);
			}
	    }
	}
	
	public boolean isOverriding()
	{
		return this.ticks > -1;
	}
}
