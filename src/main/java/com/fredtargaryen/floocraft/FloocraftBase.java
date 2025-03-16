package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import com.fredtargaryen.floocraft.client.Ticker;
import com.fredtargaryen.floocraft.client.gui.TeleportEffects;
import com.fredtargaryen.floocraft.client.gui.screens.inventory.FlooSignEditScreen;
import com.fredtargaryen.floocraft.client.gui.screens.inventory.FloowerPotScreen;
import com.fredtargaryen.floocraft.client.gui.screens.teleport.TeleportScreen;
import com.fredtargaryen.floocraft.client.particle.FlooTorchFlameParticle;
import com.fredtargaryen.floocraft.client.renderer.blockentity.FlooSignRenderer;
import com.fredtargaryen.floocraft.client.renderer.blockentity.FloowerPotRenderer;
import com.fredtargaryen.floocraft.client.renderer.entity.PeekerRenderer;
import com.fredtargaryen.floocraft.command.CommandsBase;
import com.fredtargaryen.floocraft.config.ClientConfig;
import com.fredtargaryen.floocraft.config.CommonConfig;
import com.fredtargaryen.floocraft.network.messages.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.UUID;

/**
 * Other updates aside from porting the mod:
 * * Added a 1-second cooldown between uses of Floo Torches
 * * Sped up Floo Torch teleporting
 * * Restores the ability to transform flames using dropped Floo Powder
 * * Reduced amount of memory used by Floo flames
 * * Generally prettified the code base - faster more elegant algorithms; more cohesive classes
 * * Removed the valid departure blocks tag - don't think anyone would have used it so a small price to pay for better code
 * * If you can teleport into a fireplace, write its name in "Floo green" instead of ordinary green
 * * Teleport screen now shows which fireplace you are in (if any)
 * * Remove some empty spaces from sign text
 * * Players nearby hear when you arrive in a Floo fire
 * * Drawing colour to screen rather than using textures for teleport flash
 * * Small tweaks to teleport flash speed and dizziness effect
 * * Allow a little longer to exit a Floo fire on arrival; increase config maximum time
 * * Change Floower Pot minimum fire conversion range to 0 so you can use it manually if you prefer
 * * Exclusive creative menu tab for Floocraft items
 * <p>
 * TODO Peeking
 *      TODO Peekers don't die...
 * TODO README update
 * TODO Fireplace design reqs change
 * TODO Sign text filtering when sign text is on screen
 * TODO Green glint on powder in pot?
 */
// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(value = DataReference.MODID)
public class FloocraftBase {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public FloocraftBase(IEventBus eventBus, ModContainer modContainer) {

        // Register the commonSetup method for modloading
        eventBus.addListener(this::commonSetup);

        FloocraftBlocks.register(eventBus);
        FloocraftItems.register(eventBus);
        FloocraftBlockEntityTypes.register(eventBus);
        FloocraftCreativeTabs.register(eventBus);
        FloocraftEntityTypes.register(eventBus);
        FloocraftMenuTypes.register(eventBus);
        FloocraftParticleTypes.register(eventBus);
        FloocraftSounds.register(eventBus);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that NeoForge can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    /**
     * Register the mod's commands.
     */
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        CommandsBase.registerCommands(event.getDispatcher());
    }

    @OnlyIn(Dist.CLIENT)
    @EventBusSubscriber(modid = DataReference.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        /**
         * Tick counter that controls the flash effect after teleporting
         */
        public static Ticker flashTicker;

        /**
         * Floo torch teleport cooldown
         */
        public static Ticker torchTicker;

        private static TeleportEffects teleportEffects;

        public static void handleMessage(FireplaceListResponseMessage message) {
            Screen s = Minecraft.getInstance().screen;
            if (s instanceof TeleportScreen) {
                ((TeleportScreen) s).receiveFireplaceList(message);
            }
        }

        public static void handleMessage(FlooSignNameResponseMessage message) {
            Screen s = Minecraft.getInstance().screen;
            if (s instanceof FlooSignEditScreen) {
                ((FlooSignEditScreen) s).handleResponse(message.answer());
            }
        }

        public static void handleMessage(OpenFlooSignEditScreenMessage message) {
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            if (level == null) return;
            BlockEntity blockEntity = level.getBlockEntity(message.getSignPos());
            if (blockEntity == null) return;
            if (blockEntity.getType() == FloocraftBlockEntityTypes.FLOO_SIGN.get()) {
                minecraft.setScreen(new FlooSignEditScreen((FlooSignBlockEntity) blockEntity));
            }
        }

        public static void handleMessage(PeekerInfoResponseMessage message) {
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            if (level == null) return;
            //PeekerEntity peeker = (PeekerEntity) getEntityWithUUID(level, new UUID(message.peekerMsb(), message.peekerLsb()));
            //if (peeker == null) return;
            //peeker.setPlayerUUID(new UUID(message.playerMsb(), message.playerLsb()));
        }

        public static void handleMessage(StartPeekResponseMessage message) {
            if (message.accepted()) {
                Screen s = Minecraft.getInstance().screen;
                if (s instanceof TeleportScreen) {
                    ((TeleportScreen) s).onStartPeek(message);
                }
            }
        }

        public static void handleMessage(TeleportResponseMessage message) {
            if (message.accepted()) teleportEffects.start(message.soul());
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

            flashTicker = new Ticker((byte) 90);
            torchTicker = new Ticker((byte) 20);
            teleportEffects = new TeleportEffects();
            DataReference.SIGN_MATERIAL = new Material(Sheets.SIGN_SHEET, DataReference.getResourceLocation("entity/signs/floo_sign"));
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(FlooSignRenderer.FLOO_SIGN_MODEL_LOCATION, FlooSignRenderer::createSignLayer);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(FloocraftBlockEntityTypes.FLOO_SIGN.get(), FlooSignRenderer::new);
            event.registerBlockEntityRenderer(FloocraftBlockEntityTypes.FLOOWER_POT.get(), FloowerPotRenderer::new);
            event.registerEntityRenderer(FloocraftEntityTypes.PEEKER.get(), PeekerRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(FloocraftMenuTypes.FLOOWER_POT.get(), FloowerPotScreen::new);
        }

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(FloocraftParticleTypes.FLOO_TORCH_FLAME.get(), FlooTorchFlameParticle.Provider::new);
        }
    }

    //////////////////
    //LOGGER METHODS//
    //////////////////
    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }
}
