package com.fredtargaryen.floocraft.entity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBusy;
import com.fredtargaryen.floocraft.block.GreenFlamesTemp;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.fredtargaryen.floocraft.FloocraftBase.greened;

public class EntityDroppedFlooPowder extends EntityItem
{
    private byte concentration;

	public EntityDroppedFlooPowder(World world, double x, double y, double z, ItemStack stack, byte conc)
	{
		super(world, x, y, z, stack);
        this.concentration = conc;
	}
	
	public void setImmunity()
	{
		super.isImmuneToFire = true;
	}

	/**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        BlockPos pos = this.getPosition();
        if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE)
        {
			if(((GreenFlamesTemp)FloocraftBase.greenFlamesTemp).isInFireplace(this.world, pos) != null)
			{
                this.world.setBlockState(pos, FloocraftBase.greenFlamesBusy.getDefaultState().withProperty(GreenFlamesBusy.AGE, (int) this.concentration), 3);
                this.playSound(greened, 1.0F, 1.0F);
            }
            this.setDead();
        }
        super.onUpdate();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound par1)
    {
        par1 = super.writeToNBT(par1);
        par1.setByte("Concentration", this.concentration);
        return par1;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1)
    {
        super.readFromNBT(par1);
        this.concentration = par1.getByte("Concentration");
    }
}
