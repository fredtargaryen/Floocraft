package com.fredtargaryen.floocraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesTemp extends GreenFlamesIdle
{
    private final int renderID;

    @Override
    public int tickRate(World par1World)
    {
        return 100;
    }

    public GreenFlamesTemp(int renderID)
    {
        super();
        this.renderID = renderID;
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

    @Override
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
    }

    public int getRenderType()
    {
        return this.renderID;
    }
}