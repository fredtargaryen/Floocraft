package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.FlooTorchBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemFlooTorch extends Item {
    public ItemFlooTorch() {
        super(new Item.Properties().group(ItemGroup.DECORATIONS));
    }

    @Override
    @Nonnull
    public ActionResultType onItemUse(ItemUseContext context) {
        World w = context.getWorld();
        if(!w.isRemote) {
            Direction side = context.getFace();
            if (side == Direction.DOWN) {
                return ActionResultType.PASS;
            } else {
                BlockPos pos = context.getPos();
                BlockState blockPlacedOn = w.getBlockState(pos);
                PlayerEntity player = context.getPlayer();
                ItemStack stack = context.getItem();
                if (player != null && player.canPlayerEdit(pos, side, stack)) {
                    if (blockPlacedOn.isSolidSide(w, pos, side)) {
                        w.setBlockState(pos.offset(side),
                                FloocraftBase.BLOCK_FLOO_TORCH.get().getDefaultState().with(FlooTorchBlock.FACING_EXCEPT_DOWN, side),
                                3);
                        stack.grow(-1);
                        return ActionResultType.CONSUME;
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }
}
