package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.FlooSignBlock;
import com.fredtargaryen.floocraft.client.gui.FlooSignScreen;
import com.fredtargaryen.floocraft.tileentity.FireplaceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class ItemFlooSign extends Item {
	public ItemFlooSign(Properties p) { super(p); }

    @Override
    @Nonnull
	public ActionResultType onItemUse(ItemUseContext context) {
	    Direction side = context.getFace();
		if (side == Direction.DOWN || side == Direction.UP) {
            return ActionResultType.FAIL;
        }
        else {
            PlayerEntity player = context.getPlayer();
            BlockPos pos = context.getPos();
            ItemStack stack = context.getItem();
            if (!player.canPlayerEdit(pos, side, stack)) {
                return ActionResultType.FAIL;
            }
            else {
                World world = context.getWorld();
                BlockPos newpos = pos.offset(side);
            	world.setBlockState(newpos, FloocraftBase.BLOCK_FLOO_SIGN.getDefaultState().with(FlooSignBlock.FACING, side), 3);
            	stack.grow(-1);
            	FireplaceTileEntity fireplaceTE = (FireplaceTileEntity)world.getTileEntity(newpos);
            	if (fireplaceTE != null) {
            		fireplaceTE.setPlayer(player);
            		if(world.isRemote) {
            			this.dothesigneditguiscreen(fireplaceTE);
            		}
                }
                return ActionResultType.SUCCESS;
            }
        }
    }
	
	@OnlyIn(Dist.CLIENT)
	private void dothesigneditguiscreen(FireplaceTileEntity t) {
        Minecraft.getInstance().displayGuiScreen(new FlooSignScreen(t));
	}
}