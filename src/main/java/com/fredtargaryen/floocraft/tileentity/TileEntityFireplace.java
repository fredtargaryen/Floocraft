package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.block.GreenFlamesLowerBase;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageAddFireplace;
import com.fredtargaryen.floocraft.network.messages.MessageRemoveFireplace;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
        	m.name = fullName;
        	m.x = newX;
        	m.y = newY;
        	m.z = newZ;
        	PacketHandler.INSTANCE.sendToServer(m);
        }
        //else, we are on the Bukkit or server side.

        return true; //Change this when disallowing fireplaces with the same name.
   	}
   
	public static void removeLocation(World w, int x, int y, int z, int metadata)
	{
		if(w.isRemote)
		{
            // We are on the client side.
            int newX = x;
            int newZ = z;
            switch(metadata)
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
            int newY = iterateDownFromTop(w, newX, y, newZ);
			MessageRemoveFireplace m = new MessageRemoveFireplace();
			m.x = newX;
			m.y = newY;
			m.z = newZ;
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
    private static int iterateDownFromTop(World w, int x, int y, int z)
    {
        y--;
        while((w.isAirBlock(x, y, z) || w.getBlock(x, y, z) instanceof BlockFire) && y > -1)
        {
            y--;
        }
        return y + 1;
    }
}
