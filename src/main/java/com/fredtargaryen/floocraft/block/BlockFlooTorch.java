package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.entity.EntityGreenFlame;
import com.google.common.base.Predicate;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.BlockTorch;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFlooTorch extends BlockTorch
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate()
    {
        private static final String __OBFID = "CL_00002054";
        public boolean apply(EnumFacing facing)
        {
            return facing != EnumFacing.DOWN;
        }
        public boolean apply(Object p_apply_1_)
        {
            return this.apply((EnumFacing)p_apply_1_);
        }
    });

	public BlockFlooTorch()
	{
		super();
	}
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World par1World, BlockPos pos, IBlockState state, Random rand)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        double d0 = (double)((float)pos.getX() + 0.5F);
        double d1 = (double)((float)pos.getY() + 0.7F);
        double d2 = (double)((float)pos.getZ() + 0.5F);
        double d3 = 0.2199999988079071D;
        double d4 = 0.27000001072883606D;
        switch(enumfacing) {
            case UP:
                par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
                Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0 - d4, d1 + d3, d2));
                break;
            case NORTH:
                par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
                Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0 + d4, d1 + d3, d2));
                break;
            case SOUTH:
                par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
                Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0, d1 + d3, d2 - d4));
                break;
            case WEST:
                par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
                Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0, d1 + d3, d2 + d4));
                break;
            case EAST:
                par1World.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(par1World, d0, d1, d2));
                break;
        }
    }
	
	public int quantityDropped(Random par1Random)
    {
        return 1;
    }
}
