package com.fredtargaryen.floocraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import com.fredtargaryen.floocraft.network.FloocraftWorldData;
import com.fredtargaryen.floocraft.network.PacketHandler;
import com.fredtargaryen.floocraft.network.messages.MessageAddFireplace;
import com.fredtargaryen.floocraft.network.messages.MessageRemoveFireplace;

public class TileEntityFireplace extends TileEntitySign
{
	private EntityPlayer writer;
	public String fullName;
	
	/**Sends packet containing:
	 *--xcoord
	 *--ycoord
	 *--zcoord
	 *--placename
	 */
	public boolean connect(int x,int y, int z, String[] name, World par5World, EntityPlayer ep)
   	{
		int md = par5World.getBlockMetadata(x, y, z);

		if (md == 2)
		{
			--z;
		}
		else if (md == 3)
		{
			++z;
		}
		else if (md == 4)
		{
			--x;
		}
		else if (md == 5)
		{
			++x;
		}
		y -= 2;
		fullName = name[0]+" "+name[1]+" "+name[2]+" "+name[3];
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        if(side == Side.CLIENT)
        {
         	// We are on the client side.
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
        
        return true;
   	}
   
	public static void removeLocation(int x, int y, int z, World world)
	{
		if(world.isRemote)
		{
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
}
