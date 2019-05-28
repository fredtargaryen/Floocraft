package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;

public class BlockFloowerPot extends Block {
    protected static final VoxelShape POTBOX = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

    public BlockFloowerPot()
    {
        super(Block.Properties.create(Material.CIRCUITS).hardnessAndResistance(0F));
    }

    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return POTBOX;
    }

    @Override
    public boolean hasTileEntity(IBlockState ibs)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world)
    {
        return new TileEntityFloowerPot();
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity == null || player.isSneaking())
        {
            return false;
        }
        NetworkHooks.openGui((EntityPlayerMP) player, (IInteractionObject) tileEntity, buf -> {
            BlockPos blockPos = tileEntity.getPos();
            buf.writeInt(blockPos.getX());
            buf.writeInt(blockPos.getY());
            buf.writeInt(blockPos.getZ());
        });
        return true;
    }

    @Override
    public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
        this.dropItems(worldIn, pos);
        super.onReplaced(state, worldIn, pos, newState, isMoving);
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
                        new ItemStack(item.getItem(), item.getCount(), item.getTag()));

                if (item.hasTag()) {
                    entityItem.getItem().setTag(item.getTag().copy());
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
    public int tickRate(IWorldReaderBase par1World)
    {
        return 50;
    }

    @Override
    public void tick(IBlockState state, World world, BlockPos pos, Random rand) {
        super.tick(state, world, pos, rand);
        if(!world.isRemote) {
            TileEntityFloowerPot pot = (TileEntityFloowerPot) world.getTileEntity(pos);
            ItemStack stack = pot.getStackInSlot(0);
            if (stack != null && stack.getCount() > 0) {
                int par2 = pos.getX();
                int par3 = pos.getY();
                int par4 = pos.getZ();
                BlockPos currentPos;
                Block currentBlock;
                for (int x = par2 - 5; x < par2 + 6; x++) {
                    for (int y = par3 - 5; y < par3 + 6; y++) {
                        for (int z = par4 - 5; z < par4 + 6; z++) {
                            if(stack != null && stack.getCount() > 0) {
                                currentPos = new BlockPos(x, y, z);
                                currentBlock = world.getBlockState(currentPos).getBlock();
                                if (currentBlock == Blocks.FIRE) {
                                    if (((GreenFlamesBase) FloocraftBase.GREEN_FLAMES_TEMP).isInFireplace(world, currentPos) != null) {
                                        Item i = stack.getItem();
                                        world.setBlockState(currentPos, FloocraftBase.GREEN_FLAMES_IDLE.getDefaultState().with(BlockStateProperties.AGE_0_15, (int) ((ItemFlooPowder) i).getConcentration()), 3);
                                        world.playSound(null, currentPos, FloocraftBase.GREENED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                        stack = stack.getCount() == 1 ? ItemStack.EMPTY : stack.split(stack.getCount() - 1);
                                    } else {
                                        world.setBlockState(currentPos, Blocks.FIRE.getDefaultState());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            pot.setInventorySlotContents(0, stack);
            world.notifyBlockUpdate(pos, state, state, 3);
            world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), this.tickRate(world) + rand.nextInt(100), TickPriority.EXTREMELY_LOW);
        }
    }

    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
        worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), this.tickRate(worldIn), TickPriority.EXTREMELY_LOW);
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
    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        return super.isValidPosition(state, worldIn, pos) && worldIn.getBlockState(pos.down()).getBlockFaceShape(worldIn, pos, EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    /**
     * Called when a neighboring block changes.
     */
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos someOtherPos) {
        if (worldIn.getBlockState(pos.down()).getBlockFaceShape(worldIn, pos, EnumFacing.UP) != BlockFaceShape.SOLID) {
            this.onReplaced(state, worldIn, pos, Blocks.AIR.getDefaultState(), false);
        }
    }
}