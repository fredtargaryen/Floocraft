package com.fredtargaryen.floocraft.block;

import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesIdle extends GreenFlamesLowerBase
{
    private BlockFire blockToSet;
    private int ticksToWait;
    private boolean usedForTP;

	public GreenFlamesIdle()
    {
		super();
    }

    public GreenFlamesIdle(BlockFire bf)
    {
        this();
        this.blockToSet = bf;
    }
	public boolean approveOrDenyTeleport(World par1World, int par2, int par3, int par4)
	{
        boolean b = this.isInFireplace(par1World, par2, par3, par4);
        if(b)
        {
            this.ticksToWait = 28;
        }
        else
        {
            this.ticksToWait = 2;
        }
		return b;
	}

    @Override
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if(blockToSet != null)
        {
            this.ticksToWait--;
            if(ticksToWait < 1)
            {
                par1World.setBlockToAir(par2, par3, par4);
                par1World.setBlock(par2, par3, par4, this.blockToSet);
            }
        }
        super.updateTick(par1World, par2, par3, par4, par5Random);
    }
}
