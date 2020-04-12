package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.Flash;
import com.fredtargaryen.floocraft.client.gui.FlooSignScreen;
import com.fredtargaryen.floocraft.client.gui.FloowerPotScreen;
import com.fredtargaryen.floocraft.client.gui.TeleportScreen;
import com.fredtargaryen.floocraft.client.renderer.RenderPeekerFactory;
import com.fredtargaryen.floocraft.client.ticker.OverrideTicker;
import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.fredtargaryen.floocraft.network.messages.*;
import com.fredtargaryen.floocraft.tileentity.renderer.TileEntityFlooSignRenderer;
import com.fredtargaryen.floocraft.tileentity.renderer.TileEntityPotRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy implements IProxy {
    public OverrideTicker overrideTicker;
    public Flash flash;

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
        ClientRegistry.bindTileEntityRenderer(FloocraftBase.FIREPLACE_TYPE, TileEntityFlooSignRenderer::new);
        ClientRegistry.bindTileEntityRenderer(FloocraftBase.POT_TYPE, TileEntityPotRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FloocraftBase.PEEKER_TYPE, new RenderPeekerFactory());
    }

    @Override
    public void registerTickHandlers() {
        //Tickers now register and unregister themselves when necessary, improving performance very slightly
        this.overrideTicker = new OverrideTicker();
        this.flash = new Flash();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setUUIDs(MessagePlayerID message) {
        PeekerEntity ep = (PeekerEntity) FloocraftBase.proxy.getEntityWithUUID(Minecraft.getInstance().world, message.peekerUUID);
        ep.setPlayerUUID(message.playerUUID);
    }

    @Override
    public void setupRenderTypes()
    {
        RenderTypeLookup.setRenderLayer(FloocraftBase.GREEN_FLAMES_BUSY, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FloocraftBase.GREEN_FLAMES_IDLE, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FloocraftBase.GREEN_FLAMES_TEMP, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(FloocraftBase.BLOCK_FLOO_TORCH, RenderType.getCutoutMipped());
    }

    @Override
    public Entity getEntityWithUUID(World world, UUID uuid) {
        if(world != null && uuid != null) {
            Iterator<Entity> iterator = ((ClientWorld) world).getAllEntities().iterator();
            while (iterator.hasNext()) {
                Entity next = iterator.next();
                if (next.getUniqueID().equals(uuid)) return next;
            }
        }
        return null;
    }

    /**
     * For texture stitching
     */
    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre tse)
    {
        if(tse.getMap().getTextureLocation().equals((Atlases.SIGN_ATLAS)))
        {
            tse.addSprite(DataReference.SIGN_TEX_LOC);
        }
    }
}
