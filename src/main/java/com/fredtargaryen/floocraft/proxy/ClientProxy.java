package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiFlash;
import com.fredtargaryen.floocraft.client.ticker.*;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import com.fredtargaryen.floocraft.tileentity.specialrenderer.TileEntityFlooSignRenderer;
import com.fredtargaryen.floocraft.tileentity.specialrenderer.TileEntityPotRenderer;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;

public class ClientProxy extends CommonProxy
{
    public OverrideTicker overrideTicker;
    public GuiFlash flash;

    @Override
    public void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFireplace.class, new TileEntityFlooSignRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFloowerPot.class, new TileEntityPotRenderer());
    }

    @Override
    public void registerModels()
    {
        //Describes how some blocks should look in the inventory
        ItemModelMesher m = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        m.register(FloocraftBase.itemFlooSign, 0, new ModelResourceLocation(DataReference.MODID + ":itemfloosign", "inventory"));
        m.register(Item.getItemFromBlock(FloocraftBase.blockFlooTorch), 0, new ModelResourceLocation(DataReference.MODID + ":flootorch", "inventory"));
    }

    @Override
    public void registerTickHandlers()
    {
        this.overrideTicker = new OverrideTicker();
        this.flash = new GuiFlash(Minecraft.getMinecraft());

        FMLCommonHandler.instance().bus().register(this.overrideTicker);
        FMLCommonHandler.instance().bus().register(this.flash);
    }
}
