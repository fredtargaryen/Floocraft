package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.entity.EntityGreenFlame;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockFlooTorch extends BlockTorch
{
    private static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate()
    {
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

    protected BlockState createBlockState()
    {
        return new BlockState(this, FACING);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY() + 0.7D;
        double d2 = (double) pos.getZ() + 0.5D;
        double d3 = 0.22D;
        double d4 = 0.27D;

        if (enumfacing.getAxis().isHorizontal()) {
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4 * (double) enumfacing1.getFrontOffsetX(), d1 + d3, d2 + d4 * (double) enumfacing1.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(worldIn, d0 + d4 * (double) enumfacing1.getFrontOffsetX(), d1 + d3, d2 + d4 * (double) enumfacing1.getFrontOffsetZ()));
        } else {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(new EntityGreenFlame(worldIn, d0, d1, d2));
        }
    }

    public boolean isCollidable()
    {
        return true;
    }

	public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity)
    {
        if (!par1World.isRemote)
        {
            if (par5Entity instanceof EntityPlayer)
            {
                Random r = new Random();
                int blockx = pos.getX();
                int minx = blockx - 3;
                int maxx = blockx + 3;
                int blockz = pos.getZ();
                int minz = blockz - 3;
                int maxz = blockz + 3;
                int blocky = pos.getY();
                List<BlockPos> coords = new ArrayList<BlockPos>();
                for (int x = minx; x <= maxx; x++)
                {
                    for (int z = minz; z <= maxz; z++)
                    {
                        BlockPos nextPos = new BlockPos(x, blocky, z);
                        if (par1World.isAirBlock(nextPos) && par1World.isAirBlock(nextPos.up()))
                        {
                            coords.add(nextPos);
                        }
                    }
                }
                if(coords.size() > 0)
                {
                    BlockPos chosenCoord = coords.get(r.nextInt(coords.size())).add(0.5D, 0.0D, 0.5D);
                    double x = chosenCoord.getX();
                    double y = chosenCoord.getY();
                    double z = chosenCoord.getZ();
                    par5Entity.setPositionAndUpdate(x, y, z);
                    par1World.playSoundEffect(x, y, z, DataReference.MODID + ":flick", 1.0F, 1.0F);
                }
            }
        }
    }
}