package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import com.fredtargaryen.floocraft.config.GeneralConfig;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.state.properties.BlockStateProperties.AGE_0_15;

public abstract class GreenFlamesBase extends Block {
    private static final EnumFacing[] HORIZONTALS = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST };
    private static final VoxelShape TALLBOX = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);

    GreenFlamesBase(int lightLevel) { super(Properties.create(Material.FIRE).lightValue(lightLevel)); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
    {
        builder.add(AGE_0_15);
    }

    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) { return TALLBOX; }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isRemote) {
            if (entityIn == Minecraft.getInstance().player) {
                doClientGuiTings(pos);
            }
        }
        else {
            //Server side. Players are dealt with on the client because of the GUI
            if(!(entityIn instanceof EntityPlayer)) {
                boolean teleport = false;
                //Set teleport destination one block outside the fire, instead of in the fire
                boolean landOutside = false;

                if(GeneralConfig.ITEMS_TELEPORT.get() && entityIn instanceof EntityItem) {
                    teleport = true;
                    landOutside = true;
                }
                else if(    (GeneralConfig.VILLAGERS_TELEPORT.get() && entityIn instanceof EntityVillager)
                          ||(GeneralConfig.MISC_MOBS_TELEPORT.get() && entityIn instanceof EntityLiving)) {
                    teleport = worldIn.rand.nextFloat() < 0.2;
                }
                if(teleport) {
                    //Get list of locations and whether they are available
                    FloocraftWorldData fwd = FloocraftWorldData.forWorld(worldIn);
                    MessageFireplaceList mfl = fwd.assembleNewFireplaceList(worldIn);
                    ArrayList<String> possibleLocations = new ArrayList<>();
                    //Add the enabled locations to possibleLocations
                    for(int i = 0; i < mfl.places.length; ++i) {
                        if(mfl.enabledList[i]) possibleLocations.add((String) mfl.places[i]);
                    }
                    //Pick a random location from possibleLocations
                    int destNo = worldIn.rand.nextInt(possibleLocations.size());
                    //Teleport to that location
                    String destName = possibleLocations.get(destNo);
                    //Get location coords
                    int[] coords = fwd.placeList.get(destName);
                    BlockPos dest = new BlockPos(coords[0], coords[1], coords[2]);
                    //Set a temporary Floo fire here
                    if(worldIn.getBlockState(dest).getBlock() == Blocks.FIRE) {
                        worldIn.setBlockState(dest, FloocraftBase.GREEN_FLAMES_TEMP.getDefaultState());
                    }
                    if(landOutside) {
                        dest = dest.offset(this.isInFireplace(worldIn, dest));
                        entityIn.setLocationAndAngles(
                                dest.getX(), coords[1], dest.getZ(), entityIn.rotationYaw, entityIn.rotationPitch);
                    }
                    else {
                        entityIn.setLocationAndAngles(
                                   coords[0], coords[1], coords[2], entityIn.rotationYaw, entityIn.rotationPitch);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientGuiTings(BlockPos pos) {
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
        if (Minecraft.getInstance().currentScreen == null && !proxy.overrideTicker.isOverriding()) {
            Minecraft.getInstance().displayGuiScreen(new GuiTeleport(pos.getX(), pos.getY(), pos.getZ()));
            proxy.overrideTicker.start();
        }
    }

    @Override
    public int tickRate(IWorldReaderBase par1World)
    {
        return 30;
    }

    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
        if (isInFireplace(worldIn, pos) != null) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
        } else {
            worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
        }
    }

    @Override
    public void tick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (isInFireplace(world, pos) == null || world.getBlockState(pos).get(AGE_0_15).equals(0)) {
            world.setBlockState(pos, Blocks.FIRE.getDefaultState());
        } else {
            world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world) + rand.nextInt(10));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (rand.nextInt(24) == 0)
        {
            worldIn.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
        }
        for (int i = 0; i < 3; ++i)
        {
            double d0 = (double)pos.getX() + rand.nextDouble();
            double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
            double d2 = (double)pos.getZ() + rand.nextDouble();
            worldIn.spawnParticle(Particles.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    //FIREPLACE VALIDATION CODE STARTS HERE
    private int getTopBlockY(World w, BlockPos pos) {
        BlockPos newPos = pos.offset(EnumFacing.UP, 1);
        int y = newPos.getY();
        IBlockState bs = w.getBlockState(newPos);
        while (bs.getBlock().isAir(bs, w, newPos) && y < 256) {
            newPos = newPos.offset(EnumFacing.UP, 1);
            y = newPos.getY();
            bs = w.getBlockState(newPos);
        }
        //When y >= 256 you get an air block, so if b is a solid cube y is implicitly < 256
        if (bs.getBlock().getMaterial(bs).isSolid()) return y;
        return 0;
    }

    private boolean isWallColumn(World w, BlockPos bottomPos, int topY) {
        boolean valid = true;
        BlockPos newBottomPos = bottomPos;
        while (valid && newBottomPos.getY() < topY) {
            IBlockState bs = w.getBlockState(newBottomPos);
            if (bs.getBlock().getMaterial(bs).isSolid()) {
                newBottomPos = newBottomPos.offset(EnumFacing.UP, 1);
            } else {
                valid = false;
            }
        }
        return valid;
    }

    private List<EnumFacing> getWalls(World w, BlockPos bottomPos, int topY) {
        List<EnumFacing> walls = new ArrayList<>();
        if (this.isWallColumn(w, bottomPos.offset(EnumFacing.NORTH), topY)) {
            walls.add(EnumFacing.NORTH);
        }
        if (this.isWallColumn(w, bottomPos.offset(EnumFacing.WEST), topY)) {
            walls.add(EnumFacing.WEST);
        }
        if (this.isWallColumn(w, bottomPos.offset(EnumFacing.EAST), topY)) {
            walls.add(EnumFacing.EAST);
        }
        if (this.isWallColumn(w, bottomPos.offset(EnumFacing.SOUTH), topY)) {
            walls.add(EnumFacing.SOUTH);
        }
        return walls;
    }

    private boolean canLoopToCorner(World w, int x, int y, int z, EnumFacing backWall, EnumFacing oldSideWall, int top) {
        int oldX = x;
        int oldZ = z;
        EnumFacing sideWall = oldSideWall.getOpposite();
        boolean stop = false;
        while (!stop) {
            if (backWall == EnumFacing.NORTH || backWall == EnumFacing.SOUTH) {
                if (sideWall == EnumFacing.WEST) {
                    x--;
                } else {
                    x++;
                }
            } else {
                if (sideWall == EnumFacing.NORTH) {
                    z--;
                } else {
                    z++;
                }
            }
            BlockPos newBottomPos = new BlockPos(x, y, z);
            int newTop = this.getTopBlockY(w, newBottomPos);
            List<EnumFacing> walls = this.getWalls(w, newBottomPos, newTop);
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
     * Returns the EnumFacing that points out of the fireplace, if the fireplace is valid.
     * Not necessarily the direction of the sign.
     * If the fireplace is invalid, returns null.
     * @return EnumFacing.UP if the fire block is in a corner of a valid fireplace; NORTH, SOUTH, EAST, WEST if the fire
     * is in a valid fireplace but not a corner; null if the fireplace is invalid
     */
    public EnumFacing isInFireplace(World w, BlockPos pos)
    {
        if (pos.getY() < 254)
        {
            int t = this.getTopBlockY(w, pos);
            if (t > 0)
            {
                List<EnumFacing> walls = this.getWalls(w, pos, t);
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                switch (walls.size())
                {
                    case 3:
                        //One-block-long fireplace
                        for(EnumFacing ef : HORIZONTALS) {
                            if(!walls.contains(ef)) return ef;
                        }
                        break;
                    case 2:
                        if ((walls.contains(EnumFacing.NORTH) && (walls.contains(EnumFacing.WEST) || walls.contains(EnumFacing.EAST))
                                || (walls.contains(EnumFacing.SOUTH) && (walls.contains(EnumFacing.WEST) || walls.contains(EnumFacing.EAST))))) {
                            boolean zeroToOne = this.canLoopToCorner(w, x, y, z, walls.get(0), walls.get(1), t);
                            boolean oneToZero = this.canLoopToCorner(w, x, y, z, walls.get(1), walls.get(0), t);
                            if(zeroToOne && oneToZero) {
                                //Fire is in corner of fireplace. Valid fireplace, but can't put a sign on a corner, so
                                //return UP
                                return EnumFacing.UP;
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
                                if      (this.canLoopToCorner(w, x, y, z, EnumFacing.NORTH, EnumFacing.WEST, t)
                                        &&  this.canLoopToCorner(w, x, y, z, EnumFacing.NORTH, EnumFacing.EAST, t))
                                    return EnumFacing.SOUTH;
                            case WEST:
                                if      (this.canLoopToCorner(w, x, y, z, EnumFacing.WEST, EnumFacing.SOUTH, t)
                                        &&  this.canLoopToCorner(w, x, y, z, EnumFacing.WEST, EnumFacing.NORTH, t))
                                    return EnumFacing.EAST;
                            case EAST:
                                if      (this.canLoopToCorner(w, x, y, z, EnumFacing.EAST, EnumFacing.SOUTH, t)
                                        &&  this.canLoopToCorner(w, x, y, z, EnumFacing.EAST, EnumFacing.NORTH, t))
                                    return EnumFacing.WEST;
                            case SOUTH:
                                if      (this.canLoopToCorner(w, x, y, z, EnumFacing.SOUTH, EnumFacing.WEST, t)
                                        &&  this.canLoopToCorner(w, x, y, z, EnumFacing.SOUTH, EnumFacing.EAST, t))
                                    return EnumFacing.NORTH;
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
    @Deprecated
    public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    ////////////////////////
    //MIRAGE COMPATIBILITY//
    ////////////////////////
//    @Override
//    public final boolean hasTileEntity(IBlockState ibs)
//    {
//        return FloocraftBase.isMirageInstalled();
//    }
//
//    @Override
//    public abstract TileEntity createTileEntity(World world, IBlockState state);
}