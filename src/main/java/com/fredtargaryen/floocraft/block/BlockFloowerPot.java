package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.tileentity.FloowerPotTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockFloowerPot extends Block {
    private static final VoxelShape POTBOX = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

    public BlockFloowerPot()
    {
        super(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0F));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return POTBOX;
    }

    @Override
    public boolean hasTileEntity(BlockState ibs)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new FloowerPotTileEntity();
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity == null || player.isSneaking())
        {
            return ActionResultType.FAIL;
        }
        if(!worldIn.isRemote)
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
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

                ItemEntity ItemEntity = new ItemEntity(world,
                        pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
                        new ItemStack(item.getItem(), item.getCount(), item.getTag()));

                if (item.hasTag()) {
                    ItemEntity.getItem().setTag(item.getTag().copy());
                }

                float factor = 0.05F;
                ItemEntity.setMotion(
                        rand.nextGaussian() * factor,
                        rand.nextGaussian() * factor + 0.2F,
                        rand.nextGaussian() * factor
                );
                world.addEntity(ItemEntity);
                item.setCount(0);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        super.tick(state, world, pos, rand);
        if(!world.isRemote) {
            FloowerPotTileEntity pot = (FloowerPotTileEntity) world.getTileEntity(pos);
            int hRange = pot.getHRange();
            int vRange = pot.getVRange();
            ItemStack stack = pot.getStackInSlot(0);
            if (stack != null && stack.getCount() > 0) {
                int par2 = pos.getX();
                int par3 = pos.getY();
                int par4 = pos.getZ();
                BlockPos currentPos;
                Block currentBlock;
                Block greenBusy = FloocraftBase.GREEN_FLAMES_BUSY.get();
                BlockState greenState = greenBusy.getDefaultState();
                BlockState magentaState = FloocraftBase.MAGENTA_FLAMES_BUSY.get().getDefaultState();
                SoundEvent greened = FloocraftBase.GREENED.get();
                for (int x = par2 - hRange; x <= par2 + hRange; x++) {
                    for (int y = par3 - vRange; y <= par3 + vRange; y++) {
                        for (int z = par4 - hRange; z <= par4 + hRange; z++) {
                            if(stack != null && stack.getCount() > 0) {
                                currentPos = new BlockPos(x, y, z);
                                currentBlock = world.getBlockState(currentPos).getBlock();
                                if (currentBlock.isIn(BlockTags.FIRE)) {
                                    if (((FlooFlamesBase) greenBusy).isInFireplace(world, currentPos) != null) {
                                        Item i = stack.getItem();
                                        boolean soul = SoulFireBlock.shouldLightSoulFire(world.getBlockState(currentPos.down()).getBlock());
                                        BlockState stateToSet = soul ? magentaState : greenState;
                                        world.setBlockState(currentPos, stateToSet.with(BlockStateProperties.AGE_0_15, (int) ((ItemFlooPowder) i).getConcentration()), 3);
                                        world.playSound(null, currentPos, greened, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                        stack = stack.getCount() == 1 ? ItemStack.EMPTY : stack.split(stack.getCount() - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            pot.setInventorySlotContents(0, stack);
            world.notifyBlockUpdate(pos, state, state, 3);
            world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 50 + rand.nextInt(100), TickPriority.EXTREMELY_LOW);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean b) {
        worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 50, TickPriority.EXTREMELY_LOW);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via IBlockState#getRenderType() whenever possible. Implementing/overriding is fine.
     */
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
     */
    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return super.isValidPosition(state, worldIn, pos);
    }

    /**
     * Called when a neighboring block changes.
     */
    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
        if (!this.isValidPosition(state, worldIn, pos)) {
            this.onReplaced(state, worldIn, pos, Blocks.AIR.getDefaultState(), false);
        }
    }
}