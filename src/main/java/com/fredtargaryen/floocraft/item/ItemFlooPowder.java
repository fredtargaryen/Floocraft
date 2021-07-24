package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.FlooFlamesBase;
import com.fredtargaryen.floocraft.config.CommonConfig;
import com.fredtargaryen.floocraft.entity.DroppedFlooPowderEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemFlooPowder extends Item {
    private final byte concentration;

    public byte getConcentration()
    {
        return this.concentration;
    }

	public ItemFlooPowder(byte conc) {
		super(new Item.Properties().group(ItemGroup.MISC).maxStackSize(64));
        this.concentration = conc;
	}

    @Override
	public ActionResultType onItemUse(ItemUseContext context) {
        World worldIn = context.getWorld();
        BlockPos pos = context.getPos();
	    if(!worldIn.isRemote) {
	        BlockState state = worldIn.getBlockState(pos);
	        Block b = state.getBlock();
	        if(b == Blocks.CAMPFIRE && state.get(BlockStateProperties.LIT))
            {
                worldIn.setBlockState(pos, FloocraftBase.FLOO_CAMPFIRE.get().getDefaultState()
                        .with(BlockStateProperties.HORIZONTAL_FACING, state.get(BlockStateProperties.HORIZONTAL_FACING))
                        .with(BlockStateProperties.AGE_0_15, (int) this.concentration), 3);
                worldIn.playSound(null, pos, FloocraftBase.GREENED.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                context.getItem().grow(-1);
                return ActionResultType.SUCCESS;
            }
            else if(b == Blocks.SOUL_CAMPFIRE && state.get(BlockStateProperties.LIT))
            {
                worldIn.setBlockState(pos, FloocraftBase.FLOO_SOUL_CAMPFIRE.get().getDefaultState()
                        .with(BlockStateProperties.HORIZONTAL_FACING, state.get(BlockStateProperties.HORIZONTAL_FACING))
                        .with(BlockStateProperties.AGE_0_15, (int) this.concentration), 3);
                worldIn.playSound(null, pos, FloocraftBase.GREENED.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                context.getItem().grow(-1);
                return ActionResultType.SUCCESS;
            }
            else if (b.isIn(BlockTags.FIRE)) {
                if (((FlooFlamesBase) FloocraftBase.GREEN_FLAMES_TEMP.get()).isInFireplace(worldIn, pos) != null) {
                    Block fireBlock = SoulFireBlock.shouldLightSoulFire(worldIn.getBlockState(pos.down()).getBlock()) ? FloocraftBase.MAGENTA_FLAMES_BUSY.get() : FloocraftBase.GREEN_FLAMES_BUSY.get();
                    worldIn.setBlockState(pos, fireBlock.getDefaultState().with(BlockStateProperties.AGE_0_15, (int) this.concentration), 3);
                    worldIn.playSound(null, pos, FloocraftBase.GREENED.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                context.getItem().grow(-1);
                return ActionResultType.SUCCESS;
            }
        }
		return ActionResultType.FAIL;
	}
	
	/**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the ItemEntity and will leave it to function normally.
     * Called when the item it placed in a world.
     *
     * @param world The world object
     * @param location The ItemEntity object, useful for getting the position of the entity
     * @param itemstack The current item stack
     * @return A new Entity object to spawn or null
     */
	@Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        if(!world.isRemote) {
            Vector3d pos = location.getPositionVec();
            DroppedFlooPowderEntity flp = new DroppedFlooPowderEntity(world, pos.x, pos.y, pos.z, itemstack, this.concentration);
            //Set immune to fire in type;
            flp.setPickupDelay(40);
            flp.setMotion(location.getMotion());
            return flp;
        }
        return null;
    }

    /**
     * TODO Temporarily disabled. Entering a world with DroppedFlooPowderEntities in it causes the server to freeze.
     * This is a known Forge issue.
     */
    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(this.concentration == 9) {
			tooltip.add(new StringTextComponent(I18n.format("item.floocraftft.concentration", '\u221E')).mergeStyle(TextFormatting.GREEN));
            tooltip.add(new StringTextComponent(I18n.format("item.floocraftft.creativeonly")));
		}
		else {
		    if(CommonConfig.DEPLETE_FLOO.get()) {
                tooltip.add(new StringTextComponent(I18n.format("item.floocraftft.concentration", this.concentration)).mergeStyle(TextFormatting.GREEN));
            }
		    else
            {
                tooltip.add(new StringTextComponent(I18n.format("item.floocraftft.concentration", '\u221E')).mergeStyle(TextFormatting.GREEN));
            }
        	if(this.concentration == 1) {
                tooltip.add(new StringTextComponent(I18n.format("item.floocraftft.craftable")));
			}
        }
    }
}