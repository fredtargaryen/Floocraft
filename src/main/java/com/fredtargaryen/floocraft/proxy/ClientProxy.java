package com.fredtargaryen.floocraft.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFlooSignRenderer;
import com.fredtargaryen.floocraft.client.gui.OverrideTicker;

public class ClientProxy extends CommonProxy
{
    public OverrideTicker ticker;

    @Override
    public void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFireplace.class, new TileEntityFlooSignRenderer());
    }

    @Override
    public void registerTickHandlers()
    {
        this.ticker = new OverrideTicker();
        FMLCommonHandler.instance().bus().register(this.ticker);
    }
}
