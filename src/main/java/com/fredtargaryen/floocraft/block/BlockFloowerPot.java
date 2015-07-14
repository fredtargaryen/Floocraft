package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFloowerPot extends BlockContainer
{
    public BlockFloowerPot()
    {
        super(Material.circuits);
        this.setBlockBounds(0.3125F, 0.0F, 0.3125F, 0.6875F, 0.375F, 0.6875F);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntityFloowerPot();
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity == null || player.isSneaking())
        {
            return false;
        }
        player.openGui(FloocraftBase.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        this.dropItems(worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }

    private void dropItems(World world, BlockPos pos){
        Random rand = new Random();

        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof IInventory)) {
            return;
        }
        IInventory inventory = (IInventory) tileEntity;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (item != null && item.stackSize > 0) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world,
                        pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
                        new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

                if (item.hasTagCompound()) {
                    entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                }

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntityInWorld(entityItem);
                item.stackSize = 0;
            }
        }
    }

    @Override
    public int tickRate(World par1World)
    {
        return 50;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if(!worldIn.isRemote)
        {
            TileEntityFloowerPot pot = (TileEntityFloowerPot) worldIn.getTileEntity(pos);
            ItemStack stack = pot.getStackInSlot(0);
            if (stack != null && stack.stackSize > 0)
            {
                int par2 = pos.getX();
                int par3 = pos.getY();
                int par4 = pos.getZ();
                for (int x = par2 - 5; x < par2 + 6; x++)
                {
                    for (int y = par3 - 5; y < par3 + 6; y++)
                    {
                        for (int z = par4 - 5; z < par4 + 6; z++)
                        {
                            if (state.getBlock() == Blocks.fire)
                            {
                                worldIn.setBlockState(pos, FloocraftBase.greenFlamesTemp.getDefaultState());
                                GreenFlamesTemp gfit = (GreenFlamesTemp) worldIn.getBlockState(pos).getBlock();
                                boolean shouldPut = gfit.approveOrDenyTeleport(worldIn, pos);
                                worldIn.setBlockState(pos, Blocks.fire.getDefaultState());
                                if (shouldPut)
                                {
                                    Item i = stack.getItem();
                                    worldIn.setBlockState(pos, FloocraftBase.greenFlamesIdle.getDefaultState().withProperty(GreenFlamesIdle.AGE, ((ItemFlooPowder) i).getConcentration()), 2);
                                    stack.stackSize--;
                                    pot.setInventorySlotContents(0, stack.stackSize == 0 ? null : stack.splitStack(stack.stackSize));
                                }
                            }
                        }
                    }
                }
            }
        }
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn) + rand.nextInt(100));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
     */
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && World.doesBlockHaveSolidTopSurface(worldIn, new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()));
    }

    /**
     * Called when a neighboring block changes.
     */
    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!World.doesBlockHaveSolidTopSurface(worldIn, new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
}