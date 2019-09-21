package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.entity.DroppedFlooPowderEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
            BlockPos firePos = pos.offset(Direction.UP, 1);
            if (worldIn.getBlockState(firePos).getBlock() == Blocks.FIRE) {
                if (((GreenFlamesBase) FloocraftBase.GREEN_FLAMES_TEMP).isInFireplace(worldIn, firePos) != null) {
                    worldIn.setBlockState(firePos, FloocraftBase.GREEN_FLAMES_BUSY.getDefaultState().with(BlockStateProperties.AGE_0_15, (int) this.concentration), 3);
                    worldIn.playSound(null, firePos, FloocraftBase.GREENED, SoundCategory.BLOCKS, 1.0F, 1.0F);
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
            DroppedFlooPowderEntity flp = new DroppedFlooPowderEntity(world, location.posX, location.posY, location.posZ, itemstack, this.concentration);
            //Set immune to fire in type;
            flp.setPickupDelay(40);
            flp.setMotion(location.getMotion());
            return flp;
        }
        return null;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(this.concentration == 9) {
			tooltip.add(new StringTextComponent(I18n.format("item.floocraftft.concentration", '\u221E')).applyTextStyle(TextFormatting.GREEN));
            tooltip.add(new StringTextComponent(I18n.format("item.floocraftft.creativeonly")));
		}
		else {
            tooltip.add(new StringTextComponent(I18n.format("item.floocraftft.concentration",this.concentration)).applyTextStyle(TextFormatting.GREEN));
        	if(this.concentration == 1) {
                tooltip.add(new StringTextComponent(I18n.format("item.floocraftft.craftable")));
			}
        }
    }
}