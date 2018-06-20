package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class GreenFlamesBase extends Block {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 9);

    public GreenFlamesBase() {
        super(Material.FIRE);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (Integer)state.getValue(AGE);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, BlockPos pos, IBlockState state, Entity par4Entity) {
        if (par1World.isRemote) {
            if (par4Entity instanceof EntityPlayer) {
                doClientGuiTings(pos);
            }
        }
        else {
            //Server side. Players are dealt with on the client because of the GUI
            if(!(par4Entity instanceof EntityPlayer)) {
                boolean teleport = false;
                //Teleport destination one block outside the fire, instead of in the fire
                boolean landOutside = false;
                if(FloocraftBase.itemsTeleport && par4Entity instanceof EntityItem) {
                    teleport = true;
                    landOutside = true;
                }
                else if(    (FloocraftBase.villagersTeleport && par4Entity instanceof EntityVillager)
                        ||  (FloocraftBase.miscMobsTeleport && par4Entity instanceof EntityLiving)) {
                    teleport = par1World.rand.nextFloat() < 0.2;
                }
                if(teleport) {
                    //Get list of locations and whether they are available
                    MessageFireplaceList mfl = FloocraftWorldData.forWorld(par1World).assembleNewFireplaceList(par1World);
                    ArrayList<String> possibleLocations = new ArrayList<String>();
                    Iterator<Boolean> enabledIter = mfl.enabledList.iterator();
                    Iterator<String> nameIter = mfl.placeList.keySet().iterator();
                    String nextName;
                    Boolean nextEnabled;
                    //Add the enabled locations to possibleLocations
                    while(nameIter.hasNext()) {
                        nextName = nameIter.next();
                        nextEnabled = enabledIter.next();
                        if(nextEnabled) possibleLocations.add(nextName);
                    }
                    //Pick a random location from possibleLocations
                    String destName = possibleLocations.get(par1World.rand.nextInt(possibleLocations.size()));
                    //Teleport to that location
                    int[] coords = mfl.placeList.get(destName);
                    if(landOutside) {
                        BlockPos dest = new BlockPos(coords[0], coords[1], coords[2]);
                        dest = dest.offset(this.isInFireplace(par1World, dest));
                        par4Entity.setLocationAndAngles(
                                dest.getX(), coords[1], dest.getZ(), par4Entity.rotationYaw, par4Entity.rotationPitch);
                    }
                    else {
                        par4Entity.setLocationAndAngles(
                                coords[0], coords[1], coords[2], par4Entity.rotationYaw, par4Entity.rotationPitch);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void doClientGuiTings(BlockPos pos) {
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
        if (Minecraft.getMinecraft().currentScreen == null && !proxy.overrideTicker.isOverriding()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiTeleport(pos.getX(), pos.getY(), pos.getZ()));
            proxy.overrideTicker.start();
        }
    }

    @Override
    public int tickRate(World par1World) {
        return 30;
    }

    @Override
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state) {
        if (isInFireplace(par1World, pos) != null) {
            par1World.scheduleUpdate(pos, this, this.tickRate(par1World));
        } else {
            par1World.setBlockState(pos, Blocks.FIRE.getDefaultState());
        }
    }

    @Override
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random) {
        if (isInFireplace(par1World, pos) == null || par1World.getBlockState(pos).getValue(AGE).equals(0)) {
            par1World.setBlockState(pos, Blocks.FIRE.getDefaultState());
        } else {
            par1World.scheduleUpdate(pos, this, this.tickRate(par1World) + par5Random.nextInt(10));
        }
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return false;
    }

    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean lolidkwhatthisis)
    {}

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
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
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    ////////////////////////
    //ALBEDO COMPATIBILITY//
    ////////////////////////
    @Override
    public boolean hasTileEntity(IBlockState ibs)
    {
        return FloocraftBase.isAlbedoInstalled();
    }

    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state) throws NullPointerException;

    ////////////////////////
    //FIREPLACE VALIDATION//
    ////////////////////////
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
        List<EnumFacing> walls = new ArrayList<EnumFacing>();
        if(this.isWallColumn(w, bottomPos.offset(EnumFacing.NORTH), topY)) {
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
                    z++;
                } else {
                    z--;
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
                        for(EnumFacing ef : EnumFacing.HORIZONTALS) {
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
                            default:
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
}