package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.BlockFlooTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ItemFlooTorch extends Item {
    public ItemFlooTorch() {
        super(new Item.Properties().group(ItemGroup.DECORATIONS));
    }

    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {
        EnumFacing side = context.getFace();
        if(side == EnumFacing.DOWN) {
            return EnumActionResult.FAIL;
        }
        else {
            BlockPos pos = context.getPos();
            IBlockState blockPlacedOn = context.getWorld().getBlockState(pos);
            EntityPlayer player = context.getPlayer();
            ItemStack stack = context.getItem();
            if (player.canPlayerEdit(pos, side, stack)) {
                if(blockPlacedOn.isSolid()) {
                    context.getWorld().setBlockState(pos.offset(side),
                            FloocraftBase.BLOCK_FLOO_TORCH.getDefaultState().with(BlockFlooTorch.FACING_EXCEPT_DOWN, side),
                            3);
                    stack.grow(-1);
                    return EnumActionResult.SUCCESS;
                }
            }
            return EnumActionResult.FAIL;
        }
    }
}
