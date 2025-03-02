package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class FlooCampfireBlock extends FlooMainTeleporterBase implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
    protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HORIZONTAL_FACING;

    public FlooCampfireBlock(int lightLevel) {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PODZOL)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .lightLevel(state -> lightLevel)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection());
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to randomTick and needsRandomTick, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, RandomSource rand) {
        int randomInt = rand.nextInt(10);
        if (randomInt < 2) {
            spawnSmokeParticles(level, pos);
        }
        if (randomInt == 0) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
        }
    }

    /**
     * Based on {@link CampfireBlock#dowse}
     */
    public static void extinguish(@Nullable Entity entity, LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        if (levelAccessor.isClientSide()) {
            for (int i = 0; i < 20; ++i) {
                spawnSmokeParticles((Level) levelAccessor, pos);
            }
        }
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor levelAccessor, @NotNull BlockPos pos, @NotNull BlockState state, FluidState fluidState) {
        if (fluidState.getType() == Fluids.WATER) {
            if (!levelAccessor.isClientSide()) {
                levelAccessor.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            extinguish(null, levelAccessor, pos, state);

            BlockState campfireBlockState = state.getBlock() == FloocraftBlocks.FLOO_SOUL_CAMPFIRE.get() ? Blocks.SOUL_CAMPFIRE.defaultBlockState() : Blocks.CAMPFIRE.defaultBlockState();
            levelAccessor.setBlock(pos, campfireBlockState
                            .setValue(CampfireBlock.LIT, false)
                            .setValue(WATERLOGGED, true),
                    3);
            levelAccessor.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(levelAccessor));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Based on {@link CampfireBlock#makeParticles}
     */
    public static void spawnSmokeParticles(Level level, BlockPos pos) {
        RandomSource randomsource = level.getRandom();
        SimpleParticleType simpleparticletype = ParticleTypes.CAMPFIRE_COSY_SMOKE;
        level.addAlwaysVisibleParticle(simpleparticletype, true, (double) pos.getX() + 0.5 + randomsource.nextDouble() / 3.0 * (double) (randomsource.nextBoolean() ? 1 : -1), (double) pos.getY() + randomsource.nextDouble() + randomsource.nextDouble(), (double) pos.getZ() + 0.5 + randomsource.nextDouble() / 3.0 * (double) (randomsource.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        level.addParticle(ParticleTypes.SMOKE, (double) pos.getX() + 0.5 + randomsource.nextDouble() / 4.0 * (double) (randomsource.nextBoolean() ? 1 : -1), (double) pos.getY() + 0.4, (double) pos.getZ() + 0.5 + randomsource.nextDouble() / 4.0 * (double) (randomsource.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     *
     * @deprecated Implementing/overriding is fine.
     */
    @Deprecated
    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     *
     * @deprecated Implementing/overriding is fine.
     */
    @Deprecated
    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    @Nonnull
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter blockGetter, @Nonnull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
}
