package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBlockEntityTypes;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.network.MessageHandler;
import com.fredtargaryen.floocraft.network.messages.OpenFlooSignEditScreenMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlooSignItem extends SignItem {
    public FlooSignItem(Properties p) {
        super(p, FloocraftBlocks.FLOO_SIGN.get(), FloocraftBlocks.FLOO_SIGN.get());
    }

    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack itemStack, BlockState state) {
        boolean updated = super.updateCustomBlockEntityTag(pos, level, player, itemStack, state);
        if (!level.isClientSide && !updated && player != null) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null && blockEntity.getType() == FloocraftBlockEntityTypes.FLOO_SIGN.get()) {
                Block block = level.getBlockState(pos).getBlock();
                if (block == FloocraftBlocks.FLOO_SIGN.get()) {
                    OpenFlooSignEditScreenMessage ofsesm = new OpenFlooSignEditScreenMessage(pos);
                    MessageHandler.sendToPlayer(ofsesm, (ServerPlayer) player);
                }
            }
        }

        return updated;
    }

    @Override
    @Nonnull
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
//	    Direction side = context.getClickedFace();
////		if (side == Direction.DOWN || side == Direction.UP) {
////            return InteractionResult.FAIL;
////        }
////        else {
//            Player player = context.getPlayer();
//            BlockPos pos = context.getClickedPos();
//            ItemStack stack = context.getItemInHand();
//            if (!player.canPlayerEdit(pos, side, stack)) {
//                return InteractionResult.FAIL;
//            }
//            else {
//                Level level = context.getLevel();
//                BlockPos newpos = pos.relative(side);
//            	level.setBlockAndUpdate(newpos, FloocraftBlocks.FLOO_SIGN.get()
//                        .defaultBlockState()
//                        .setValue(FlooSignBlock.FACING, side));
//            	stack.grow(-1);
//            	FlooSignBlockEntity fsbe = (FlooSignBlockEntity) level.getBlockEntity(newpos);
//            	if (fsbe != null) {
//                    fsbe.setPlayer(player);
//            		if(level.isClientSide) {
//            			this.dothesigneditguiscreen(fireplaceTE);
//            		}
//                }
//                return InteractionResult.SUCCESS;
//            }
//        }
    }
}