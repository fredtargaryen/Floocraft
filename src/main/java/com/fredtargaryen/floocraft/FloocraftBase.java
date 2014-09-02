/**
 * Create "promotional material"
 * Test teleporting
 */
/**
 * ===IDEAS FOR WHEN NOT WIP===
 * Floower Pot
 * Support for brick blocks from other mods
 * Improved teleport effects
 * Sound!
 */

package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.block.*;
import com.fredtargaryen.floocraft.item.*;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.proxy.CommonProxy;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.client.gui.GuiFlash;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Mod(modid=DataReference.MODID, name=DataReference.MODNAME, version=DataReference.VERSION)
public class FloocraftBase
{
	/**
	 * The instance of your mod that Forge uses.
	 */
    @Mod.Instance(DataReference.MODID)
    public static FloocraftBase instance;
    
    //All vanilla blocks which can be used to build a fireplace. 
    public static List<Block> acceptedBlocks = new ArrayList<Block>();
    
    /**
     * Declare all blocks here
     */
    public static Block flooTorch;
    public static Block greenFlamesBusyLower;
    public static Block greenFlamesBusyHigher;
    public static Block greenFlamesIdle;
    public static Block greenFlamesTemp;
    public static Block blockFlooSign;
    /**
     * Declare all items here
     */
    public static Item floopowder;
    public static Item itemFlooSign;
    /**   
     * Says where the client and server 'proxy' code is loaded.
     */
    @SidedProxy(clientSide=DataReference.CLIENTPROXYPATH, serverSide=DataReference.SERVERPROXYPATH)
    public static CommonProxy proxy;
        
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PacketHandler.init();

    	flooTorch = new BlockFlooTorch()
    	.setBlockName("flootorch")
    	.setLightLevel(1.0F)
    	.setCreativeTab(CreativeTabs.tabDecorations);
    	
    	greenFlamesBusyLower = new GreenFlamesBusyLower()        
    	.setBlockName("greenflamesbusylower")
    	.setLightLevel(1.0F);
    	
    	greenFlamesBusyHigher = new GreenFlamesBusyHigher()
        .setBlockName("greenflamesbusyhigher")
        .setLightLevel(1.0F);
    	
    	greenFlamesIdle = new GreenFlamesIdle()
    	.setBlockName("greenflamesidle")
    	.setLightLevel(0.875F);

        greenFlamesTemp = new GreenFlamesIdleTemp(3)
        .setBlockName("greenflamesidletemp")
        .setLightLevel(0.875F);
    	
    	blockFlooSign = new BlockFlooSign()
    	.setBlockName("blockfloosign");
    	
    	floopowder = new ItemFlooPowder()
    	.setMaxStackSize(64)
    	.setUnlocalizedName("floopowder")
    	.setCreativeTab(CreativeTabs.tabMisc);

    	itemFlooSign = new ItemFlooSign()
    	.setMaxStackSize(16)
    	.setUnlocalizedName("itemfloosign")
    	.setCreativeTab(CreativeTabs.tabDecorations);
    	
    	acceptedBlocks.add(Blocks.stonebrick);
    	acceptedBlocks.add(Blocks.brick_block);
    	acceptedBlocks.add(Blocks.nether_brick);
    }
        
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	//Register blocks with GameRegistry
    	GameRegistry.registerBlock(blockFlooSign, "blockfloosign");
    	GameRegistry.registerBlock(flooTorch, "flootorch");
    	GameRegistry.registerBlock(greenFlamesBusyLower, "greenflamesbusylower");
    	GameRegistry.registerBlock(greenFlamesBusyHigher, "greenflamesbusyhigher");
    	GameRegistry.registerBlock(greenFlamesIdle, "greenflamesidle");
        GameRegistry.registerBlock(greenFlamesTemp, "greenflamesidletemp");
    	
    	GameRegistry.registerItem(floopowder, "floopowder");
    	GameRegistry.registerItem(itemFlooSign, "itemfloosign");

    	//Register (Tile) Entities with GameRegistry
    	GameRegistry.registerTileEntity(TileEntityFireplace.class, "fireplaceTE");

    	//Add recipes with GameRegistry
    	GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.floopowder,8),
    			new ItemStack(Items.ender_pearl), new ItemStack(Items.gunpowder));
    	GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.itemFlooSign,1),
    			new ItemStack(Items.sign), new ItemStack(FloocraftBase.floopowder));
    	GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.flooTorch,4),
    			new ItemStack(Items.stick), new ItemStack(FloocraftBase.floopowder));
    	proxy.registerRenderers();
    	proxy.registerTickHandlers();
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new GuiFlash(Minecraft.getMinecraft()));
    }
}