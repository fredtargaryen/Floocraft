package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiFlash;
import com.fredtargaryen.floocraft.client.renderer.RenderPeekerFactory;
import com.fredtargaryen.floocraft.client.ticker.OverrideTicker;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.entity.TextureStitcherBreathFX;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import com.fredtargaryen.floocraft.tileentity.specialrenderer.TileEntityFlooSignRenderer;
import com.fredtargaryen.floocraft.tileentity.specialrenderer.TileEntityPotRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    public OverrideTicker overrideTicker;
    public GuiFlash flash;

    @Override
    public void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFireplace.class, new TileEntityFlooSignRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFloowerPot.class, new TileEntityPotRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityPeeker.class, new RenderPeekerFactory());
        MinecraftForge.EVENT_BUS.register(new TextureStitcherBreathFX());
    }

    @Override
    public void registerModels()
    {
        //Describes how some blocks should look in the inventory
        ItemModelMesher m = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        m.register(FloocraftBase.itemFlooSign, 0, new ModelResourceLocation(DataReference.MODID + ":itemfloosign", "inventory"));
        m.register(Item.getItemFromBlock(FloocraftBase.blockFlooTorch), 0, new ModelResourceLocation(DataReference.MODID + ":flootorch", "inventory"));
        m.register(Item.getItemFromBlock(FloocraftBase.floowerPot), 0, new ModelResourceLocation(DataReference.MODID + ":floowerpot", "inventory"));
        m.register(FloocraftBase.floopowder1t, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_one", "inventory"));
        m.register(FloocraftBase.floopowder2t, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_two", "inventory"));
        m.register(FloocraftBase.floopowder4t, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_four", "inventory"));
        m.register(FloocraftBase.floopowder8t, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_eight", "inventory"));
        m.register(FloocraftBase.floopowderc, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_infinite", "inventory"));
    }

    @Override
    public void registerTickHandlers()
    {
        //Tickers now register and unregister themselves when necessary, improving performance very slightly
        this.overrideTicker = new OverrideTicker();
        this.flash = new GuiFlash(Minecraft.getMinecraft());
    }
}
