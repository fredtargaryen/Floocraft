/**
 * ===MOST URGENT PROBLEMS===
 * Teleporting to a fireplace with normal fire will set players on fire (duh!); should make it a bit safer
 * When a Floo Sign explodes, the wrong location is removed and an extra sign can occasionally drop
 * When no players are near busy flames, the busy flames stop ticking and the idle flames aren't set, afaik
 */
/**
 * ===COSMETICS & FANCY BITS===
 * Green Flames break in an interesting way. I could fix this, but I like it.
 * Teleporting interface isn't very elegant; hoping to replace scroll buttons with a smooth scrollbar
 * Villagers could be able to use the fireplaces as well...
 * Fireplaces can only be one block wide
 * Floo Signs should only add a location if they are connected to an appropriate block which is more than 2 blocks above ground
 */
/**
 * ===TELEPORTATION===
 * Teleportation is instant
 * The blocks the fireplace is made of has no bearing on teleport speed
 * Fireplaces with the same name can be created
 */
/**
 * ===LESS IMPORTANT THINGS FOR ME TO WORRY ABOUT===
 * Client: Using missing texture, unable to load ftfloocraft:textures/blocks/greenflamesbusyhigher_layer_1.png
 */

package com.fredtargaryen.floocraft;

import com.fredtargaryen.floocraft.block.*;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.item.ItemFlooSign;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.proxy.CommonProxy;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
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
    	// Stub Method
    }
}