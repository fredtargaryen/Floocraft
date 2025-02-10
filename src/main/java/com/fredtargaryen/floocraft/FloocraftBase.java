package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.blockentity.FlooSignBlockEntity;
import com.fredtargaryen.floocraft.client.Ticker;
import com.fredtargaryen.floocraft.client.gui.screens.inventory.FlooSignEditScreen;
import com.fredtargaryen.floocraft.client.gui.screens.teleport.TeleportScreen;
import com.fredtargaryen.floocraft.client.renderer.blockentity.FlooSignRenderer;
import com.fredtargaryen.floocraft.config.ClientConfig;
import com.fredtargaryen.floocraft.config.CommonConfig;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.FireplaceListResponseMessage;
import com.fredtargaryen.floocraft.network.messages.FlooSignNameResponseMessage;
import com.fredtargaryen.floocraft.network.messages.OpenFlooSignEditScreenMessage;
import com.fredtargaryen.floocraft.network.messages.TeleportFlashMessage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

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
 * TODO Messages only seem to execute once or twice
 * TODO Teleport screen place list
 *      TODO Refresh doesn't really work
 *      TODO Disable teleporting to own location and tell user they are there
 * TODO Correct NBT tag type in FloocraftLevelData? (line 39)
 * TODO Does FlooSignBlockEntit#signText need to be final?
 * TODO Can placeList be an array of Strings or does it have to be Objects?
 * TODO Can place signs on other signs (allow this only if vanilla signs can do it)
 *      TODO Also copy sign collision
 * TODO Occasionally signs place without opening the GUI
 * TODO Occasional indefinite saving world screen. Might be due to debug mode?
 * TODO Floower Pot
 * TODO Peeking
 * TODO Fireplace design reqs change
 * TODO Sign text filtering when sign text is on screen
 */
// The value here should match an entry in the META-INF/mods.toml file
@Mod(value = DataReference.MODID)
public class FloocraftBase {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public FloocraftBase() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        FloocraftBlocks.register(modEventBus);
        FloocraftItems.register(modEventBus);
        FloocraftBlockEntityTypes.register(modEventBus);
        FloocraftCreativeTabs.register(modEventBus);
        FloocraftParticleTypes.register(modEventBus);
        FloocraftSounds.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        event.enqueueWork(MessageHandler::init);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = DataReference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        /**
         * Tick counter that controls the flash effect after teleporting
         */
        public static Ticker flashTicker;

        /**
         * Floo torch teleport cooldown
         */
        public static Ticker torchTicker;

        public static void handleMessage(FireplaceListResponseMessage message) {
            Screen s = Minecraft.getInstance().screen;
            if (s instanceof TeleportScreen) {
                ((TeleportScreen) s).receiveFireplaceList(message);
            }
        }

        public static void handleMessage(FlooSignNameResponseMessage message) {
            Screen s = Minecraft.getInstance().screen;
            if (s instanceof FlooSignEditScreen) {
                ((FlooSignEditScreen) s).handleResponse(message.answer);
            }
        }

        public static void handleMessage(OpenFlooSignEditScreenMessage message) {
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            assert level != null;
            BlockEntity blockEntity = level.getBlockEntity(message.signPos);
            assert blockEntity != null;
            if (blockEntity.getType() == FloocraftBlockEntityTypes.FLOO_SIGN.get()) {
                minecraft.setScreen(new FlooSignEditScreen((FlooSignBlockEntity) blockEntity));
            }
        }

        public static void handleMessage(TeleportFlashMessage message) {

        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

            flashTicker = new Ticker((byte) 90);
            torchTicker = new Ticker((byte) 20);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(FlooSignRenderer.FLOO_SIGN_MODEL_LOCATION, FlooSignRenderer::createSignLayer);
        }

        @SubscribeEvent
        public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(FloocraftBlockEntityTypes.FLOO_SIGN.get(), FlooSignRenderer::new);
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
