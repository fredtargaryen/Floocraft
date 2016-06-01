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
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockFloowerPot extends BlockContainer
{
    private static final AxisAlignedBB POTBOX = new AxisAlignedBB(0.3125F, 0.0F, 0.3125F, 0.6875F, 0.375F, 0.6875F);

    public BlockFloowerPot()
    {
        super(Material.CIRCUITS);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return POTBOX;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntityFloowerPot();
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
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
            if (stack != null)
            {
                int par2 = pos.getX();
                int par3 = pos.getY();
                int par4 = pos.getZ();
                BlockPos currentPos;
                Block currentBlock;
                for (int x = par2 - 5; x < par2 + 6; x++)
                {
                    for (int y = par3 - 5; y < par3 + 6; y++)
                    {
                        for (int z = par4 - 5; z < par4 + 6; z++)
                        {
                            if(stack.stackSize > 0) {
                                currentPos = new BlockPos(x, y, z);
                                currentBlock = worldIn.getBlockState(currentPos).getBlock();
                                if (currentBlock == Blocks.FIRE) {
                                    worldIn.setBlockState(currentPos, FloocraftBase.greenFlamesTemp.getDefaultState());
                                    GreenFlamesTemp gfit = (GreenFlamesTemp) worldIn.getBlockState(currentPos).getBlock();
                                    if (gfit.isInFireplace(worldIn, currentPos)) {
                                        Item i = stack.getItem();
                                        worldIn.setBlockState(currentPos, FloocraftBase.greenFlamesIdle.getDefaultState().withProperty(GreenFlamesIdle.AGE, (int) ((ItemFlooPowder) i).getConcentration()), 3);
                                        //worldIn.playSound((double) x, (double) y, (double) z, DataReference.MODID + ":greened", 1.0F, 1.0F, true);
                                        stack.stackSize--;
                                        pot.setInventorySlotContents(0, stack.stackSize == 0 ? null : stack.splitStack(stack.stackSize));
                                    } else {
                                        worldIn.setBlockState(currentPos, Blocks.FIRE.getDefaultState());
                                    }
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

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
     */
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP);
    }

    /**
     * Called when a neighboring block changes.
     */
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
        if (!worldIn.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
}