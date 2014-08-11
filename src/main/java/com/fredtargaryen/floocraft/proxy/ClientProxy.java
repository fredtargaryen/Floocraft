package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.client.ticker.*;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFlooSignRenderer;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy
{
    public OverrideTicker overrideTicker;
    public TeleportTicker teleportTicker;

    @Override
    public void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFireplace.class, new TileEntityFlooSignRenderer());
    }

    @Override
    public void registerTickHandlers()
    {
        this.overrideTicker = new OverrideTicker();
        this.teleportTicker = new TeleportTicker();
        FMLCommonHandler.instance().bus().register(this.overrideTicker);
        FMLCommonHandler.instance().bus().register(this.teleportTicker);
    }
}
