package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.network.messages.MessagePlayerID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IProxy
{
    void registerRenderers();

    void registerModels();

    void registerTickHandlers();

    void registerTextureStitcher();

    void setUUIDs(MessagePlayerID message);

    EntityPlayer getClientPlayer();

    World getClientWorld();
}
