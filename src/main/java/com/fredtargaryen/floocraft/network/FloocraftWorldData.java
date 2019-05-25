package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.FMLLog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FloocraftWorldData extends WorldSavedData
{
	/**
	 * Code inspection will tell you the parameter is redundant and the access can be private, but it isnae and cannae
	 */
	public FloocraftWorldData(String key)
	{
		super(key);
	}

	public final ConcurrentHashMap<String, int[]> placeList = new ConcurrentHashMap<>();
	
	public static FloocraftWorldData forWorld(World world)
	{
        //Retrieves the FloocraftWorldData instance for the given world, creating it if necessary
		MapStorage storage = world.getPerWorldStorage();
		FloocraftWorldData data = (FloocraftWorldData)storage.getOrLoadData(FloocraftWorldData.class, DataReference.MODID);
		if (data == null)
		{
            FMLLog.warning("[FLOOCRAFT-SERVER] No fireplace data was found for this world. Creating new fireplace data.");
			data = new FloocraftWorldData(DataReference.MODID);
			storage.setData(DataReference.MODID, data);
		}
		return data;
	}

	public void addLocation(String name, BlockPos pos)
	{
		placeList.put(name, new int[]{pos.getX(), pos.getY(), pos.getZ()});
		FMLLog.info("[FLOOCRAFT-SERVER] Added fireplace at " + pos.toString() + ". Name: " + name);
		markDirty();
	}
	
	public void removeLocation(int x, int y, int z)
	{
		int[] coords = new int[]{x, y, z};
		boolean removedPlace = false;
		Iterator i = this.placeList.keySet().iterator();
		while(i.hasNext() && !removedPlace)
		{
			String nextPlaceName = (String)i.next();
			if(Arrays.equals(this.placeList.get(nextPlaceName), coords))
			{
				FMLLog.info("[FLOOCRAFT-SERVER] Removed fireplace at (" + x + ", " + y + ", " + z + "). Name: " + nextPlaceName);
				this.placeList.remove(nextPlaceName);
				removedPlace = true;
			}
		}
		if(!removedPlace)
		{
            FMLLog.warning("[FLOOCRAFT-SERVER] Failed to remove fireplace at (" + x + ", " + y + ", " + z + ").");
			FMLLog.warning("[FLOOCRAFT-SERVER] Data can be manually removed with an NBT editor.");
		}
		markDirty();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList nbttaglist = nbt.getTagList(DataReference.MODID, 10);
		for(int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbt1 = nbttaglist.getCompoundTagAt(i);
			int[] coords = new int[]{nbt1.getInteger("X"), nbt1.getInteger("Y"), nbt1.getInteger("Z")};
            this.placeList.put(nbt1.getString("NAME"), coords);
        }
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{	
		NBTTagList nbttaglist = new NBTTagList();
		for(String nextName : this.placeList.keySet())
		{
			NBTTagCompound nbt1 = new NBTTagCompound();
			nbt1.setString("NAME", nextName);
			int[] coords = this.placeList.get(nextName);
			nbt1.setInteger("X", coords[0]);
			nbt1.setInteger("Y", coords[1]);
			nbt1.setInteger("Z", coords[2]);
			nbttaglist.appendTag(nbt1);
		}
		nbt.setTag(DataReference.MODID, nbttaglist);
		return nbt;
	}
	
	public MessageFireplaceList assembleNewFireplaceList(World w)
	{
		MessageFireplaceList m = new MessageFireplaceList();
		m.places = this.placeList.keySet().toArray();
		boolean[] l = new boolean[m.places.length];
		int keyCount = 0;
		for(String nextName : this.placeList.keySet())
		{
			int[] coords = this.placeList.get(nextName);
            BlockPos dest = new BlockPos(coords[0], coords[1], coords[2]);
			Block b = w.getBlockState(dest).getBlock();
            boolean ok;
            if(b instanceof BlockFire)
            {
                ok = ((GreenFlamesBase) FloocraftBase.greenFlamesTemp).isInFireplace(w, dest) != null;
                w.setBlockState(dest, Blocks.FIRE.getDefaultState());
            }
			else
			{
				ok = b instanceof GreenFlamesBase;
			}
            l[keyCount] = ok;
			++keyCount;
		}
		m.enabledList = l;
		return m;
	}
}