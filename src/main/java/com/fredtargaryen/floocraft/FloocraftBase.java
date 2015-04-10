/**
 * ===NEXT UP===
 * Unusable fire bug
 * Peeking
 */

package com.fredtargaryen.floocraft;
import com.fredtargaryen.floocraft.block.*;
import com.fredtargaryen.floocraft.client.gui.GuiHandler;
import com.fredtargaryen.floocraft.client.renderer.FireRenderer;
import com.fredtargaryen.floocraft.item.*;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.proxy.CommonProxy;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
    public static Block flooTorch;
    public static Block greenFlamesBusyLower;
    public static Block greenFlamesIdle;
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
        PacketHandler.init();
        int fireRenderID = RenderingRegistry.getNextAvailableRenderId();

    	flooTorch = new BlockFlooTorch()
    	.setLightLevel(1.0F)
    	.setCreativeTab(CreativeTabs.tabDecorations);
    	
    	greenFlamesBusyLower = new GreenFlamesBusyLower(fireRenderID)
    	.setLightLevel(1.0F);
    	
    	greenFlamesIdle = new GreenFlamesIdle()
    	.setLightLevel(0.875F);

        greenFlamesTemp = new GreenFlamesIdleTemp()
        .setLightLevel(0.875F);
    	
    	blockFlooSign = new BlockFlooSign();

        floowerPot = new BlockFloowerPot()
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

        //Register blocks with GameRegistry
        GameRegistry.registerBlock(blockFlooSign, "blockfloosign");
        GameRegistry.registerBlock(flooTorch, "flootorch");
        GameRegistry.registerBlock(greenFlamesBusyLower, "greenflamesbusylower");
        GameRegistry.registerBlock(greenFlamesIdle, "greenflamesidle");
        GameRegistry.registerBlock(greenFlamesTemp, "greenflamesidletemp");
        GameRegistry.registerBlock(floowerPot, "floowerpot");

        //Register items with GameRegistry
        GameRegistry.registerItem(floopowder1t, "floopowder1");
        GameRegistry.registerItem(floopowder2t, "floopowder2");
        GameRegistry.registerItem(floopowder4t, "floopowder4");
        GameRegistry.registerItem(floopowder8t, "floopowder8");
        GameRegistry.registerItem(floopowderc, "floopowder9");
        GameRegistry.registerItem(itemFlooSign, "itemfloosign");

        //Register (Tile) Entities with GameRegistry
        GameRegistry.registerTileEntity(TileEntityFireplace.class, "fireplaceTE");
        GameRegistry.registerTileEntity(TileEntityFloowerPot.class, "potTE");

        RenderingRegistry.registerBlockHandler(new FireRenderer(fireRenderID));
    }
        
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	//Add recipes with GameRegistry
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
    	GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.flooTorch,4),
    			new ItemStack(Items.stick), new ItemStack(FloocraftBase.floopowder1t));
        GameRegistry.addShapelessRecipe(new ItemStack(FloocraftBase.floowerPot),
                new ItemStack(Items.flower_pot), new ItemStack(FloocraftBase.floopowder1t));

        //More registering
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    	proxy.registerRenderers();
    	proxy.registerTickHandlers();
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}