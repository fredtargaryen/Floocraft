package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FlooFlamesBlock extends FlooMainTeleporterBase {
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private static final VoxelShape BOX = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    /**
     * The fire "behaviour". 0 for temporary; 1 for idle; 2 for busy
     */
    public static IntegerProperty BEHAVIOUR;
    public static final int TEMP = 0;
    public static final int IDLE = 1;
    public static final int BUSY = 2;

    static {
        // We are saving properties by just making aliases for existing properties which use the values we want...
        // so careful to use the below property names in the resource files!
        BEHAVIOUR = BlockStateProperties.AGE_2; // "age"
    }

    public FlooFlamesBlock() {
        super(Block.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, DataReference.getResourceLocation(FloocraftBlocks.FLOO_FLAMES_RL)))
                .lightLevel(state -> {
                    if (state.getValue(COLOUR) == STANDARD) {
                        if (state.getValue(BEHAVIOUR) == IDLE) return 12;
                        return 15;
                    } else {
                        if (state.getValue(BEHAVIOUR) == IDLE) return 7;
                        return 10;
                    }
                })
                .replaceable()
                .sound(SoundType.EMPTY)
                .noTerrainParticles());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BEHAVIOUR);
    }

    @Override
    protected @Nonnull VoxelShape getShape(BlockState state, @Nonnull BlockGetter blockGetter, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return BOX;
    }

    @Override
    protected void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean whatIsThis) {
        if (isInFireplace(level, pos) != null) {
            level.scheduleTick(pos, this, this.getTimeToFirstTick());
        } else {
            boolean soul = SoulFireBlock.canSurviveOnBlock(level.getBlockState(pos.below()));
            level.setBlockAndUpdate(pos, (soul ? Blocks.SOUL_FIRE : Blocks.FIRE).defaultBlockState());
        }
    }

    @Override
    protected BlockState updateShape(
            BlockState myState,
            LevelReader reader,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighbourPos,
            BlockState neighbouringState,
            RandomSource source
    ) {
        return direction == Direction.DOWN && !this.canSurvive(myState, reader, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(myState, reader, tickAccess, pos, direction, neighbourPos, neighbouringState, source);
    }

    @Override
    protected void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
        switch (state.getValue(BEHAVIOUR)) {
            case BUSY:
                if (level.getNearestPlayer((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, DataReference.FLOO_FIRE_DETECTION_RANGE, false) == null) {
                    boolean colour = SoulFireBlock.canSurviveOnBlock(level.getBlockState(pos.below())) ? SOUL : STANDARD;
                    level.setBlockAndUpdate(pos, state
                            .setValue(COLOUR, colour)
                            .setValue(BEHAVIOUR, IDLE));
                }
                super.tick(state, level, pos, rand);
                break;
            case IDLE:
                if (level.getNearestPlayer((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, DataReference.FLOO_FIRE_DETECTION_RANGE, false) != null) {
                    boolean colour = SoulFireBlock.canSurviveOnBlock(level.getBlockState(pos.below())) ? SOUL : STANDARD;
                    level.setBlockAndUpdate(pos, state
                            .setValue(COLOUR, colour)
                            .setValue(BEHAVIOUR, BUSY));
                }
                super.tick(state, level, pos, rand);
                break;
            default: // TEMP
                Block fireBlock = SoulFireBlock.canSurviveOnBlock(level.getBlockState(pos.below())) ? Blocks.SOUL_FIRE : Blocks.FIRE;
                level.setBlockAndUpdate(pos, fireBlock.defaultBlockState());
                break;
        }
        if (state.getValue(BEHAVIOUR) != TEMP) {
            if (isInFireplace(level, pos) == null) {
                boolean soul = SoulFireBlock.canSurviveOnBlock(level.getBlockState(pos.below()));
                level.setBlockAndUpdate(pos, (soul ? Blocks.SOUL_FIRE : Blocks.FIRE).defaultBlockState());
            } else {
                level.scheduleTick(pos, this, 30 + rand.nextInt(10));
            }
        }
    }

    @Override
    public boolean canDepartFromBlock(BlockState state) {
        return state.getValue(BEHAVIOUR) != TEMP;
    }

    @Override
    protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.below();
        return pLevel.getBlockState(blockpos).isFaceSturdy(pLevel, blockpos, Direction.UP);
    }

    // Client effects

    @Override
    protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
    }

    @Override
    public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, RandomSource rand) {
        if (rand.nextInt(24) == 0) {
            level.playLocalSound(pos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }
        this.doSmokeParticles(state, level, pos, rand);
    }

    protected void doSmokeParticles(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, RandomSource rand) {
        if (rand.nextInt(8) == 0) {
            double d0 = (double) pos.getX() + rand.nextDouble();
            double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double) pos.getZ() + rand.nextDouble();
            level.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        if (level.isClientSide()) {
            Minecraft.getInstance().level.playLocalSound(pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1f, 1f, true);
        }
    }

    /**
     * When placed, when should the next tick start?
     *
     * @return the number of ticks to wait before the first tick
     */
    protected int getTimeToFirstTick() {
        return 30;
    }

    //FIREPLACE VALIDATION CODE STARTS HERE

    /**
     * Find the first solid block above this one.
     * N.B. the method assumes this is being called from a Floo fire block's position
     *
     * @param level the level this fire block is in
     * @param pos   the position of this fire block
     * @return the Y position of the first solid block above this one.
     * Return Integer.MAX_VALUE if the max build height is reached without
     * finding a solid block
     */
    private int getTopBlockY(Level level, BlockPos pos) {
        int maxY = level.getMaxY();
        BlockPos nextPos = pos.above();
        int y = nextPos.getY();
        BlockState bs;
        while (y <= maxY) {
            bs = level.getBlockState(nextPos);
            if (!bs.canBeReplaced()) return y;
            nextPos = nextPos.above();
            y++;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Check for an uninterrupted column of solid blocks.
     *
     * @param level     the level this block is in
     * @param bottomPos the position of the bottom of the column
     * @param topY      the Y position of the block above where the fire would be
     * @return if there was an unbroken column from bottomPos.getY() to topY
     */
    private boolean isWallColumn(Level level, BlockPos bottomPos, int topY) {
        BlockPos nextPos = bottomPos;
        int y = nextPos.getY();
        BlockState bs;
        while (y < topY) {
            bs = level.getBlockState(nextPos);
            if (bs.canBeReplaced()) return false;
            nextPos = nextPos.above();
            y++;
        }
        return true;
    }

    private List<Direction> getWalls(Level level, BlockPos bottomPos, int topY) {
        List<Direction> walls = new ArrayList<>();
        for (Direction d : HORIZONTALS) {
            if (this.isWallColumn(level, bottomPos.relative(d), topY)) {
                walls.add(d);
            }
        }
        return walls;
    }

    private boolean canLoopToCorner(Level level, int x, int y, int z, Direction backWall, Direction oldSideWall, int top) {
        int oldX = x;
        int oldZ = z;
        Direction sideWall = oldSideWall.getOpposite();
        boolean stop = false;
        while (!stop) {
            if (backWall == Direction.NORTH || backWall == Direction.SOUTH) {
                if (sideWall == Direction.WEST) {
                    x--;
                } else {
                    x++;
                }
            } else {
                if (sideWall == Direction.NORTH) {
                    z--;
                } else {
                    z++;
                }
            }
            BlockPos newBottomPos = new BlockPos(x, y, z);
            int newTop = this.getTopBlockY(level, newBottomPos);
            List<Direction> walls = this.getWalls(level, newBottomPos, newTop);
            switch (walls.size()) {
                case 1:
                    if (!walls.contains(backWall)) {
                        return false;
                    } else {
                        if (newTop > top++) {
                            if (!this.isWallColumn(level, new BlockPos(oldX, top, oldZ), newTop)) {
                                return false;
                            }
                        } else if (newTop < top--) {
                            if (!this.isWallColumn(level, new BlockPos(x, newTop, z), top)) {
                                return false;
                            }
                        }
                        oldX = x;
                        top = newTop;
                        oldZ = z;
                    }
                    break;
                case 2:
                    if (walls.contains(backWall) && walls.contains(sideWall)) {
                        stop = true;
                    } else {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns the Direction that points out of the fireplace, if the fireplace is valid.
     * Not necessarily the direction of the sign.
     * If the fireplace is invalid, returns null.
     *
     * @param level The level the flames block is in
     * @param pos   The position of the flames block
     * @return Direction.UP if the fire block is in a corner of a valid fireplace; NORTH, SOUTH, EAST, WEST if the fire
     * is in a valid fireplace but not a corner; null if the fireplace is invalid
     */
    public Direction isInFireplace(Level level, BlockPos pos) {
        int topY = level.getMaxY();
        // Above max build height - 2, you can't build a fireplace so it can't be in one
        if (pos.getY() >= topY - 1) return null;
        int t = this.getTopBlockY(level, pos);
        // This means there is no appropriate solid block above the fire so the fireplace isn't valid
        if (t == Integer.MAX_VALUE) return null;
        List<Direction> walls = this.getWalls(level, pos, t);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        switch (walls.size()) {
            case 3:
                //One-block-long fireplace
                for (Direction ef : HORIZONTALS) {
                    if (!walls.contains(ef)) return ef;
                }
                break;
            case 2:
                if ((walls.contains(Direction.NORTH) && (walls.contains(Direction.WEST) || walls.contains(Direction.EAST))
                        || (walls.contains(Direction.SOUTH) && (walls.contains(Direction.WEST) || walls.contains(Direction.EAST))))) {
                    boolean zeroToOne = this.canLoopToCorner(level, x, y, z, walls.get(0), walls.get(1), t);
                    boolean oneToZero = this.canLoopToCorner(level, x, y, z, walls.get(1), walls.get(0), t);
                    if (zeroToOne && oneToZero) {
                        //Fire is in corner of fireplace. Valid fireplace, but can't put a sign on a corner, so
                        //return UP
                        return Direction.UP;
                    } else if (zeroToOne) {
                        //End of a long fireplace
                        //Wall 0 is the back of the fireplace
                        return walls.get(0).getOpposite();
                    } else if (oneToZero) {
                        //End of a long fireplace
                        //Wall 1 is the back of the fireplace
                        return walls.get(1).getOpposite();
                    }
                }
                break;
            case 1:
                switch (walls.get(0)) {
                    //This will be the back wall. If valid, this is the middle of a long fireplace
                    case NORTH:
                        if (this.canLoopToCorner(level, x, y, z, Direction.NORTH, Direction.WEST, t)
                                && this.canLoopToCorner(level, x, y, z, Direction.NORTH, Direction.EAST, t))
                            return Direction.SOUTH;
                    case WEST:
                        if (this.canLoopToCorner(level, x, y, z, Direction.WEST, Direction.SOUTH, t)
                                && this.canLoopToCorner(level, x, y, z, Direction.WEST, Direction.NORTH, t))
                            return Direction.EAST;
                    case EAST:
                        if (this.canLoopToCorner(level, x, y, z, Direction.EAST, Direction.SOUTH, t)
                                && this.canLoopToCorner(level, x, y, z, Direction.EAST, Direction.NORTH, t))
                            return Direction.WEST;
                    case SOUTH:
                        if (this.canLoopToCorner(level, x, y, z, Direction.SOUTH, Direction.WEST, t)
                                && this.canLoopToCorner(level, x, y, z, Direction.SOUTH, Direction.EAST, t))
                            return Direction.NORTH;
                        break;
                }
                break;
            default:
                break;
        }
        return null;
    }
}