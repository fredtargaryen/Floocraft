package com.fredtargaryen.floocraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.state.properties.BlockStateProperties.AGE_0_15;

public abstract class FlooFlamesBase extends FlooMainTeleporterBase {
    private static final Direction[] HORIZONTALS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    private static final VoxelShape TALLBOX = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);

    FlooFlamesBase(int lightLevel) { super(Properties.create(Material.FIRE).setLightLevel(state -> lightLevel)); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AGE_0_15);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) { return TALLBOX; }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean b) {
        if (isInFireplace(worldIn, pos) != null) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 30);
        } else {
            boolean soul = SoulFireBlock.shouldLightSoulFire(worldIn.getBlockState(pos.down()).getBlock());
            worldIn.setBlockState(pos, (soul ? Blocks.SOUL_FIRE : Blocks.FIRE).getDefaultState());
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (isInFireplace(world, pos) == null || world.getBlockState(pos).get(AGE_0_15).equals(0)) {
            boolean soul = SoulFireBlock.shouldLightSoulFire(world.getBlockState(pos.down()).getBlock());
            world.setBlockState(pos, (soul ? Blocks.SOUL_FIRE : Blocks.FIRE).getDefaultState());
        } else {
            world.getPendingBlockTicks().scheduleTick(pos, this, 30 + rand.nextInt(10));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(24) == 0) {
            worldIn.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }
        this.doSmokeParticles(stateIn, worldIn, pos, rand);
    }

    @OnlyIn(Dist.CLIENT)
    protected void doSmokeParticles(BlockState stateIn, World world, BlockPos pos, Random rand) {
        if(rand.nextInt(8) == 0) {
            double d0 = (double)pos.getX() + rand.nextDouble();
            double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double)pos.getZ() + rand.nextDouble();
            world.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean isTransparent(BlockState state)
    {
        return true;
    }

    //FIREPLACE VALIDATION CODE STARTS HERE
    private int getTopBlockY(World w, BlockPos pos) {
        BlockPos newPos = pos.offset(Direction.UP, 1);
        int y = newPos.getY();
        BlockState bs = w.getBlockState(newPos);
        while (bs.getBlock().isAir(bs, w, newPos) && y < 256) {
            newPos = newPos.offset(Direction.UP, 1);
            y = newPos.getY();
            bs = w.getBlockState(newPos);
        }
        //When y >= 256 you get an air block, so if b is a solid cube y is implicitly < 256
        if (bs.getMaterial().isSolid()) return y;
        return 0;
    }

    private boolean isWallColumn(World w, BlockPos bottomPos, int topY) {
        boolean valid = true;
        BlockPos newBottomPos = bottomPos;
        while (valid && newBottomPos.getY() < topY) {
            BlockState bs = w.getBlockState(newBottomPos);
            if (bs.getMaterial().isSolid()) {
                newBottomPos = newBottomPos.offset(Direction.UP, 1);
            } else {
                valid = false;
            }
        }
        return valid;
    }

    private List<Direction> getWalls(World w, BlockPos bottomPos, int topY) {
        List<Direction> walls = new ArrayList<>();
        if (this.isWallColumn(w, bottomPos.offset(Direction.NORTH), topY)) {
            walls.add(Direction.NORTH);
        }
        if (this.isWallColumn(w, bottomPos.offset(Direction.WEST), topY)) {
            walls.add(Direction.WEST);
        }
        if (this.isWallColumn(w, bottomPos.offset(Direction.EAST), topY)) {
            walls.add(Direction.EAST);
        }
        if (this.isWallColumn(w, bottomPos.offset(Direction.SOUTH), topY)) {
            walls.add(Direction.SOUTH);
        }
        return walls;
    }

    private boolean canLoopToCorner(World w, int x, int y, int z, Direction backWall, Direction oldSideWall, int top) {
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
            int newTop = this.getTopBlockY(w, newBottomPos);
            List<Direction> walls = this.getWalls(w, newBottomPos, newTop);
            switch (walls.size()) {
                case 1:
                    if (!walls.contains(backWall)) {
                        return false;
                    } else {
                        if (newTop > top++) {
                            if (!this.isWallColumn(w, new BlockPos(oldX, top, oldZ), newTop)) {
                                return false;
                            }
                        } else if (newTop < top--) {
                            if (!this.isWallColumn(w, new BlockPos(x, newTop, z), top)) {
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
     * @return Direction.UP if the fire block is in a corner of a valid fireplace; NORTH, SOUTH, EAST, WEST if the fire
     * is in a valid fireplace but not a corner; null if the fireplace is invalid
     */
    public Direction isInFireplace(World w, BlockPos pos) {
        if (pos.getY() < 254) {
            int t = this.getTopBlockY(w, pos);
            if (t > 0) {
                List<Direction> walls = this.getWalls(w, pos, t);
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                switch (walls.size()) {
                    case 3:
                        //One-block-long fireplace
                        for(Direction ef : HORIZONTALS) {
                            if(!walls.contains(ef)) return ef;
                        }
                        break;
                    case 2:
                        if ((walls.contains(Direction.NORTH) && (walls.contains(Direction.WEST) || walls.contains(Direction.EAST))
                                || (walls.contains(Direction.SOUTH) && (walls.contains(Direction.WEST) || walls.contains(Direction.EAST))))) {
                            boolean zeroToOne = this.canLoopToCorner(w, x, y, z, walls.get(0), walls.get(1), t);
                            boolean oneToZero = this.canLoopToCorner(w, x, y, z, walls.get(1), walls.get(0), t);
                            if(zeroToOne && oneToZero) {
                                //Fire is in corner of fireplace. Valid fireplace, but can't put a sign on a corner, so
                                //return UP
                                return Direction.UP;
                            }
                            else if(zeroToOne) {
                                //End of a long fireplace
                                //Wall 0 is the back of the fireplace
                                return walls.get(0).getOpposite();
                            }
                            else if(oneToZero) {
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
                                if      (this.canLoopToCorner(w, x, y, z, Direction.NORTH, Direction.WEST, t)
                                        &&  this.canLoopToCorner(w, x, y, z, Direction.NORTH, Direction.EAST, t))
                                    return Direction.SOUTH;
                            case WEST:
                                if      (this.canLoopToCorner(w, x, y, z, Direction.WEST, Direction.SOUTH, t)
                                        &&  this.canLoopToCorner(w, x, y, z, Direction.WEST, Direction.NORTH, t))
                                    return Direction.EAST;
                            case EAST:
                                if      (this.canLoopToCorner(w, x, y, z, Direction.EAST, Direction.SOUTH, t)
                                        &&  this.canLoopToCorner(w, x, y, z, Direction.EAST, Direction.NORTH, t))
                                    return Direction.WEST;
                            case SOUTH:
                                if      (this.canLoopToCorner(w, x, y, z, Direction.SOUTH, Direction.WEST, t)
                                        &&  this.canLoopToCorner(w, x, y, z, Direction.SOUTH, Direction.EAST, t))
                                    return Direction.NORTH;
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return null;
    }

    //FOR ALLOWING COLLISIONS TO HAPPEN
    @Override
    @Nonnull
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }
}