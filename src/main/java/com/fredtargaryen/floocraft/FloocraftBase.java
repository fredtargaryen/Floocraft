/**
 * ===NEXT UP===
 * ConcurrentModificationException when teleporting (check)
 * HOW TO TORCH FLAMES?!?!???!?!?!?!?!?!??!??!?!?!!??!?!!!?!?!?!!?
 */
package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.block.*;
import com.fredtargaryen.floocraft.client.gui.GuiHandler;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.item.ItemFlooSign;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.proxy.CommonProxy;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
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
    //Temporary green flames which "usher you out" of the new fireplace. Disappear after 100 ticks.
    //Also used to check if a fireplace is valid at that time - if using it for this purpose, make
    //sure to immediately set it back to whatever block it was before.
    public static Block greenFlamesTemp;
    public static Block blockFlooSign;
    public static Block floowerPot;
    /**
     * Declare all items here
     */
    public static Item floopowder1t;
    public static Item floopowder2t;
    public static Item floopowder4t;
    public static Item floopowder8t;
    public static Item floopowderc;
    public static Item itemFlooSign;
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
    	        .setLightLevel(1.0F)
    	        .setCreativeTab(CreativeTabs.tabDecorations);
    	
    	greenFlamesBusy = new GreenFlamesBusy()
                .setUnlocalizedName("greenflamesbusy")
                .setLightLevel(1.0F);

        greenFlamesIdle = new GreenFlamesIdle()
                .setUnlocalizedName("greenflamesidle")
                .setLightLevel(0.875F);

        greenFlamesTemp = new GreenFlamesTemp()
                .setUnlocalizedName("greenflamesbusy")
                .setLightLevel(1.0F);
    	
    	blockFlooSign = new BlockFlooSign();

        floowerPot = new BlockFloowerPot()
                .setUnlocalizedName("floowerpot")
                .setCreativeTab(CreativeTabs.tabMisc);
    	
    	floopowder1t = new ItemFlooPowder((byte)1)
    	        .setMaxStackSize(64)
    	        .setUnlocalizedName("floopowder")
    	        .setCreativeTab(CreativeTabs.tabMisc);

        floopowder2t = new ItemFlooPowder((byte)2)
                .setMaxStackSize(64)
                .setUnlocalizedName("floopowder")
                .setCreativeTab(CreativeTabs.tabMisc);

        floopowder4t = new ItemFlooPowder((byte)4)
                .setMaxStackSize(64)
                .setUnlocalizedName("floopowder")
                .setCreativeTab(CreativeTabs.tabMisc);

        floopowder8t = new ItemFlooPowder((byte)8)
                .setMaxStackSize(64)
                .setUnlocalizedName("floopowder")
                .setCreativeTab(CreativeTabs.tabMisc);

        floopowderc = new ItemFlooPowder((byte)9)
                .setMaxStackSize(64)
                .setUnlocalizedName("floopowder")
                .setCreativeTab(CreativeTabs.tabMisc);

    	itemFlooSign = new ItemFlooSign()
                .setMaxStackSize(16)
                .setUnlocalizedName("itemfloosign")
                .setCreativeTab(CreativeTabs.tabDecorations);

        //Registering blocks
        GameRegistry.registerBlock(blockFlooSign, "blockfloosign");
        GameRegistry.registerBlock(blockFlooTorch, "flootorch");
        GameRegistry.registerBlock(greenFlamesBusy, "greenflamesbusy");
        GameRegistry.registerBlock(greenFlamesIdle, "greenflamesidle");
        GameRegistry.registerBlock(greenFlamesTemp, "greenflamestemp");
        GameRegistry.registerBlock(floowerPot, "floowerpot");

        //Registering items
        GameRegistry.registerItem(floopowder1t, "floopowder_one");
        GameRegistry.registerItem(floopowder2t, "floopowder_two");
        GameRegistry.registerItem(floopowder4t, "floopowder_four");
        GameRegistry.registerItem(floopowder8t, "floopowder_eight");
        GameRegistry.registerItem(floopowderc, "floopowder_infinite");
        GameRegistry.registerItem(itemFlooSign, "itemfloosign");

        //Registering Tile Entities
        GameRegistry.registerTileEntity(TileEntityFireplace.class, "fireplaceTE");
        GameRegistry.registerTileEntity(TileEntityFloowerPot.class, "potTE");

        //Adding recipes
        //Infinite powder is creative only so no recipe
        GameRegistry.addShapelessRecipe(new ItemStack(floopowder1t,8),
                new ItemStack(Items.ender_pearl), new ItemStack(Items.gunpowder));
        GameRegistry.addShapelessRecipe(new ItemStack(floopowder2t,8),
                new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_pearl), new ItemStack(Items.gunpowder));
        GameRegistry.addShapelessRecipe(new ItemStack(floopowder4t,8),
                new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_pearl),
                new ItemStack(Items.ender_pearl), new ItemStack(Items.gunpowder));
        GameRegistry.addShapelessRecipe(new ItemStack(floopowder8t,8),
                new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_pearl),
                new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_pearl),
                new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_pearl), new ItemStack(Items.gunpowder));

        GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.itemFlooSign,1),
                new ItemStack(Items.sign), new ItemStack(floopowder1t,8));
        GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.blockFlooTorch,4),
                new ItemStack(Items.stick), new ItemStack(FloocraftBase.floopowder1t));
        GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.floowerPot),
                new ItemStack(Items.flower_pot), new ItemStack(FloocraftBase.floopowder1t));
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