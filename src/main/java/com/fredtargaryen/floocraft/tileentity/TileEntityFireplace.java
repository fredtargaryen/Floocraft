package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.block.GreenFlamesLowerBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageAddFireplace;
import com.fredtargaryen.floocraft.network.messages.MessageRemoveFireplace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;

public class TileEntityFireplace extends TileEntitySign
{
	private EntityPlayer writer;
	public String fullName;
    private GreenFlamesLowerBase boundBlock;
	
	/**Sends packet containing:
	 *--xcoord
	 *--ycoord
	 *--zcoord
	 *--placename
	 */
	public boolean addLocation(int x,int y, int z, String[] name, World par5World, EntityPlayer ep)
   	{
		fullName = name[0]+" "+name[1]+" "+name[2]+" "+name[3];
        if(par5World.isRemote)
        {
         	// We are on the client side.
            y = this.iterateDownFromSign(par5World, x, y, z);
        	MessageAddFireplace m = new MessageAddFireplace();
        	m.name = fullName;
        	m.x = x;
        	m.y = y;
        	m.z = z;
        	PacketHandler.INSTANCE.sendToServer(m);
        }
        else
        {
         	// We are on the Bukkit or server side.
        }
        return true; //Change this when disallowing fireplaces with the same name.
   	}
   
	public static void removeLocation(World w, int x, int y, int z)
	{
		if(w.isRemote)
		{
            y = iterateDownFromSign(w, x, y, z);
			MessageRemoveFireplace m = new MessageRemoveFireplace();
			m.x = x;
			m.y = y;
			m.z = z;
			PacketHandler.INSTANCE.sendToServer(m);
		}
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

    //Only call if world is remote
    private static int iterateDownFromSign(World w, int x, int y, int z)
    {
        while(w.getBlock(x, y, z).isCollidable() && y > -1)
        {
            y--;
        }
        return y;
    }
}
