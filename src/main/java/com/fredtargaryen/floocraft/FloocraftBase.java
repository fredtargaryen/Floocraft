package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.block.*;
import com.fredtargaryen.floocraft.client.particle.GreenFlameParticle;
import com.fredtargaryen.floocraft.command.CommandsBase;
import com.fredtargaryen.floocraft.config.Config;
import com.fredtargaryen.floocraft.entity.PeekerEntity;
import com.fredtargaryen.floocraft.inventory.container.FloowerPotContainer;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.item.ItemFlooSign;
import com.fredtargaryen.floocraft.item.ItemFlooTorch;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import com.fredtargaryen.floocraft.proxy.IProxy;
import com.fredtargaryen.floocraft.proxy.ServerProxy;
import com.fredtargaryen.floocraft.tileentity.FireplaceTileEntity;
import com.fredtargaryen.floocraft.tileentity.FloowerPotTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = DataReference.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(DataReference.MODID)
public class FloocraftBase {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    
    //Declare all blocks here
    @ObjectHolder("flootorch")
    public static Block BLOCK_FLOO_TORCH;

    @ObjectHolder("greenflamesbusy")
    public static Block GREEN_FLAMES_BUSY;
    @ObjectHolder("greenflamesidle")
    public static Block GREEN_FLAMES_IDLE;
    /**
     * Temporary green flames which replace any normal fire at the destination fireplace, so that players aren't
     * burnt on arrival. Disappear after 100 ticks.
     */
    @ObjectHolder("greenflamestemp")
    public static Block GREEN_FLAMES_TEMP;

    //Sadly they have to be separate blocks
    @ObjectHolder("magentaflamesbusy")
    public static Block MAGENTA_FLAMES_BUSY;
    @ObjectHolder("magentaflamesidle")
    public static Block MAGENTA_FLAMES_IDLE;
    @ObjectHolder("magentaflamestemp")
    public static Block MAGENTA_FLAMES_TEMP;

    @ObjectHolder("floosign")
    public static Block BLOCK_FLOO_SIGN;
    @ObjectHolder("floowerpot")
    public static Block FLOOWER_POT;

    //Declare all items here
    @ObjectHolder("floosign")
    public static Item ITEM_FLOO_SIGN;
    @ObjectHolder("flootorch")
    public static Item ITEM_FLOO_TORCH;
    @ObjectHolder("floowerpot")
    public static Item ITEM_FLOOWER_POT;
    @ObjectHolder("floopowder_one")
    public static Item ITEM_FLOO_POWDER_1;
    @ObjectHolder("floopowder_two")
    public static Item ITEM_FLOO_POWDER_2;
    @ObjectHolder("floopowder_four")
    public static Item ITEM_FLOO_POWDER_4;
    @ObjectHolder("floopowder_eight")
    public static Item ITEM_FLOO_POWDER_8;
    @ObjectHolder("floopowder_infinite")
    public static Item ITEM_FLOO_POWDER_INFINITE;

    //Declare containers here
    @ObjectHolder("pot")
    public static ContainerType<FloowerPotContainer> POT_CONTAINER_TYPE;

    //Declare sounds here
    /**
     * When a fire makes contact with Floo Powder
     */
    @ObjectHolder("greened")
    public static SoundEvent GREENED;
    /**
     * When a player teleports using a fireplace
     */
    @ObjectHolder("tp")
    public static SoundEvent TP;
    /**
     * When a player is teleported by a Floo Torch
     */
    @ObjectHolder("flick")
    public static SoundEvent FLICK;

    //Declare EntityTypes here
    @ObjectHolder("peeker")
    public static EntityType<PeekerEntity> PEEKER_TYPE;

    //Declare ParticleTypes here
    @ObjectHolder("greenflame")
    public static BasicParticleType GREEN_FLAME;

    //Declare TileEntityTypes here
    @ObjectHolder("fireplace")
    public static TileEntityType<FireplaceTileEntity> FIREPLACE_TYPE;
    @ObjectHolder("pot")
    public static TileEntityType<FloowerPotTileEntity> POT_TYPE;

    /**   
     * Says where the client and server 'proxy' code is loaded.
     */
    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public FloocraftBase() {
        //Register the config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG_SPEC);

        //Event bus
        IEventBus loadingBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        loadingBus.addListener(this::postRegistration);
        loadingBus.addListener(this::clientSetup);

        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Load the config
        Config.loadConfig(FMLPaths.CONFIGDIR.get().resolve(DataReference.MODID + ".toml"));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt) {
        evt.getRegistry().registerAll(
                new FlooTorchBlock()
                        .setRegistryName("flootorch"),
                new FlooFlamesBusy()
                        .setRegistryName("greenflamesbusy"),
                new FlooFlamesIdle()
                        .setRegistryName("greenflamesidle"),
                new FlooFlamesTemp()
                        .setRegistryName("greenflamestemp"),
                new FlooFlamesBusy()
                        .setRegistryName("magentaflamesbusy"),
                new FlooFlamesIdle()
                        .setRegistryName("magentaflamesidle"),
                new FlooFlamesTemp()
                        .setRegistryName("magentaflamestemp"),
                new FlooSignBlock()
                        .setRegistryName("floosign"),
                new BlockFloowerPot()
                        .setRegistryName("floowerpot"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt) {
        evt.getRegistry().registerAll(
                new ItemFlooTorch()
                        .setRegistryName("flootorch"),
                new BlockItem(FLOOWER_POT, new Item.Properties().group(ItemGroup.MISC))
                        .setRegistryName("floowerpot"),
                new ItemFlooPowder((byte)1)
                        .setRegistryName("floopowder_one"),
                new ItemFlooPowder((byte)2)
                        .setRegistryName("floopowder_two"),
                new ItemFlooPowder((byte)4)
                        .setRegistryName("floopowder_four"),
                new ItemFlooPowder((byte)8)
                        .setRegistryName("floopowder_eight"),
                new ItemFlooPowder((byte)9)
                        .setRegistryName("floopowder_infinite"),
                new ItemFlooSign(new Item.Properties().group(ItemGroup.DECORATIONS).maxStackSize(16))
                        .setRegistryName("floosign"));
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new FloowerPotContainer(windowId, inv, inv.player.world, data.readBlockPos())).setRegistryName("pot"));
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                EntityType.Builder.create((type, world) -> new PeekerEntity(world), EntityClassification.MISC)
                        .setTrackingRange(32)
                        .setUpdateInterval(10)
                        .setShouldReceiveVelocityUpdates(false)
                        .immuneToFire()
                        .size(0.5F, 0.5F)
                        .setCustomClientFactory((spawnEntity, world) -> new PeekerEntity(world))
                        .build(DataReference.MODID)
                        .setRegistryName("peeker")
        );
    }

    @SubscribeEvent
    public static void registerParticleTypes(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().register(
                new BasicParticleType(false).setRegistryName("greenflame")
        );
    }

    @SubscribeEvent
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(FloocraftBase.GREEN_FLAME, GreenFlameParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
            new SoundEvent(new ResourceLocation(DataReference.MODID, "greened")).setRegistryName("greened"),
            new SoundEvent(new ResourceLocation(DataReference.MODID, "tp")).setRegistryName("tp"),
            new SoundEvent(new ResourceLocation(DataReference.MODID, "flick")).setRegistryName("flick"));
    }

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(FireplaceTileEntity::new, FloocraftBase.BLOCK_FLOO_SIGN)
                        .build(null)
                        .setRegistryName("fireplace"),
                TileEntityType.Builder.create(FloowerPotTileEntity::new, FloocraftBase.FLOOWER_POT)
                        .build(null)
                        .setRegistryName("pot"));
    }

    /**
     * Register the mod's commands.
     */
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        CommandsBase.registerCommands(event.getDispatcher());
    }

    /**
     * Called after all registry events. Runs in parallel with other SetupEvent handlers.
     * @param event
     */
    public void postRegistration(FMLCommonSetupEvent event) {
        MessageHandler.init();
        proxy.registerTickHandlers();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        proxy.registerGUIs();
        proxy.registerRenderers();
        proxy.setupRenderTypes();
    }

    ////////////////////////
    //FOR THE MODID CHANGE//
    ////////////////////////
    @SubscribeEvent
    public static void handleMissingMappings(RegistryEvent.MissingMappings evt) {
        String fullName = evt.getName().toString();
        if(fullName.equals("minecraft:blocks")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if(trueMapping.key.getNamespace().equals("ftfloocraft")) {
                    switch (trueMapping.key.getPath()) {
                        case "flootorch":
                            trueMapping.remap(BLOCK_FLOO_TORCH);
                            break;
                        case "greenflamesbusy":
                            trueMapping.remap(GREEN_FLAMES_BUSY);
                            break;
                        case "greenflamesidle":
                            trueMapping.remap(GREEN_FLAMES_IDLE);
                            break;
                        case "greenflamestemp":
                            trueMapping.remap(GREEN_FLAMES_TEMP);
                            break;
                        case "blockfloosign":
                            trueMapping.remap(BLOCK_FLOO_SIGN);
                            break;
                        case "floowerpot":
                            trueMapping.remap(FLOOWER_POT);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        else if(fullName.equals("minecraft:items")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if (trueMapping.key.getNamespace().equals("ftfloocraft")) {
                    switch (trueMapping.key.getPath()) {
                        case "flootorch":
                            trueMapping.remap(ITEM_FLOO_TORCH);
                            break;
                        case "floowerpot":
                            trueMapping.remap(ITEM_FLOOWER_POT);
                            break;
                        case "floopowder_one":
                            trueMapping.remap(ITEM_FLOO_POWDER_1);
                            break;
                        case "floopowder_two":
                            trueMapping.remap(ITEM_FLOO_POWDER_2);
                            break;
                        case "floopowder_four":
                            trueMapping.remap(ITEM_FLOO_POWDER_4);
                            break;
                        case "floopowder_eight":
                            trueMapping.remap(ITEM_FLOO_POWDER_8);
                            break;
                        case "floopowder_infinite":
                            trueMapping.remap(ITEM_FLOO_POWDER_INFINITE);
                            break;
                        case "itemfloosign":
                            trueMapping.remap(ITEM_FLOO_SIGN);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        else if(fullName.equals("minecraft:entities")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if (trueMapping.key.getNamespace().equals("ftfloocraft")) {
                    switch (trueMapping.key.getPath()) {
                        case "peeker":
                            trueMapping.remap(ForgeRegistries.ENTITIES.getValue(PEEKER_TYPE.getRegistryName()));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        else if(fullName.equals("minecraft:soundevents")) {
            for(Object mapping : evt.getAllMappings()) {
                RegistryEvent.MissingMappings.Mapping trueMapping = (RegistryEvent.MissingMappings.Mapping) mapping;
                if (trueMapping.key.getNamespace().equals("ftfloocraft")) {
                    switch (trueMapping.key.getPath()) {
                        case "tp":
                            trueMapping.remap(TP);
                            break;
                        case "flick":
                            trueMapping.remap(FLICK);
                            break;
                        case "greened":
                            trueMapping.remap(GREENED);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    //////////////////
    //LOGGER METHODS//
    //////////////////
    public static void info(String message) { LOGGER.info(message); }
    public static void warn(String message) {
        LOGGER.warn(message);
    }
}