package com.fredtargaryen.floocraft.item;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.EntityDroppedFlooPowder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDirectional;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
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

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister i)
	{
		this.itemIcon = i.registerIcon(DataReference.resPath("item.floopowder"));
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int X, int Y, int Z, int par7, float par8, float par9, float par10)
	{
		if (par3World.getBlock(X, Y, Z) == Blocks.torch)
		{
			par3World.setBlock(X, Y, Z, FloocraftBase.flooTorch);
			--par1ItemStack.stackSize;
			return true;
		}
		else if (par3World.getBlock(X, Y + 1, Z) == Blocks.fire)
		{
			par3World.extinguishFire(par2EntityPlayer, X, Y, Z, BlockDirectional.getDirection(par3World.getBlockMetadata(X, Y, Z)));
            par3World.setBlock(X, Y + 1, Z, FloocraftBase.greenFlamesBusyLower, this.concentration, 2);
            if(par3World.getBlock(X, Y + 2, Z) == Blocks.air)
            {
                par3World.setBlock(X, Y + 2, Z, FloocraftBase.greenFlamesBusyHigher);
            }
			--par1ItemStack.stackSize;
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
    	EntityDroppedFlooPowder flp = new EntityDroppedFlooPowder(world, location.posX, location.posY, location.posZ, itemstack, this.concentration);
    	flp.setImmunity();
    	flp.setPickupDelay(40);
    	flp.motionX = location.motionX;
    	flp.motionY = location.motionY;
    	flp.motionZ = location.motionZ;
    	return flp;
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