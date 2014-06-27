package com.fredtargaryen.floocraft.network;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.fredtargaryen.floocraft.block.GreenFlamesLowerBase;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class FloocraftWorldData extends WorldSavedData
{	
	public FloocraftWorldData(String par1Str)
	{
		super(par1Str);
	}

	final static String key = "ftfloocraft";
	
	public List<String>placenamelist = new ArrayList<String>();
	public List<Integer>xcoordlist, ycoordlist, zcoordlist = new ArrayList<Integer>();
	
	public static FloocraftWorldData forWorld(World world)
	{
        //Retrieves the FloocraftWorldData instance for the given world, creating it if necessary
		MapStorage storage = world.perWorldStorage;
		FloocraftWorldData data = (FloocraftWorldData)storage.loadData(FloocraftWorldData.class, key);
		if (data == null)
		{
			System.out.println("[FLOOCRAFT-SERVER] No fireplace data was found for this world. Creating new fireplace data.");
			data = new FloocraftWorldData(key);
			storage.setData(key, data);
		}
		return data;
	}

	public void addLocation(String name, int x, int y, int z)
	{
		placenamelist.add(name);
		xcoordlist.add(x);
		ycoordlist.add(y);
		zcoordlist.add(z);
		System.out.println("[FLOOCRAFT-SERVER] Adding fireplace at ("+x+", "+y+", "+z+"). Name: "+name);
		markDirty();
	}
	
	public void removeLocation(int x, int y, int z)
	{
		int i = 0;
		int j = -1;
		while(i < placenamelist.size() && j == -1)
		{
			if(!(xcoordlist.get(i) == x && ycoordlist.get(i) == y && zcoordlist.get(i) == z))
			{
				++i;
			}
			else
			{
				j = i;
			}
		}
		if(j > -1)
		{
			System.out.println("[FLOOCRAFT-SERVER] Removing fireplace at ("+x+", "+y+", "+z+"). Name: "+placenamelist.get(j));
			placenamelist.remove(j);
			xcoordlist.remove(j);
			ycoordlist.remove(j);
			zcoordlist.remove(j);
		}
		else
		{
			System.out.println("[FLOOCRAFT-SERVER] Failed to remove a fireplace at ("+x+", "+y+", "+z+").");
			System.out.println("[FLOOCRAFT-SERVER] The data can be manually removed with an NBT editor.");
		}
		markDirty();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList nbttaglist = nbt.getTagList(key, 10);
		for(int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbt1 = nbttaglist.getCompoundTagAt(i);
            this.xcoordlist.add(nbt1.getInteger("X"));
            this.ycoordlist.add(nbt1.getInteger("Y"));
            this.zcoordlist.add(nbt1.getInteger("Z"));
            this.placenamelist.add(nbt1.getString("NAME"));
        }
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{	
		NBTTagList nbttaglist = new NBTTagList();
		for(int i = 0; i < placenamelist.size(); i++)
		{
			NBTTagCompound nbt1 = new NBTTagCompound();
			nbt1.setString("NAME", placenamelist.get(i));
			nbt1.setInteger("X", xcoordlist.get(i));
			nbt1.setInteger("Y", ycoordlist.get(i));
			nbt1.setInteger("Z", zcoordlist.get(i));
			nbttaglist.appendTag(nbt1);
		}
		nbt.setTag(key, nbttaglist);
	}
	
	public MessageFireplaceList assembleNewFireplaceList(World w)
	{
		MessageFireplaceList m = new MessageFireplaceList();
		m.placenamelist = this.placenamelist;
		m.xcoordlist = this.xcoordlist;
		m.ycoordlist = this.ycoordlist;
		m.zcoordlist = this.zcoordlist;
		List<Boolean> l = new ArrayList<Boolean>();
		for(int x = 0; x < placenamelist.size(); x++)
		{
			Block b = w.getBlock(xcoordlist.get(x), ycoordlist.get(x), zcoordlist.get(x));
			l.add(b instanceof BlockFire || b instanceof GreenFlamesLowerBase);
		}
		m.enabledlist = l;
		return m;
	}
}