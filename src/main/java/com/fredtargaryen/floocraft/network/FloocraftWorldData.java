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
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.WorldSavedDataStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FloocraftWorldData extends WorldSavedData {
	/**
	 * Code inspection will tell you the parameter is redundant and the access can be private, but it isnae and cannae
	 */
	public FloocraftWorldData(String key)
	{
		super(key);
	}

	/**
	 * reads in data from the NBTTagCompound into this MapDataBase
	 *
	 * @param nbt
	 */
	@Override
	public void read(NBTTagCompound nbt) {
		NBTTagList nbttaglist = nbt.getList(DataReference.MODID, 10);
		for(int i = 0; i < nbttaglist.size(); ++i)
		{
			NBTTagCompound nbt1 = nbttaglist.getCompound(i);
			int[] coords = new int[]{nbt1.getInt("X"), nbt1.getInt("Y"), nbt1.getInt("Z")};
			this.placeList.put(nbt1.getString("NAME"), coords);
		}
	}

	@Override
	public NBTTagCompound write(NBTTagCompound compound) {
		NBTTagList nbttaglist = new NBTTagList();
		for(String nextName : this.placeList.keySet()) {
			NBTTagCompound nbt1 = new NBTTagCompound();
			nbt1.setString("NAME", nextName);
			int[] coords = this.placeList.get(nextName);
			nbt1.setInt("X", coords[0]);
			nbt1.setInt("Y", coords[1]);
			nbt1.setInt("Z", coords[2]);
			nbttaglist.add(nbt1);
		}
		compound.setTag(DataReference.MODID, nbttaglist);
		return compound;
	}

	public final ConcurrentHashMap<String, int[]> placeList = new ConcurrentHashMap<>();
	
	public static FloocraftWorldData forWorld(World world) {
        //Retrieves the FloocraftWorldData instance for the given world, creating it if necessary
		WorldSavedDataStorage storage = world.getMapStorage();
		DimensionType dt = world.getDimension().getType();
		FloocraftWorldData data = (FloocraftWorldData)storage.func_212426_a(dt, FloocraftWorldData::new, DataReference.MODID); //getOrLoadData
		if (data == null) {
            FloocraftBase.warn("[FLOOCRAFT-SERVER] No fireplace data was found for this world. Creating new fireplace data.");
			data = new FloocraftWorldData(DataReference.MODID);
			storage.func_212424_a(dt, DataReference.MODID, data); //setData
		}
		return data;
	}

	public void addLocation(String name, BlockPos pos)
	{
		placeList.put(name, new int[]{pos.getX(), pos.getY(), pos.getZ()});
		FloocraftBase.info("[FLOOCRAFT-SERVER] Added fireplace at " + pos.toString() + ". Name: " + name);
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
				FloocraftBase.info("[FLOOCRAFT-SERVER] Removed fireplace at (" + x + ", " + y + ", " + z + "). Name: " + nextPlaceName);
				this.placeList.remove(nextPlaceName);
				removedPlace = true;
			}
		}
		if(!removedPlace)
		{
            FloocraftBase.warn("[FLOOCRAFT-SERVER] Failed to remove fireplace at (" + x + ", " + y + ", " + z + ").");
			FloocraftBase.warn("[FLOOCRAFT-SERVER] Data can be manually removed with an NBT editor.");
		}
		markDirty();
	}
	
	public MessageFireplaceList assembleNewFireplaceList(World w) {
		MessageFireplaceList m = new MessageFireplaceList();
		m.places = this.placeList.keySet().toArray();
		boolean[] l = new boolean[m.places.length];
		int keyCount = 0;
		for(String nextName : this.placeList.keySet()) {
			int[] coords = this.placeList.get(nextName);
            BlockPos dest = new BlockPos(coords[0], coords[1], coords[2]);
			Block b = w.getBlockState(dest).getBlock();
            boolean ok;
            if(b instanceof BlockFire) {
                ok = ((GreenFlamesBase) FloocraftBase.GREEN_FLAMES_TEMP).isInFireplace(w, dest) != null;
                w.setBlockState(dest, Blocks.FIRE.getDefaultState());
            }
			else {
				ok = b instanceof GreenFlamesBase;
			}
            l[keyCount] = ok;
			++keyCount;
		}
		m.enabledList = l;
		return m;
	}
}