package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.network.messages.MessagePlayerID;

public interface IProxy
{
    void registerRenderers();

    void registerModels();

    void registerTickHandlers();
	
	void setUUIDs(MessagePlayerID message);
}
