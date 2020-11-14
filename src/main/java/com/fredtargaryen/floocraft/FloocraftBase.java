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
import net.minecraft.entity.Entity;
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
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = DataReference.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FloocraftBase {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DataReference.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DataReference.MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, DataReference.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, DataReference.MODID);
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, DataReference.MODID);
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DataReference.MODID);
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DataReference.MODID);
    
    //Declare all blocks here
    public static final RegistryObject<Block> BLOCK_FLOO_TORCH = BLOCKS.register("flootorch", FlooTorchBlock::new);
    public static final RegistryObject<Block> GREEN_FLAMES_BUSY = BLOCKS.register("greenflamesbusy", FlooFlamesBusy::new);
    public static final RegistryObject<Block> GREEN_FLAMES_IDLE = BLOCKS.register("greenflamesidle", FlooFlamesIdle::new);
    /**
     * Temporary green flames which replace any normal fire at the destination fireplace, so that players aren't
     * burnt on arrival. Disappear after 100 ticks.
     */
    public static final RegistryObject<Block> GREEN_FLAMES_TEMP = BLOCKS.register("greenflamestemp", FlooFlamesTemp::new);

    //Sadly they have to be separate blocks
    public static final RegistryObject<Block> MAGENTA_FLAMES_BUSY = BLOCKS.register("magentaflamesbusy", FlooFlamesBusy::new);
    public static final RegistryObject<Block> MAGENTA_FLAMES_IDLE = BLOCKS.register("magentaflamesidle", FlooFlamesIdle::new);
    public static final RegistryObject<Block> MAGENTA_FLAMES_TEMP = BLOCKS.register("magentaflamestemp", FlooFlamesTemp::new);

    public static final RegistryObject<Block> BLOCK_FLOO_SIGN = BLOCKS.register("floosign", FlooSignBlock::new);
    public static final RegistryObject<Block> FLOOWER_POT = BLOCKS.register("floowerpot", BlockFloowerPot::new);

    //Declare all items here
    public static final RegistryObject<Item> ITEM_FLOO_SIGN = ITEMS.register("floosign", () -> new ItemFlooSign(new Item.Properties().group(ItemGroup.DECORATIONS).maxStackSize(16)));
    public static final RegistryObject<Item> ITEM_FLOO_TORCH = ITEMS.register("flootorch", ItemFlooTorch::new);
    public static final RegistryObject<Item> ITEM_FLOOWER_POT = ITEMS.register("floowerpot", () -> new BlockItem(FLOOWER_POT.get(), new Item.Properties().group(ItemGroup.MISC)));
    public static final RegistryObject<Item> ITEM_FLOO_POWDER_1 = ITEMS.register("floopowder_one", () -> new ItemFlooPowder((byte)1));
    public static final RegistryObject<Item> ITEM_FLOO_POWDER_2 = ITEMS.register("floopowder_two", () -> new ItemFlooPowder((byte)2));
    public static final RegistryObject<Item> ITEM_FLOO_POWDER_4 = ITEMS.register("floopowder_four", () -> new ItemFlooPowder((byte)4));
    public static final RegistryObject<Item> ITEM_FLOO_POWDER_8 = ITEMS.register("floopowder_eight", () -> new ItemFlooPowder((byte)8));
    public static final RegistryObject<Item> ITEM_FLOO_POWDER_INFINITE = ITEMS.register("floopowder_infinite", () -> new ItemFlooPowder((byte)9));;

    //Declare containers here
    public static final RegistryObject<ContainerType<FloowerPotContainer>> POT_CONTAINER_TYPE = CONTAINER_TYPES.register("pot", () -> IForgeContainerType.create((windowId, inv, data) -> new FloowerPotContainer(windowId, inv, inv.player.world, data.readBlockPos())));

    //Declare EntityTypes here
    public static final RegistryObject<EntityType<PeekerEntity>> PEEKER_TYPE = ENTITY_TYPES.register("peeker", () ->
            EntityType.Builder.<PeekerEntity>create((type, world) -> new PeekerEntity(world), EntityClassification.MISC)
                    .setTrackingRange(32)
                    .setUpdateInterval(10)
                    .setShouldReceiveVelocityUpdates(false)
                    .immuneToFire()
                    .size(0.5F, 0.5F)
                    .setCustomClientFactory((spawnEntity, world) -> new PeekerEntity(world))
                    .build(DataReference.MODID));

    //Declare ParticleTypes here
    public static final RegistryObject<BasicParticleType> GREEN_FLAME = PARTICLE_TYPES.register("greenflame", () -> new BasicParticleType(false));

    //Declare sounds here
    /**
     * When a fire makes contact with Floo Powder
     */
    public static final RegistryObject<SoundEvent> GREENED = SOUND_EVENTS.register("greened", () -> new SoundEvent(DataReference.GREENED_RL));
    /**
     * When a player teleports using a fireplace
     */
    public static final RegistryObject<SoundEvent> TP = SOUND_EVENTS.register("tp", () -> new SoundEvent(DataReference.TP_RL));
    /**
     * When a player is teleported by a Floo Torch
     */
    public static final RegistryObject<SoundEvent> FLICK = SOUND_EVENTS.register("flick", () -> new SoundEvent(DataReference.FLICK_RL));

    //Declare TileEntityTypes here
    public static final RegistryObject<TileEntityType<FireplaceTileEntity>> FIREPLACE_TYPE = TILE_ENTITY_TYPES.register("fireplace", () ->
            TileEntityType.Builder.create(FireplaceTileEntity::new, FloocraftBase.BLOCK_FLOO_SIGN.get())
                    .build(null));
    public static final RegistryObject<TileEntityType<FloowerPotTileEntity>> POT_TYPE = TILE_ENTITY_TYPES.register("pot", () ->
            TileEntityType.Builder.create(FloowerPotTileEntity::new, FloocraftBase.FLOOWER_POT.get())
                    .build(null));

    /**   
     * Says where the client and server 'proxy' code is loaded.
     */
    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public FloocraftBase() {
        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Event bus
        IEventBus loadingBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the setup method for modloading
        loadingBus.addListener(this::postRegistration);
        loadingBus.addListener(this::clientSetup);

        //Register all the RegistryObjects
        BLOCKS.register(loadingBus);
        ITEMS.register(loadingBus);
        CONTAINER_TYPES.register(loadingBus);
        ENTITY_TYPES.register(loadingBus);
        PARTICLE_TYPES.register(loadingBus);
        SOUND_EVENTS.register(loadingBus);
        TILE_ENTITY_TYPES.register(loadingBus);

        //Register the config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG_SPEC);

        //Load the config
        Config.loadConfig(Config.CLIENT_CONFIG_SPEC, FMLPaths.CONFIGDIR.get().resolve(DataReference.MODID + "-client.toml"));
        Config.loadConfig(Config.SERVER_CONFIG_SPEC, FMLPaths.CONFIGDIR.get().resolve(DataReference.MODID + ".toml"));
    }

    @SubscribeEvent
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(FloocraftBase.GREEN_FLAME.get(), GreenFlameParticle.Factory::new);
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
                            trueMapping.remap(BLOCK_FLOO_TORCH.get());
                            break;
                        case "greenflamesbusy":
                            trueMapping.remap(GREEN_FLAMES_BUSY.get());
                            break;
                        case "greenflamesidle":
                            trueMapping.remap(GREEN_FLAMES_IDLE.get());
                            break;
                        case "greenflamestemp":
                            trueMapping.remap(GREEN_FLAMES_TEMP.get());
                            break;
                        case "blockfloosign":
                            trueMapping.remap(BLOCK_FLOO_SIGN.get());
                            break;
                        case "floowerpot":
                            trueMapping.remap(FLOOWER_POT.get());
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
                            trueMapping.remap(ITEM_FLOO_TORCH.get());
                            break;
                        case "floowerpot":
                            trueMapping.remap(ITEM_FLOOWER_POT.get());
                            break;
                        case "floopowder_one":
                            trueMapping.remap(ITEM_FLOO_POWDER_1.get());
                            break;
                        case "floopowder_two":
                            trueMapping.remap(ITEM_FLOO_POWDER_2.get());
                            break;
                        case "floopowder_four":
                            trueMapping.remap(ITEM_FLOO_POWDER_4.get());
                            break;
                        case "floopowder_eight":
                            trueMapping.remap(ITEM_FLOO_POWDER_8.get());
                            break;
                        case "floopowder_infinite":
                            trueMapping.remap(ITEM_FLOO_POWDER_INFINITE.get());
                            break;
                        case "itemfloosign":
                            trueMapping.remap(ITEM_FLOO_SIGN.get());
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
                            trueMapping.remap(ForgeRegistries.ENTITIES.getValue(PEEKER_TYPE.get().getRegistryName()));
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
                            trueMapping.remap(TP.get());
                            break;
                        case "flick":
                            trueMapping.remap(FLICK.get());
                            break;
                        case "greened":
                            trueMapping.remap(GREENED.get());
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