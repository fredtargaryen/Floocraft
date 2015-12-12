package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.block.GreenFlamesTemp;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.FMLLog;

import java.util.*;

public class FloocraftWorldData extends WorldSavedData
{	
	public FloocraftWorldData(String key)
	{
		super(key);
	}

	public HashMap<String, int[]> placeList = new HashMap<String, int[]>();
	
	public static FloocraftWorldData forWorld(World world)
	{
        //Retrieves the FloocraftWorldData instance for the given world, creating it if necessary
		MapStorage storage = world.getPerWorldStorage();
		FloocraftWorldData data = (FloocraftWorldData)storage.loadData(FloocraftWorldData.class, DataReference.MODID);
		if (data == null)
		{
            FMLLog.warning("[FLOOCRAFT-SERVER] No fireplace data was found for this world. Creating new fireplace data.");
			data = new FloocraftWorldData(DataReference.MODID);
			storage.setData(DataReference.MODID, data);
		}
		return data;
	}

	public void addLocation(String name, int x, int y, int z)
	{
		placeList.put(name, new int[]{x, y, z});
		FMLLog.info("[FLOOCRAFT-SERVER] Added fireplace at (" + x + ", " + y + ", " + z + "). Name: " + name);
		markDirty();
	}
	
	public void removeLocation(int x, int y, int z)
	{
		int[] coords = new int[]{x, y, z};
		boolean removedPlace = false;
		//ArrayList<String> placesToRemove = new ArrayList<String>();
		for(String placeName : this.placeList.keySet())
		{
			if(Arrays.equals(this.placeList.get(placeName), coords))
			{
				//placesToRemove.add(placeName);
				FMLLog.info("[FLOOCRAFT-SERVER] Removed fireplace at (" + x + ", " + y + ", " + z + "). Name: " + placeName);
				this.placeList.remove(placeName);
				removedPlace = true;
			}
		}
		//for(String s : placesToRemove)
		//{
		//	this.placeList.remove(s);
		//}
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
	public void writeToNBT(NBTTagCompound nbt)
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
	}
	
	public MessageFireplaceList assembleNewFireplaceList(World w)
	{
		MessageFireplaceList m = new MessageFireplaceList();
		m.placeList = this.placeList;
		List<Boolean> l = new ArrayList<Boolean>();
		for(String nextName : this.placeList.keySet())
		{
			int[] coords = this.placeList.get(nextName);
            BlockPos dest = new BlockPos(coords[0], coords[1], coords[2]);
			Block b = w.getBlockState(dest).getBlock();
            boolean ok;
            if(b instanceof BlockFire)
            {
                w.setBlockState(dest, FloocraftBase.greenFlamesTemp.getDefaultState());
                GreenFlamesTemp gfit = (GreenFlamesTemp) w.getBlockState(dest).getBlock();
                ok = gfit.isInFireplace(w, dest);
                w.setBlockState(dest, Blocks.fire.getDefaultState());
            }
			else
			{
				ok = b instanceof GreenFlamesBase;
			}
            l.add(ok);
		}
		m.enabledList = l;
		return m;
	}
}