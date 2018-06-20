package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBusy;
import com.fredtargaryen.floocraft.entity.EntityDroppedFlooPowder;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static com.fredtargaryen.floocraft.FloocraftBase.greened;

public class ItemFlooPowder extends Item
{
    private final byte concentration;

    public byte getConcentration()
    {
        return this.concentration;
    }

	public ItemFlooPowder(byte conc)
	{
		super();
        this.concentration = conc;
	}

    @Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
        BlockPos firePos = pos.offset(EnumFacing.UP, 1);
		if (worldIn.getBlockState(firePos).getBlock() == Blocks.FIRE)
		{
            if(((GreenFlamesBase)FloocraftBase.greenFlamesTemp).isInFireplace(worldIn, firePos) != null)
            {
                worldIn.setBlockState(firePos, FloocraftBase.greenFlamesBusy.getDefaultState().withProperty(GreenFlamesBusy.AGE, (int) this.concentration), 2);
                worldIn.playSound(null, firePos, greened, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            else
            {
                worldIn.setBlockState(firePos, Blocks.FIRE.getDefaultState(), 2);
            }
            playerIn.getHeldItem(hand).grow(-1);
			return EnumActionResult.PASS;
		}
		return EnumActionResult.FAIL;
	}
	
	/**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to function normally.
     * Called when the item it placed in a world.
     *
     * @param world The world object
     * @param location The EntityItem object, useful for getting the position of the entity
     * @param itemstack The current item stack
     * @return A new Entity object to spawn or null
     */
    public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        if(!world.isRemote)
        {
            EntityDroppedFlooPowder flp = new EntityDroppedFlooPowder(world, location.posX, location.posY, location.posZ, itemstack, this.concentration);
            flp.setImmunity();
            flp.setPickupDelay(40);
            flp.motionX = location.motionX;
            flp.motionY = location.motionY;
            flp.motionZ = location.motionZ;
            return flp;
        }
        return null;
    }
    
    public boolean hasCustomEntity(ItemStack stack)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
		if(this.concentration == 9)
		{
			par3List.add(ChatFormatting.GREEN+"Concentration: \u221E tp/p");
			par3List.add("Creative mode only!");
		}
		else
		{
        	par3List.add(ChatFormatting.GREEN+"Concentration: "+this.concentration+" tp/p");
        	if(this.concentration == 1)
			{
				par3List.add("Can use in crafting");
			}
        }
    }
}