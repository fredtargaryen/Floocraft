package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.ParticleGreenFlame;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageFlooTorchTeleport;
import com.fredtargaryen.floocraft.tileentity.TileEntityAlbedoFire;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        EnumFacing enumfacing = state.getValue(FACING);
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY() + 0.7D;
        double d2 = (double) pos.getZ() + 0.5D;
        double d3 = 0.22D;
        double d4 = 0.27D;

        if (enumfacing.getAxis().isHorizontal()) {
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4 * (double) enumfacing1.getFrontOffsetX(), d1 + d3, d2 + d4 * (double) enumfacing1.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleGreenFlame(worldIn, d0 + d4 * (double) enumfacing1.getFrontOffsetX(), d1 + d3, d2 + d4 * (double) enumfacing1.getFrontOffsetZ()));
        } else {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleGreenFlame(worldIn, d0, d1, d2));
        }
    }

    @Override
    public boolean isCollidable()
    {
        return true;
    }

    @Override
	public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, BlockPos pos, IBlockState state, Entity par5Entity)
    {
        if (par1World.isRemote)
        {
            if (par5Entity instanceof EntityPlayer)
            {
                //Triggered by a player on the client side.
                MessageFlooTorchTeleport mftt = new MessageFlooTorchTeleport();
                mftt.torchX = pos.getX();
                mftt.torchY = pos.getY();
                mftt.torchZ = pos.getZ();
                PacketHandler.INSTANCE.sendToServer(mftt);
            }
        }
    }

    ////////////////////////
    //ALBEDO COMPATIBILITY//
    ////////////////////////
    @Override
    public boolean hasTileEntity(IBlockState ibs)
    {
        return FloocraftBase.isAlbedoInstalled();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        TileEntityAlbedoFire teaf = null;
        if(FloocraftBase.isAlbedoInstalled())
        {
            teaf = new TileEntityAlbedoFire();
            teaf.setRadius(4.0F);
        }
        return teaf;
    }
}