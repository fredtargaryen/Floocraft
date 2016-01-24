package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GreenFlamesBase extends Block {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 9);

    public GreenFlamesBase() {
        super(Material.fire);
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, AGE);
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
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }

    @Override
    public boolean isOpaqueCube(){return false;}

    @Override
    public boolean isFullCube(){return false;}

    @Override
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, BlockPos pos, IBlockState state, Entity par4Entity) {
        if (par4Entity instanceof EntityPlayer) {
            if (par1World.isRemote) {
                doClientGuiTings(pos);
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
        if (!isInFireplace(par1World, pos)) {
            par1World.setBlockState(pos, Blocks.fire.getDefaultState());
        } else {
            par1World.scheduleUpdate(pos, this, this.tickRate(par1World));
        }
    }

    @Override
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random) {
        if (!isInFireplace(par1World, pos) || par1World.getBlockState(pos).getValue(AGE).equals(0)) {
            par1World.setBlockState(pos, Blocks.fire.getDefaultState());
        } else {
            par1World.scheduleUpdate(pos, this, this.tickRate(par1World) + par5Random.nextInt(10));
        }
    }

    /**
     * ALL FIREPLACE VALIDATION CODE STARTS HERE
     */
    private int getTopBlockY(World w, BlockPos pos) {
        BlockPos newPos = pos.offset(EnumFacing.UP, 1);
        int y = newPos.getY();
        Block b = w.getBlockState(newPos).getBlock();
        while (b.isAir(w, newPos) && y < 256) {
            newPos = newPos.offset(EnumFacing.UP, 1);
            y = newPos.getY();
            b = w.getBlockState(newPos).getBlock();
        }
        //When y >= 256 you get an air block, so if b is a normal cube y is implicitly < 256
        if (b.isSolidFullCube()) return y;
        return 0;
    }

    private boolean isWallColumn(World w, BlockPos bottomPos, int topY) {
        boolean valid = true;
        BlockPos newBottomPos = bottomPos;
        while (valid && newBottomPos.getY() < topY) {
            if (w.getBlockState(newBottomPos).getBlock().isSolidFullCube()) {
                newBottomPos = newBottomPos.offset(EnumFacing.UP, 1);
            } else {
                valid = false;
            }
        }
        return valid;
    }

    private List<Integer> getWalls(World w, BlockPos bottomPos, int topY) {
        List<Integer> walls = new ArrayList<Integer>();
        if (this.isWallColumn(w, bottomPos.add(0.0D, 0.0D, 1.0D), topY)) {
            walls.add(2);
        }
        if (this.isWallColumn(w, bottomPos.add(-1.0D, 0.0D, 0.0D), topY)) {
            walls.add(4);
        }
        if (this.isWallColumn(w, bottomPos.add(1.0D, 0.0D, 0.0D), topY)) {
            walls.add(6);
        }
        if (this.isWallColumn(w, bottomPos.add(0.0D, 0.0D, -1.0D), topY)) {
            walls.add(8);
        }
        return walls;
    }

    private boolean canLoopToCorner(World w, int x, int y, int z, int backWall, int oldSideWall, int top) {
        int sideWall;
        int oldX = x;
        int oldZ = z;
        switch (oldSideWall) {
            case 2:
                sideWall = 8;
                break;
            case 4:
                sideWall = 6;
                break;
            case 6:
                sideWall = 4;
                break;
            case 8:
                sideWall = 2;
                break;
            default:
                return false;
        }
        boolean stop = false;
        while (!stop) {
            if (backWall == 2 || backWall == 8) {
                if (sideWall == 4) {
                    x--;
                } else {
                    x++;
                }
            } else {
                if (sideWall == 2) {
                    z++;
                } else {
                    z--;
                }
            }
            BlockPos newBottomPos = new BlockPos(x, y, z);
            int newTop = this.getTopBlockY(w, newBottomPos);
            List<Integer> walls = this.getWalls(w, newBottomPos, newTop);
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

    public boolean isInFireplace(World w, BlockPos pos)
    {
        if (pos.getY() < 254)
        {
            int t = this.getTopBlockY(w, pos);
            if (t > 0)
            {
                List<Integer> walls = this.getWalls(w, pos, t);
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                switch (walls.size())
                {
                    case 3:
                        return true;
                    case 2:
                        if ((walls.contains(2) && (walls.contains(4) || walls.contains(6))
                                || (walls.contains(8) && (walls.contains(4) || walls.contains(6))))) {
                            return this.canLoopToCorner(w, x, y, z, walls.get(0), walls.get(1), t)
                                    || this.canLoopToCorner(w, x, y, z, walls.get(1), walls.get(0), t);
                        }
                        break;
                    case 1:
                        switch (walls.get(0)) {
                            case 2:
                                return this.canLoopToCorner(w, x, y, z, 2, 4, t) && this.canLoopToCorner(w, x, y, z, 2, 6, t);
                            case 4:
                                return this.canLoopToCorner(w, x, y, z, 4, 8, t) && this.canLoopToCorner(w, x, y, z, 4, 2, t);
                            case 6:
                                return this.canLoopToCorner(w, x, y, z, 6, 8, t) && this.canLoopToCorner(w, x, y, z, 6, 2, t);
                            case 8:
                                return this.canLoopToCorner(w, x, y, z, 8, 4, t) && this.canLoopToCorner(w, x, y, z, 8, 6, t);
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return false;
    }
}