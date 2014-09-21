package com.fredtargaryen.floocraft.block;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import java.util.Random;

public class GreenFlamesIdleTemp extends GreenFlamesIdle
{
    @Override
    public int tickRate(World par1World)
    {
        return 100;
    }

    public GreenFlamesIdleTemp()
    {
        super();
    }

    public boolean approveOrDenyTeleport(World par1World, int par2, int par3, int par4)
    {
        return this.isInFireplace(par1World, par2, par3, par4);
    }

    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
    }

    @Override
    public void updateTick(World w, int x, int y, int z, Random par5Random)
    {
        w.setBlock(x, y, z, Blocks.fire);
    }
}