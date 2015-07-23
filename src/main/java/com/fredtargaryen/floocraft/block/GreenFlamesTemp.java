package com.fredtargaryen.floocraft.block;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Random;

public class GreenFlamesTemp extends GreenFlamesIdle
{
    @Override
    public int tickRate(World par1World)
    {
        return 100;
    }

    public GreenFlamesTemp(int renderID)
    {
        super(renderID);
    }

    @Override
    public void onBlockAdded(World w, int x, int y, int z)
    {
       w.scheduleBlockUpdate(x, y, z, this, this.tickRate(w));
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