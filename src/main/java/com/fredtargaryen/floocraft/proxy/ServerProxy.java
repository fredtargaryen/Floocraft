package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.network.messages.MessagePlayerID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ServerProxy implements IProxy
{
    public void registerRenderers(){}
    public void registerTickHandlers(){}
    public void registerTextureStitcher(){}
    public void setUUIDs(MessagePlayerID message){}
    public EntityPlayer getClientPlayer() {return null;}
    public World getClientWorld() {return null;}
}
