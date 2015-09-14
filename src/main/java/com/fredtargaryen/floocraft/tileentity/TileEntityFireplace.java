package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.block.BlockFlooSign;
import com.fredtargaryen.floocraft.block.GreenFlamesBusy;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageAddFireplace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileEntityFireplace extends TileEntitySign
{
	private EntityPlayer writer;
    private boolean isConnected;
    private int y;

    public TileEntityFireplace(){}

	/**Sends packet containing:
	 *--xcoord
	 *--ycoord
	 *--zcoord
	 *--placename
	 */
	public void addLocation(BlockPos pos, String name, World par5World)
   	{
        if(par5World.isRemote)
        {
         	// We are on the client side.
            BlockPos finalpos = iterateDownFromSign(par5World, pos);
            this.y = finalpos.getY();
        	MessageAddFireplace m = new MessageAddFireplace();
        	m.name = name;
        	m.x = finalpos.getX();
        	m.y = this.y;
        	m.z = finalpos.getZ();
        	PacketHandler.INSTANCE.sendToServer(m);
        }
   	}
	
	@Override
	public EntityPlayer getPlayer()
    {
		return this.writer;
    }
	
	@Override
	public void setPlayer(EntityPlayer par1EntityPlayer)
    {
		if(this.writer == null)
		{
			this.writer = par1EntityPlayer;
		}
    }

    public int getY(){return this.y;}

    //Only call if world is remote
    private static BlockPos iterateDownFromSign(World w, BlockPos pos)
    {
        //The block below the block at the top of the fireplace
        pos = pos.offset(((EnumFacing)w.getBlockState(pos).getValue(BlockFlooSign.FACING)).getOpposite()).offset(EnumFacing.DOWN, 1);
        while((w.isAirBlock(pos) || w.getBlockState(pos).getBlock() == Blocks.fire || w.getBlockState(pos).getBlock() instanceof GreenFlamesBusy) && pos.getY() > -1)
        {
            pos = pos.offset(EnumFacing.DOWN, 1);
        }
        return pos.offset(EnumFacing.UP, 1);
    }

    public void setConnected(boolean b)
    {
        this.isConnected = b;
    }

    public boolean getConnected()
    {
        return this.isConnected;
    }

    public void writeToNBT(NBTTagCompound par1)
    {
        super.writeToNBT(par1);
        par1.setBoolean("Connected",this.isConnected);
        par1.setInteger("Y", this.y);
    }

    public void readFromNBT(NBTTagCompound par1)
    {
        super.readFromNBT(par1);
        this.isConnected = par1.getBoolean("Connected");
        this.y = par1.getInteger("Y");
    }
}
