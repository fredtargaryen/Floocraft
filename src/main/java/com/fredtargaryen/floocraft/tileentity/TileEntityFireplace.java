package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.block.BlockFlooSign;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageAddFireplace;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileEntityFireplace extends TileEntitySign
{
	private EntityPlayer writer;
    private boolean isConnected;

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
            BlockPos finalpos = iterateDownFromTop(par5World, pos.offset(((EnumFacing)par5World.getBlockState(pos).getValue(BlockFlooSign.FACING)).getOpposite()));
        	MessageAddFireplace m = new MessageAddFireplace();
        	m.name = name;
        	m.x = finalpos.getX();
        	m.y = finalpos.getY();
        	m.z = finalpos.getZ();
        	PacketHandler.INSTANCE.sendToServer(m);
        }
        //else, we are on the Bukkit or server side.
   	}
   
	public static void removeLocation(World w, BlockPos pos, EnumFacing facing)
	{
        if(!w.isRemote)
        {
            BlockPos finalPos = iterateDownFromTop(w, pos.offset((EnumFacing)w.getBlockState(pos).getValue(BlockFlooSign.FACING)));
            FloocraftWorldData.forWorld(w).removeLocation(finalPos.getX(), finalPos.getY(), finalPos.getZ());
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

    //Only call if world is remote
    private static BlockPos iterateDownFromTop(World w, BlockPos pos)
    {
        pos = pos.offset(EnumFacing.DOWN, 1);
        while((w.isAirBlock(pos) || w.getBlockState(pos) instanceof BlockFire) && pos.getY() > -1)
        {
            pos = pos.offset(EnumFacing.DOWN, 1);
        }
        return pos.up(1);
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
    }

    public void readFromNBT(NBTTagCompound par1)
    {
        super.readFromNBT(par1);
        this.isConnected = par1.getBoolean("Connected");
    }
}
