package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.block.*;
import com.fredtargaryen.floocraft.client.gui.GuiHandler;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.item.ItemFlooSign;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.proxy.CommonProxy;
import com.fredtargaryen.floocraft.tileentity.TileEntityAlbedoFire;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Mod(modid=DataReference.MODID, name=DataReference.MODNAME, version=DataReference.VERSION)
public class FloocraftBase
{
	/**
	 * The instance of your mod that Forge uses.
	 */
    @Mod.Instance(DataReference.MODID)
    public static FloocraftBase instance;

    private static boolean albedoInstalled;

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
    public static Item iFlooTorch;
    public static Item iFloowerPot;

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
        //Makes all packets to be used
        PacketHandler.init();

        //CONFIG SETUP
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        villagersTeleport = config.getBoolean("Villagers Teleport", "Teleportation", false, "If true, villagers who wander into Floo fires MAY teleport to a random fireplace. Never consumes Floo Powder");
        itemsTeleport = config.getBoolean("Items Teleport", "Teleportation", false, "If true, dropped items that touch Floo fires WILL teleport to a random fireplace. Never consumes Floo Powder");
        miscMobsTeleport = config.getBoolean("Misc. Mobs Teleport", "Teleportation", false, "As with villagers, but for Sheep, Cows, Spiders, Silverfish, Zombies etc. Never consumes Floo Powder");
        config.save();

        //Makes all blocks and items to be used
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

        //Registering blocks
        GameRegistry.register(blockFlooSign);
        GameRegistry.register(blockFlooTorch);
        GameRegistry.register(greenFlamesBusy);
        GameRegistry.register(greenFlamesIdle);
        GameRegistry.register(greenFlamesTemp);
        GameRegistry.register(floowerPot);

        //Registering items
        GameRegistry.register(iFloowerPot);
        GameRegistry.register(iFlooTorch);
        GameRegistry.register(floopowder1t);
        GameRegistry.register(floopowder2t);
        GameRegistry.register(floopowder4t);
        GameRegistry.register(floopowder8t);
        GameRegistry.register(floopowderc);
        GameRegistry.register(itemFlooSign);

        //Registering Tile Entities
        GameRegistry.registerTileEntity(TileEntityFireplace.class, "fireplaceTE");
        GameRegistry.registerTileEntity(TileEntityFloowerPot.class, "potTE");
        GameRegistry.registerTileEntity(TileEntityAlbedoFire.class, "albfTE");

        //Registering sounds
        greened = GameRegistry.register(new SoundEvent(new ResourceLocation(DataReference.MODID, "greened")).setRegistryName("greened"));
        tp = GameRegistry.register(new SoundEvent(new ResourceLocation(DataReference.MODID, "tp")).setRegistryName("tp"));
        flick = GameRegistry.register(new SoundEvent(new ResourceLocation(DataReference.MODID, "flick")).setRegistryName("flick"));

        //Adding recipes
        //Infinite powder is creative only so no recipe
        GameRegistry.addShapelessRecipe(new ItemStack(floopowder1t,8),
                new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.GUNPOWDER));
        GameRegistry.addShapelessRecipe(new ItemStack(floopowder2t,8),
                new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.GUNPOWDER));
        GameRegistry.addShapelessRecipe(new ItemStack(floopowder4t,8),
                new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.ENDER_PEARL),
                new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.GUNPOWDER));
        GameRegistry.addShapelessRecipe(new ItemStack(floopowder8t,8),
                new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.ENDER_PEARL),
                new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.ENDER_PEARL),
                new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.GUNPOWDER));

        GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.itemFlooSign,1),
                new ItemStack(Items.SIGN), new ItemStack(floopowder1t,8));
        GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.blockFlooTorch,4),
                new ItemStack(Items.STICK), new ItemStack(FloocraftBase.floopowder1t));
        GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.floowerPot),
                new ItemStack(Items.FLOWER_POT), new ItemStack(FloocraftBase.floopowder1t));
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
        proxy.registerModels();
    	proxy.registerTickHandlers();
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        albedoInstalled = Loader.isModLoaded("albedo");
    }

    public static boolean isAlbedoInstalled()
    {
        return albedoInstalled;
    }

    //Common entity access method
    public static Entity getEntityWithUUID(World world, UUID uuid) {
        if(world == null || uuid == null) return null;
        List<Entity> entities = world.loadedEntityList;
        Iterator<Entity> eIter = entities.iterator();
        while(eIter.hasNext()) {
            Entity nextEntity = eIter.next();
            if(nextEntity.getUniqueID().equals(uuid)) return nextEntity;
        }
        return null;
    }
}