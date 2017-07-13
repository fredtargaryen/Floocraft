package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.block.*;
import com.fredtargaryen.floocraft.client.gui.GuiHandler;
import com.fredtargaryen.floocraft.entity.TextureStitcherBreathFX;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.item.ItemFlooSign;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.proxy.CommonProxy;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid=DataReference.MODID, name=DataReference.MODNAME, version=DataReference.VERSION)
public class FloocraftBase
{
	/**
	 * The instance of your mod that Forge uses.
	 */
    @Mod.Instance(DataReference.MODID)
    public static FloocraftBase instance;
    
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
        //Makes all packets to be used
        PacketHandler.init();

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

        greened = new SoundEvent(new ResourceLocation(DataReference.MODID, "greened")).setRegistryName("greened");
        tp = new SoundEvent(new ResourceLocation(DataReference.MODID, "tp")).setRegistryName("tp");
        flick = new SoundEvent(new ResourceLocation(DataReference.MODID, "flick")).setRegistryName("flick");

        //Register Floo Torch flame texture
        MinecraftForge.EVENT_BUS.register(new TextureStitcherBreathFX());

        //Registering blocks
        ForgeRegistries.BLOCKS.register(blockFlooSign);
        ForgeRegistries.BLOCKS.register(blockFlooTorch);
        ForgeRegistries.BLOCKS.register(greenFlamesBusy);
        ForgeRegistries.BLOCKS.register(greenFlamesIdle);
        ForgeRegistries.BLOCKS.register(greenFlamesTemp);
        ForgeRegistries.BLOCKS.register(floowerPot);

        //Registering items
        ForgeRegistries.ITEMS.register(iFloowerPot);
        ForgeRegistries.ITEMS.register(iFlooTorch);
        ForgeRegistries.ITEMS.register(floopowder1t);
        ForgeRegistries.ITEMS.register(floopowder2t);
        ForgeRegistries.ITEMS.register(floopowder4t);
        ForgeRegistries.ITEMS.register(floopowder8t);
        ForgeRegistries.ITEMS.register(floopowderc);
        ForgeRegistries.ITEMS.register(itemFlooSign);

        //Registering Tile Entities
        GameRegistry.registerTileEntity(TileEntityFireplace.class, "fireplaceTE");
        GameRegistry.registerTileEntity(TileEntityFloowerPot.class, "potTE");

        //Registering sounds
        ForgeRegistries.SOUND_EVENTS.register(greened);
        ForgeRegistries.SOUND_EVENTS.register(tp);
        ForgeRegistries.SOUND_EVENTS.register(flick);
    }
        
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        //Proxy registering
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        proxy.registerRenderers();
        proxy.registerModels();
    	proxy.registerTickHandlers();
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}