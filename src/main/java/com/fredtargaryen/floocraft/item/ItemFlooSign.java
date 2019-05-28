package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.BlockFlooSign;
import com.fredtargaryen.floocraft.client.gui.GuiFlooSign;
import com.fredtargaryen.floocraft.tileentity.TileEntityFireplace;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFlooSign extends Item {
	public ItemFlooSign(Properties p) { super(p); }

    @Override
	public EnumActionResult onItemUse(ItemUseContext context) {
	    EnumFacing side = context.getFace();
		if (side == EnumFacing.DOWN || side == EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }
        else {
            EntityPlayer player = context.getPlayer();
            BlockPos pos = context.getPos();
            ItemStack stack = context.getItem();
            if (!player.canPlayerEdit(pos, side, stack)) {
                return EnumActionResult.FAIL;
            }
            else {
                World world = context.getWorld();
                BlockPos newpos = pos.offset(side);
            	world.setBlockState(newpos, FloocraftBase.BLOCK_FLOO_SIGN.getDefaultState().with(BlockFlooSign.FACING, side), 3);
            	stack.grow(-1);
            	TileEntityFireplace fireplaceTE = (TileEntityFireplace)world.getTileEntity(newpos);
            	if (fireplaceTE != null) {
            		fireplaceTE.setPlayer(player);
            		if(world.isRemote) {
            			this.dothesigneditguiscreen(fireplaceTE);
            		}
                }
                return EnumActionResult.SUCCESS;
            }
        }
    }
	
	@OnlyIn(Dist.CLIENT)
	private void dothesigneditguiscreen(TileEntityFireplace t) {
        Minecraft.getInstance().displayGuiScreen(new GuiFlooSign(t));
	}
}