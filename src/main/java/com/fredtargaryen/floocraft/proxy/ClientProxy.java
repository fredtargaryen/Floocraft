package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.FlashScreen;
import com.fredtargaryen.floocraft.client.gui.FlooSignScreen;
import com.fredtargaryen.floocraft.client.gui.FloowerPotScreen;
import com.fredtargaryen.floocraft.client.gui.TeleportScreen;
import com.fredtargaryen.floocraft.client.renderer.RenderPeekerFactory;
import com.fredtargaryen.floocraft.client.ticker.OverrideTicker;
import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.fredtargaryen.floocraft.network.messages.*;
import com.fredtargaryen.floocraft.tileentity.FireplaceTileEntity;
import com.fredtargaryen.floocraft.tileentity.FloowerPotTileEntity;
import com.fredtargaryen.floocraft.tileentity.renderer.TileEntityFlooSignRenderer;
import com.fredtargaryen.floocraft.tileentity.renderer.TileEntityPotRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy implements IProxy {
    public OverrideTicker overrideTicker;
    public FlashScreen flash;

    @Override
    public void onMessage(MessageApproval ma) {
        Screen s = Minecraft.getInstance().currentScreen;
        if(s instanceof FlooSignScreen) {
            ((FlooSignScreen) s).dealWithAnswer(ma.answer);
        }
    }

    @Override
    public void onMessage(MessageDoGreenFlash mdgf) { this.flash.start(); }

    @Override
    public void onMessage(MessageFireplaceList mfl) {
        Screen s = Minecraft.getInstance().currentScreen;
        if(s instanceof TeleportScreen) {
            ((TeleportScreen) s).onFireplaceList(mfl);
        }
    }

    @Override
    public void onMessage(MessageStartPeek msp) {
        Screen s = Minecraft.getInstance().currentScreen;
        if(s instanceof TeleportScreen) {
            ((TeleportScreen) s).onStartPeek(msp);
        }
    }

    @Override
    public void registerGUIs() {
        ScreenManager.registerFactory(FloocraftBase.POT_CONTAINER_TYPE, FloowerPotScreen::new);
    }

    @Override
    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(FireplaceTileEntity.class, new TileEntityFlooSignRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(FloowerPotTileEntity.class, new TileEntityPotRenderer());
        RenderingRegistry.registerEntityRenderingHandler(PeekerEntity.class, new RenderPeekerFactory());
    }

    @Override
    public void registerTickHandlers() {
        //Tickers now register and unregister themselves when necessary, improving performance very slightly
        this.overrideTicker = new OverrideTicker();
        this.flash = new FlashScreen(Minecraft.getInstance());
    }

    @Override
    public void setUUIDs(MessagePlayerID message) {
        PeekerEntity ep = (PeekerEntity) FloocraftBase.getEntityWithUUID(Minecraft.getInstance().world, message.peekerUUID);
        ep.setPlayerUUID(message.playerUUID);
    }
}
