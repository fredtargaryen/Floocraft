package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.BlockFlooSign;
import com.fredtargaryen.floocraft.client.gui.GuiFlooSign;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFlooSign extends Item
{
	public ItemFlooSign()
	{
		super();
		this.maxStackSize = 16;
	}

    @Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing side, float hitx, float hity, float hitz)
    {
		if (side == EnumFacing.DOWN || side == EnumFacing.UP)
        {
            return false;
        }
        else
        {
            if (!par2EntityPlayer.canPlayerEdit(pos, side, par1ItemStack))
            {
                return false;
            }
            else
            {
                BlockPos newpos = pos.offset(side);
            	par3World.setBlockState(newpos, FloocraftBase.blockFlooSign.getDefaultState().withProperty(BlockFlooSign.FACING, side), 3);
            	--par1ItemStack.stackSize;
            	TileEntityFireplace fireplaceTE = (TileEntityFireplace)par3World.getTileEntity(newpos);
            	if (fireplaceTE != null)
            	{
            		fireplaceTE.setPlayer(par2EntityPlayer);
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
