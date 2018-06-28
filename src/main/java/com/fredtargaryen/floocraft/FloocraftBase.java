package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.block.*;
import com.fredtargaryen.floocraft.client.gui.GuiHandler;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.item.ItemFlooSign;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.proxy.CommonProxy;
import com.fredtargaryen.floocraft.tileentity.TileEntityMirageFire;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.fredtargaryen.floocraft.DataReference.MODID;

@Mod(modid= MODID, name=DataReference.MODNAME, version=DataReference.VERSION)
@Mod.EventBusSubscriber
public class FloocraftBase
{
	/**
	 * The instance of your mod that Forge uses.
	 */
    @Mod.Instance(MODID)
    public static FloocraftBase instance;

    private static boolean mirageInstalled;

    //Config vars
    public static boolean villagersTeleport;
    public static boolean itemsTeleport;
    public static boolean miscMobsTeleport;
    
    /**
     * Declare all blocks here
     */
    public static Block blockFlooTorch;
    public static Block greenFlamesBusy;
    public static Block greenFlamesIdle;
    /**
     * Temporary green flames which replace any normal fire at the destination fireplace, so that players aren't
     * burnt on arrival. Disappear after 100 ticks.
     */
    public static Block greenFlamesTemp;
    public static Block blockFlooSign;
    public static Block floowerPot;

    /**
     * Declare all items here
     */
    public static Item itemFlooSign;
    private static Item iFlooTorch;
    private static Item iFloowerPot;

    public static Item floopowder1t;
    public static Item floopowder2t;
    public static Item floopowder4t;
    public static Item floopowder8t;
    public static Item floopowderc;

    /**
     * Declare sounds here
     */
    //When a fire makes contact with Floo Powder
    public static SoundEvent greened;
    //When a player teleports using a fireplace
    public static SoundEvent tp;
    //When a player is teleported by a Floo Torch
    public static SoundEvent flick;

    /**   
     * Says where the client and server 'proxy' code is loaded.
     */
    @SidedProxy(clientSide=DataReference.CLIENTPROXYPATH, serverSide=DataReference.SERVERPROXYPATH)
    public static CommonProxy proxy;
        
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //Making packets
        PacketHandler.init();

        //CONFIG SETUP
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        villagersTeleport = config.getBoolean("Villagers Teleport", "Teleportation", false, "If true, villagers who wander into Floo fires MAY teleport to a random fireplace. Never consumes Floo Powder");
        itemsTeleport = config.getBoolean("Items Teleport", "Teleportation", false, "If true, dropped items that touch Floo fires WILL teleport to a random fireplace. Never consumes Floo Powder");
        miscMobsTeleport = config.getBoolean("Misc. Mobs Teleport", "Teleportation", false, "As with villagers, but for Sheep, Cows, Spiders, Silverfish, Zombies etc. Never consumes Floo Powder");
        config.save();

        //Making blocks
    	blockFlooTorch = new BlockFlooTorch()
                .setUnlocalizedName("flootorch")
                .setRegistryName("flootorch")
    	        .setLightLevel(1.0F)
    	        .setCreativeTab(CreativeTabs.DECORATIONS);
    	
    	greenFlamesBusy = new GreenFlamesBusy()
                .setUnlocalizedName("greenflamesbusy")
                .setRegistryName("greenflamesbusy")
                .setLightLevel(1.0F);

        greenFlamesIdle = new GreenFlamesIdle()
                .setUnlocalizedName("greenflamesidle")
                .setRegistryName("greenflamesidle")
                .setLightLevel(0.875F);

        greenFlamesTemp = new GreenFlamesTemp()
                .setUnlocalizedName("greenflamesbusy")
                .setRegistryName("greenflamestemp")
                .setLightLevel(1.0F);
    	
    	blockFlooSign = new BlockFlooSign()
                .setRegistryName("blockfloosign");

        floowerPot = new BlockFloowerPot()
                .setUnlocalizedName("floowerpot")
                .setRegistryName("floowerpot")
                .setCreativeTab(CreativeTabs.MISC);

        iFlooTorch = new ItemBlock(blockFlooTorch)
                .setUnlocalizedName("flootorch")
                .setRegistryName("flootorch");

        //Making items
        iFloowerPot = new ItemBlock(floowerPot)
                .setUnlocalizedName("floowerpot")
                .setRegistryName("floowerpot");
    	
    	floopowder1t = new ItemFlooPowder((byte)1)
    	        .setMaxStackSize(64)
    	        .setUnlocalizedName("floopowder")
                .setRegistryName("floopowder_one")
    	        .setCreativeTab(CreativeTabs.MISC);

        floopowder2t = new ItemFlooPowder((byte)2)
                .setMaxStackSize(64)
                .setUnlocalizedName("floopowder")
                .setRegistryName("floopowder_two")
                .setCreativeTab(CreativeTabs.MISC);

        floopowder4t = new ItemFlooPowder((byte)4)
                .setMaxStackSize(64)
                .setUnlocalizedName("floopowder")
                .setRegistryName("floopowder_four")
                .setCreativeTab(CreativeTabs.MISC);

        floopowder8t = new ItemFlooPowder((byte)8)
                .setMaxStackSize(64)
                .setUnlocalizedName("floopowder")
                .setRegistryName("floopowder_eight")
                .setCreativeTab(CreativeTabs.MISC);

        floopowderc = new ItemFlooPowder((byte)9)
                .setMaxStackSize(64)
                .setUnlocalizedName("floopowder")
                .setRegistryName("floopowder_infinite")
                .setCreativeTab(CreativeTabs.MISC);

    	itemFlooSign = new ItemFlooSign()
                .setMaxStackSize(16)
                .setUnlocalizedName("itemfloosign")
                .setRegistryName("itemfloosign")
                .setCreativeTab(CreativeTabs.DECORATIONS);

        //Making sounds
        greened = new SoundEvent(new ResourceLocation(MODID, "greened")).setRegistryName("greened");
        tp = new SoundEvent(new ResourceLocation(MODID, "tp")).setRegistryName("tp");
        flick = new SoundEvent(new ResourceLocation(MODID, "flick")).setRegistryName("flick");

        //Registering sounds
        ForgeRegistries.SOUND_EVENTS.register(greened);
        ForgeRegistries.SOUND_EVENTS.register(tp);
        ForgeRegistries.SOUND_EVENTS.register(flick);

        //Registering Tile Entities
        GameRegistry.registerTileEntity(TileEntityFireplace.class, new ResourceLocation(MODID+":fireplaceTE"));
        GameRegistry.registerTileEntity(TileEntityFloowerPot.class, new ResourceLocation(MODID+":potTE"));
        GameRegistry.registerTileEntity(TileEntityMirageFire.class, new ResourceLocation(MODID+":greenLightTE"));

        proxy.registerTextureStitcher();
        proxy.registerRenderers();
    }
        
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        //Proxy registering
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        ResourceLocation peekerLocation = new ResourceLocation(DataReference.MODID+":peeker");
        //Last three params are for tracking: trackingRange, updateFrequency and sendsVelocityUpdates
        EntityRegistry.registerModEntity(peekerLocation, EntityPeeker.class, "peeker", 0, instance, 32, 1, false);
    	proxy.registerTickHandlers();
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        mirageInstalled = Loader.isModLoaded("mirage");
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> evt)
    {
        evt.getRegistry().registerAll(blockFlooSign, blockFlooTorch, greenFlamesBusy, greenFlamesIdle, greenFlamesTemp,
                floowerPot);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt)
    {
        evt.getRegistry().registerAll(iFloowerPot, iFlooTorch, floopowder1t, floopowder2t, floopowder4t, floopowder8t,
                floopowderc, itemFlooSign);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        proxy.registerModels();
    }

    public static boolean isMirageInstalled()
    {
        return mirageInstalled;
    }

    //Common entity access method
    public static Entity getEntityWithUUID(World world, UUID uuid) {
        if (world == null || uuid == null) return null;
        List<Entity> entities = world.loadedEntityList;
        Iterator<Entity> eIter = entities.iterator();
        while (eIter.hasNext()) {
            Entity nextEntity = eIter.next();
            if (nextEntity.getUniqueID().equals(uuid)) return nextEntity;
        }
        return null;
    }
}