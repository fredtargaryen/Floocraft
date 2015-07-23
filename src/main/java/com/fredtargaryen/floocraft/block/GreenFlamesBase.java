package com.fredtargaryen.floocraft.block;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.client.gui.GuiTeleport;
import com.fredtargaryen.floocraft.proxy.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GreenFlamesBase extends Block
{
    protected int renderID;

    public GreenFlamesBase(int renderID)
    {
        super(Material.fire);
        this.renderID = renderID;
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return this.blockIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister i)
    {
        this.blockIcon = i.registerIcon(DataReference.MODID+":fire");
    }

    @Override
	public boolean isNormalCube(IBlockAccess w, int x, int y, int z)
	{
		return false;
	}

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		if(par5Entity instanceof EntityPlayer)
		{
			if(par1World.isRemote)
			{
				doClientGuiTings(par2, par3, par4);
			}
		}
	}
		
	@SideOnly(Side.CLIENT)
    private void doClientGuiTings(int x, int y, int z)
	{
        ClientProxy proxy = (ClientProxy) FloocraftBase.proxy;
		if(Minecraft.getMinecraft().currentScreen == null && !proxy.overrideTicker.isOverriding())
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiTeleport(x, y, z));
			proxy.overrideTicker.start();
		}
	}

    @Override
	public int tickRate(World par1World)
	{
		return 30;
	}

    @Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4)
	{
		if (!isInFireplace(par1World, par2, par3, par4))
		{
			par1World.setBlock(par2, par3, par4, Blocks.fire);
		}
		else
		{
			par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World));
		}
	}

    @Override
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if(!isInFireplace(par1World, par2, par3, par4) || par1World.getBlockMetadata(par2, par3, par4) == 0)
        {
            par1World.setBlock(par2, par3, par4, Blocks.fire);
        }
        else {
            par1World.scheduleBlockUpdate(par2, par3, par4, this, this.tickRate(par1World) + par5Random.nextInt(10));
        }
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    @Override
    public int getRenderType(){return this.renderID;}

    /**
     * ALL FIREPLACE VALIDATION CODE STARTS HERE
     */
    //The source of all my problems!
    protected int getTopBlockY(World w, int x, int y, int z)
    {
        int t = y;
        Block b0 = w.getBlock(x, ++t, z);
        while (w.getBlock(x, t, z) == Blocks.air && t < 256)
        {
            ++t;
        }
        if (w.getBlock(x, t, z).isNormalCube(w, x, t, z)) {
            return t;
        }
        return 0;
    }

    private boolean isWallColumn(World w, int x, int bottomY, int topY, int z) {
        if (topY == 0) {
            return false;
        } else {
            boolean valid = true;
            while (valid && bottomY < topY) {
                if(w.getBlock(x, bottomY, z).isNormalCube(w, x, bottomY, z)) {
                    ++bottomY;
                }
                else {
                    valid = false;
                }
            }
            return valid;
        }
    }

    private List<Integer> getWalls(World w, int x, int bottomY, int topY, int z)
    {
        List<Integer> walls = new ArrayList<Integer>();
        if(this.isWallColumn(w, x, bottomY, topY, z + 1))
        {
            walls.add(2);
        }
        if(this.isWallColumn(w, x - 1, bottomY, topY, z))
        {
            walls.add(4);
        }
        if(this.isWallColumn(w, x + 1, bottomY, topY, z))
        {
            walls.add(6);
        }
        if(this.isWallColumn(w, x, bottomY, topY, z - 1))
        {
            walls.add(8);
        }
        return walls;
    }

    private boolean canLoopToCorner(World w, int x, int y, int z, int backWall, int oldSideWall, int top)
    {
        int sideWall;
        int oldX = x;
        int oldZ = z;
        switch(oldSideWall)
        {
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
        while(!stop)
        {
            if(backWall == 2 || backWall == 8)
            {
                if(sideWall == 4){--x;}
                else{++x;}
            }
            else
            {
                if(sideWall == 2){++z;}
                else{--z;}
            }
            int newTop = this.getTopBlockY(w, x, y, z);
            List<Integer> walls = this.getWalls(w, x, y, newTop, z);
            switch(walls.size())
            {
                case 1:
                    if(!walls.contains(backWall))
                    {
                        return false;
                    }
                    else
                    {
                        if(newTop > top + 1)
                        {
                            if(!this.isWallColumn(w, oldX, top, newTop, oldZ))
                            {
                                return false;
                            }
                        }
                        else if(newTop < top - 1)
                        {
                            if(!this.isWallColumn(w, x, newTop, top, z))
                            {
                                return false;
                            }
                        }
                        oldX = x;
                        top = newTop;
                        oldZ = z;
                    }
                    break;
                case 2:
                    if(walls.contains(backWall) && walls.contains(sideWall))
                    {
                        stop = true;
                    }else{return false;}
                    break;
                default:return false;
            }
        }
        return true;
    }

    public boolean isInFireplace(World w, int x, int y, int z)
    {
        //DELETE ALL SYSTEM.OUT.PRINTLNS
        if(!w.canBlockSeeTheSky(x, y, z))
        {
            if(y < 254 && x < 30000000 && x > -30000000 && z < 30000000 && z > -30000000)
            {
                int t = this.getTopBlockY(w, x, y, z);
                //START
                if(t == 0) System.out.println("Couldn't get top Y!");
                //END
                List<Integer> walls = this.getWalls(w, x, y, t, z);
                System.out.println(walls);
                switch(walls.size())
                {
                    case 3:
                        return true;
                    case 2:
                        if((walls.contains(2) && (walls.contains(4) || walls.contains(6))
                        || (walls.contains(8) && (walls.contains(4) || walls.contains(6)))))
                        {
                            return this.canLoopToCorner(w, x, y, z, walls.get(0), walls.get(1), t)
                                    || this.canLoopToCorner(w, x, y, z, walls.get(1), walls.get(0), t);
                        }
                    break;
                    case 1:
                        switch(walls.get(0))
                        {
                            case 2:
                                return this.canLoopToCorner(w, x, y, z, 2, 4, t) && this.canLoopToCorner(w, x, y, z, 2, 6, t);
                            case 4:
                                return this.canLoopToCorner(w, x, y, z, 4, 8, t) && this.canLoopToCorner(w, x, y, z, 4, 2, t);
                            case 6:
                                return this.canLoopToCorner(w, x, y, z, 6, 8, t) && this.canLoopToCorner(w, x, y, z, 6, 2, t);
                            case 8:
                                return this.canLoopToCorner(w, x, y, z, 8, 4, t) && this.canLoopToCorner(w, x, y, z, 8, 6, t);
                            default:break;
                        }
                    break;
                    default:break;
                }
            }
            //START
            else {
                System.out.println("Block was in invalid position!");
            }
            //END
        }
        return false;
    }
}