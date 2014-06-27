package com.fredtargaryen.floocraft.item;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiFlooSign;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ItemFlooSign extends Item
{
	
	public ItemFlooSign()
	{
		super();
		this.maxStackSize = 16;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister i)
	{
		this.itemIcon = i.registerIcon("ftfloocraft:"+this.getUnlocalizedName().substring(5));
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int side, float par8, float par9, float par10)
    {
		//par1 = item used
		//par2 = Player using item
		//par3 = world
		//par4 = x of block
		//par5 = y of block
		//par6 = z of block
		//par7 = side of block
		//par8, par9 and par10 are probably not important to me as they aren't used.
		if (side == 0 || side == 1)
        {
            return false;
        }
        else if (par3World.getBlock(x, y, z) != Blocks.brick_block)
        {
            return false;
        }
        else
        {
        	//Translates the block's xyz to the sign's xyz
        	if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }

            if (!par2EntityPlayer.canPlayerEdit(x, y, z, side, par1ItemStack))
            {
                return false;
            }
            else
            {
            	par3World.setBlock(x, y, z, FloocraftBase.blockFlooSign, side, 3);
            	--par1ItemStack.stackSize;
            	TileEntityFireplace fireplaceTE = (TileEntityFireplace)par3World.getTileEntity(x, y, z);
            	if (fireplaceTE != null)
            	{
            		fireplaceTE.func_145912_a(par2EntityPlayer);
            		if(par3World.isRemote)
            		{
            			this.dothesigneditguiscreen(par2EntityPlayer, fireplaceTE);
            		}
                }
                return true;
            }
        }
    }
	
	@SideOnly(Side.CLIENT)
	private void dothesigneditguiscreen(EntityPlayer e, TileEntityFireplace t)
	{
		FMLClientHandler.instance().displayGuiScreen(e, new GuiFlooSign(t));
	}
}
