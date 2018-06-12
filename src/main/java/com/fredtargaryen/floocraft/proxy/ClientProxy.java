package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiFlash;
import com.fredtargaryen.floocraft.client.ticker.OverrideTicker;
import com.fredtargaryen.floocraft.entity.TextureStitcherBreathFX;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import com.fredtargaryen.floocraft.tileentity.specialrenderer.TileEntityFlooSignRenderer;
import com.fredtargaryen.floocraft.tileentity.specialrenderer.TileEntityPotRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

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
        ModelLoader.setCustomModelResourceLocation(FloocraftBase.itemFlooSign, 0, new ModelResourceLocation(DataReference.MODID + ":itemfloosign", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FloocraftBase.blockFlooTorch), 0, new ModelResourceLocation(DataReference.MODID + ":flootorch", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(FloocraftBase.floowerPot), 0, new ModelResourceLocation(DataReference.MODID + ":floowerpot", "inventory"));
        ModelLoader.setCustomModelResourceLocation(FloocraftBase.floopowder1t, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_one", "inventory"));
        ModelLoader.setCustomModelResourceLocation(FloocraftBase.floopowder2t, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_two", "inventory"));
        ModelLoader.setCustomModelResourceLocation(FloocraftBase.floopowder4t, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_four", "inventory"));
        ModelLoader.setCustomModelResourceLocation(FloocraftBase.floopowder8t, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_eight", "inventory"));
        ModelLoader.setCustomModelResourceLocation(FloocraftBase.floopowderc, 0, new ModelResourceLocation(DataReference.MODID + ":floopowder_infinite", "inventory"));
    }

    @Override
    public void registerTickHandlers()
    {
        this.overrideTicker = new OverrideTicker();
        this.flash = new GuiFlash(Minecraft.getMinecraft());

        MinecraftForge.EVENT_BUS.register(this.overrideTicker);
        MinecraftForge.EVENT_BUS.register(this.flash);
    }

    @Override
    public void registerTextureStitcher()
    {
        //Register Floo Torch flame texture
        MinecraftForge.EVENT_BUS.register(new TextureStitcherBreathFX());
    }
}
