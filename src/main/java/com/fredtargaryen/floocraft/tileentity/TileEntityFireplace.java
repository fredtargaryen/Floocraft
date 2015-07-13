package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageAddFireplace;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
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
	public void addLocation(int x,int y, int z, String name, World par5World)
   	{
        if(par5World.isRemote)
        {
         	// We are on the client side.
            int newX = x;
            int newZ = z;
            int md = par5World.getBlockMetadata(x, y, z);
            switch(md)
            {
                case 2:
                {
                    newZ++;
                    break;
                }
                case 3:
                {
                    newZ--;
                    break;
                }
                case 4:
                {
                    newX++;
                    break;
                }
                case 5:
                {
                    newX--;
                    break;
                }
            }
            int newY = iterateDownFromTop(par5World, newX, y, newZ);
        	MessageAddFireplace m = new MessageAddFireplace();
        	m.name = name;
        	m.x = newX;
        	m.y = newY;
        	m.z = newZ;
        	PacketHandler.INSTANCE.sendToServer(m);
        }
        //else, we are on the Bukkit or server side.
   	}
	
	@Override
	public EntityPlayer func_145911_b()
    {
		return this.writer;
    }
	
	@Override
	public void func_145912_a(EntityPlayer par1EntityPlayer)
    {
		if(this.writer == null)
		{
			this.writer = par1EntityPlayer;
		}
    }

    public static int iterateDownFromTop(World w, int x, int y, int z)
    {
        y--;
        while((w.isAirBlock(x, y, z) || w.getBlock(x, y, z) instanceof BlockFire) && y > -1)
        {
            y--;
        }
        return y + 1;
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
