package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiFlash;
import com.fredtargaryen.floocraft.client.renderer.RenderPeekerFactory;
import com.fredtargaryen.floocraft.client.ticker.OverrideTicker;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.entity.TextureStitcherBreathFX;
import com.fredtargaryen.floocraft.network.messages.MessagePlayerID;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import com.fredtargaryen.floocraft.tileentity.renderer.TileEntityFlooSignRenderer;
import com.fredtargaryen.floocraft.tileentity.renderer.TileEntityPotRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy implements IProxy {
    public OverrideTicker overrideTicker;
    public GuiFlash flash;

    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFireplace.class, new TileEntityFlooSignRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFloowerPot.class, new TileEntityPotRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityPeeker.class, new RenderPeekerFactory());
    }

    @Override
    public void registerModels() {}

    @Override
    public void registerTickHandlers() {
        //Tickers now register and unregister themselves when necessary, improving performance very slightly
        this.overrideTicker = new OverrideTicker();
        this.flash = new GuiFlash(Minecraft.getInstance());
    }

    @Override
    public void registerTextureStitcher() {
        //Register Floo Torch flame texture
        MinecraftForge.EVENT_BUS.register(new TextureStitcherBreathFX());
    }

    @Override
    public void setUUIDs(MessagePlayerID message) {
        EntityPeeker ep = (EntityPeeker) FloocraftBase.getEntityWithUUID(Minecraft.getInstance().world, message.peekerUUID);
        ep.setPlayerUUID(message.playerUUID);
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }
}
