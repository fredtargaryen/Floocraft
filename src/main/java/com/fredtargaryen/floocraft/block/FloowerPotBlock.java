package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBlockEntityTypes;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftSounds;
import com.fredtargaryen.floocraft.blockentity.FloowerPotBlockEntity;
import com.fredtargaryen.floocraft.item.FlooPowderItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

import static com.fredtargaryen.floocraft.block.FlooFlamesBlock.BUSY;

public class FloowerPotBlock extends BaseEntityBlock {
    private static final VoxelShape POTBOX = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

    public FloowerPotBlock() {
        this(Block.Properties.of()
                .strength(0F));
    }

    public FloowerPotBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @Nonnull VoxelShape getShape(BlockState state, @Nonnull BlockGetter blockGetter, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return POTBOX;
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new FloowerPotBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.getType() != FloocraftBlockEntityTypes.FLOOWER_POT.get()) {
            return InteractionResult.FAIL;
        }
        //NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        Containers.dropContentsOnDestroy(pState, pNewState, pLevel, pPos);
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        super.tick(state, level, pos, rand);
        if (!level.isClientSide) {
            FloowerPotBlockEntity pot = (FloowerPotBlockEntity) level.getBlockEntity(pos);
            int hRange = pot.getHRange();
            int vRange = pot.getVRange();
            ItemStack stack = ItemStack.EMPTY; //pot.getStackInSlot(0);
            if (stack != null && stack.getCount() > 0) {
                int par2 = pos.getX();
                int par3 = pos.getY();
                int par4 = pos.getZ();
                BlockPos currentPos;
                BlockState currentState;
                Block currentBlock;
                FlooFlamesBlock flooFlames = FloocraftBlocks.FLOO_FLAMES.get();
                SoundEvent greened = FloocraftSounds.GREENED.get();
                for (int x = par2 - hRange; x <= par2 + hRange; x++) {
                    for (int y = par3 - vRange; y <= par3 + vRange; y++) {
                        for (int z = par4 - hRange; z <= par4 + hRange; z++) {
                            if (stack != null && stack.getCount() > 0) {
                                currentPos = new BlockPos(x, y, z);
                                currentState = level.getBlockState(currentPos);
                                currentBlock = currentState.getBlock();
                                if (currentBlock == Blocks.CAMPFIRE && currentState.getValue(BlockStateProperties.LIT)) {
                                    Item i = stack.getItem();
                                    level.setBlockAndUpdate(currentPos, FloocraftBlocks.FLOO_CAMPFIRE.get().defaultBlockState()
                                            .setValue(FlooMainTeleporterBase.TPS_REMAINING, (int) ((FlooPowderItem) i).getConcentration())
                                            .setValue(BlockStateProperties.HORIZONTAL_FACING, currentState.getValue(BlockStateProperties.HORIZONTAL_FACING)));
                                    level.playSound(null, currentPos, greened, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    stack = stack.getCount() == 1 ? ItemStack.EMPTY : stack.split(stack.getCount() - 1);
                                } else if (currentBlock == Blocks.SOUL_CAMPFIRE && currentState.getValue(BlockStateProperties.LIT)) {
                                    Item i = stack.getItem();
                                    level.setBlockAndUpdate(currentPos, FloocraftBlocks.FLOO_SOUL_CAMPFIRE.get().defaultBlockState()
                                            .setValue(FlooMainTeleporterBase.TPS_REMAINING, (int) ((FlooPowderItem) i).getConcentration())
                                            .setValue(BlockStateProperties.HORIZONTAL_FACING, currentState.getValue(BlockStateProperties.HORIZONTAL_FACING)));
                                    level.playSound(null, currentPos, greened, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    stack = stack.getCount() == 1 ? ItemStack.EMPTY : stack.split(stack.getCount() - 1);
                                } else if (currentState.is(BlockTags.FIRE)) {
                                    if (flooFlames.isInFireplace(level, currentPos) != null) {
                                        Item i = stack.getItem();
                                        boolean soul = SoulFireBlock.canSurviveOnBlock(level.getBlockState(currentPos.below()));
                                        level.setBlockAndUpdate(currentPos, flooFlames.defaultBlockState()
                                                .setValue(FlooFlamesBlock.TPS_REMAINING, (int) ((FlooPowderItem) i).getConcentration())
                                                .setValue(FlooFlamesBlock.COLOUR, soul)
                                                .setValue(FlooFlamesBlock.BEHAVIOUR, BUSY));
                                        level.playSound(null, currentPos, greened, SoundSource.BLOCKS, 1.0F, 1.0F);
                                        stack = stack.getCount() == 1 ? ItemStack.EMPTY : stack.split(stack.getCount() - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
//            pot.setInventorySlotContents(0, stack);
//            level.notifyBlockUpdate(pos, state, state, 3);
//            level.notifyNeighborsOfStateChange(pos, this);
//            level.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 40, TickPriority.EXTREMELY_LOW);
        }
    }

//    @Override
//    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean b) {
//        worldIn.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 40, TickPriority.EXTREMELY_LOW);
//    }

    /**
     * BaseEntityBlock doesn't render the block model by default, so this is needed
     */
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, pos
     */
//    @Override
//    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
//        return super.isValidPosition(state, worldIn, pos);
//    }

    /**
     * Called when a neighboring block changes.
     */
//    @Override
//    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
//        if (!this.isValidPosition(state, worldIn, pos)) {
//            this.onReplaced(state, worldIn, pos, Blocks.AIR.getDefaultState(), false);
//        }
//    }

//    @Override
//    public boolean canProvidePower(BlockState state) {
//        return true;
//    }

//    /**
//     * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess, BlockPos, EnumFacing)} whenever possible.
//     * Implementing/overriding is fine.
//     */
//    @Override
//    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
//        TileEntity te = blockAccess.getTileEntity(pos);
//        if (te instanceof FloowerPotTileEntity) {
//            FloowerPotTileEntity fpte = (FloowerPotTileEntity) te;
//            int flooCount = fpte.getStackInSlot(0).getCount();
//            if (flooCount == 64) {
//                return 0;
//            } else {
//                return (int) ((64 - flooCount) * 14 / 64f + 1);
//            }
//        }
//        return 0;
//    }

//    /**
//     * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess, BlockPos, EnumFacing)} whenever possible.
//     * Implementing/overriding is fine.
//     */
//    @Override
//    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
//        return 0;
//    }
}