package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.BlockFlooSign;
import com.fredtargaryen.floocraft.client.gui.GuiFlooSign;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.util.math.BlockPos;
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
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if (side == EnumFacing.DOWN || side == EnumFacing.UP)
        {
            return EnumActionResult.FAIL;
        }
        else
        {
        	ItemStack stack = player.getHeldItem(hand);
            if (!player.canPlayerEdit(pos, side, stack))
            {
                return EnumActionResult.FAIL;
            }
            else
            {
                BlockPos newpos = pos.offset(side);
            	world.setBlockState(newpos, FloocraftBase.blockFlooSign.getDefaultState().withProperty(BlockFlooSign.FACING, side), 3);
            	stack.grow(-1);
            	TileEntityFireplace fireplaceTE = (TileEntityFireplace)world.getTileEntity(newpos);
            	if (fireplaceTE != null)
            	{
            		fireplaceTE.setPlayer(player);
            		if(world.isRemote)
            		{
            			this.dothesigneditguiscreen(player, fireplaceTE);
            		}
                }
                return EnumActionResult.SUCCESS;
            }
        }
    }
	
	@SideOnly(Side.CLIENT)
	private void dothesigneditguiscreen(EntityPlayer e, TileEntityFireplace t)
	{
		FMLClientHandler.instance().displayGuiScreen(e, new GuiFlooSign(t));
	}
}