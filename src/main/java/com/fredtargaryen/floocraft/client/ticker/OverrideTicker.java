package com.fredtargaryen.floocraft.client.ticker;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

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
			if (this.ticks > 60)
			{
                this.ticks = -1;
			}
	    }
	}
	
	public boolean isOverriding()
	{
		return this.ticks > -1;
	}
}
