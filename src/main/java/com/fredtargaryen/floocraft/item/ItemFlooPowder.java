package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlames;
import com.fredtargaryen.floocraft.entity.EntityDroppedFlooPowder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

public class ItemFlooPowder extends Item
{
    private byte concentration;

    public byte getConcentration()
    {
        return this.concentration;
    }

	public ItemFlooPowder(byte conc)
	{
		super();
        this.concentration = conc;
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (worldIn.getBlockState(pos) == Blocks.torch)
		{
			worldIn.setBlockState(pos, FloocraftBase.flooTorch.getDefaultState());
			--stack.stackSize;
			return true;
		}
		else if (worldIn.getBlockState(pos.up(1)) == Blocks.fire)
		{
            if(worldIn.getBlockState(pos.up(2)).getBlock() == Blocks.air)
            {
                worldIn.extinguishFire(playerIn, pos, side);
                worldIn.setBlockState(pos.up(1), FloocraftBase.greenFlames.getDefaultState().withProperty(GreenFlames.AGE, this.concentration), 2);
            }
			--stack.stackSize;
            worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), "ftfloocraft:greened", 1.0F, 1.0F, true);
			return true;
		}
		return false;
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
			par3List.add(EnumChatFormatting.GREEN+"Concentration: \u221E tp/p");
			par3List.add("Creative mode only!");
		}
		else
		{
        	par3List.add(EnumChatFormatting.GREEN+"Concentration: "+this.concentration+" tp/p");
        	if(this.concentration == 1)
			{
				par3List.add("Can use in crafting");
			}
        }
    }
}