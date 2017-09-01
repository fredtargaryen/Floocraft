package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.network.PacketHandler;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import static com.fredtargaryen.floocraft.FloocraftBase.greened;

public class BlockFloowerPot extends Block
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
    public boolean hasTileEntity(IBlockState ibs)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityFloowerPot();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
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

            if (!item.isEmpty() && item.getCount() > 0) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world,
                        pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
                        new ItemStack(item.getItem(), item.getCount(), item.getItemDamage()));

                if (item.hasTagCompound()) {
                    entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                }

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntity(entityItem);
                item.setCount(0);
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
        super.updateTick(worldIn, pos, state, rand);
        if(!worldIn.isRemote)
        {
            TileEntityFloowerPot pot = (TileEntityFloowerPot) worldIn.getTileEntity(pos);
            ItemStack stack = pot.getStackInSlot(0);
            if (stack != null && stack.getCount() > 0)
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
                            if(stack != null && stack.getCount() > 0) {
                                currentPos = new BlockPos(x, y, z);
                                currentBlock = worldIn.getBlockState(currentPos).getBlock();
                                if (currentBlock == Blocks.FIRE)
                                {
                                    if (((GreenFlamesBase) FloocraftBase.greenFlamesTemp).isInFireplace(worldIn, currentPos))
									{
                                        Item i = stack.getItem();
                                        worldIn.setBlockState(currentPos, FloocraftBase.greenFlamesIdle.getDefaultState().withProperty(GreenFlamesIdle.AGE, (int) ((ItemFlooPowder) i).getConcentration()), 3);
                                        worldIn.playSound(null, currentPos, greened, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                        stack = stack.getCount() == 1 ? ItemStack.EMPTY : stack.splitStack(stack.getCount() - 1);
                                    } else {
                                        worldIn.setBlockState(currentPos, Blocks.FIRE.getDefaultState());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            pot.setInventorySlotContents(0, stack);
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            worldIn.scheduleBlockUpdate(pos, state.getBlock(), this.tickRate(worldIn) + rand.nextInt(100), 0);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        worldIn.scheduleBlockUpdate(pos, state.getBlock(), this.tickRate(worldIn), 0);
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
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos someOtherPos)
    {
        if (!worldIn.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }
}