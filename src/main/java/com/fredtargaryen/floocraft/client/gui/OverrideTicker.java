package com.fredtargaryen.floocraft.client.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class OverrideTicker
{
	public int override;
	
	public OverrideTicker()
	{
		this.override = -1;
	}
	public void start()
	{
		this.override = 0;
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
	    if (event.phase == TickEvent.Phase.START)
	    {
			if(this.override == 0)
			{
				this.override++;
			}
			else if(this.override == 1)
			{
				this.override++;
			}
			else
			{
				this.override = -1;
			}
	    }
	}
}
