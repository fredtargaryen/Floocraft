package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class FloocraftWorldData extends WorldSavedData {
	/**
	 * Code inspection will tell you the access can be private, but it jolly well can't
	 */
	public FloocraftWorldData()
	{
		super(DataReference.MODID);
	}

	/**
	 * reads in data from the CompoundNBT into this MapDataBase
	 *
	 * @param nbt the compound to be read from
	 */
	@Override
	public void read(CompoundNBT nbt) {
		ListNBT ListNBT = nbt.getList(DataReference.MODID, 10);
		for(int i = 0; i < ListNBT.size(); ++i)
		{
			CompoundNBT nbt1 = ListNBT.getCompound(i);
			int[] coords = new int[]{nbt1.getInt("X"), nbt1.getInt("Y"), nbt1.getInt("Z")};
			this.placeList.put(nbt1.getString("NAME"), coords);
		}
	}

	@Override
	@Nonnull
	public CompoundNBT write(@Nonnull CompoundNBT compound) {
		ListNBT ListNBT = new ListNBT();
		for(String nextName : this.placeList.keySet()) {
			CompoundNBT nbt1 = new CompoundNBT();
			nbt1.putString("NAME", nextName);
			int[] coords = this.placeList.get(nextName);
			nbt1.putInt("X", coords[0]);
			nbt1.putInt("Y", coords[1]);
			nbt1.putInt("Z", coords[2]);
			ListNBT.add(nbt1);
		}
		compound.put(DataReference.MODID, ListNBT);
		return compound;
	}

	public final ConcurrentHashMap<String, int[]> placeList = new ConcurrentHashMap<>();
	
	public static FloocraftWorldData forWorld(World world) {
		ServerWorld serverWorld = world.getServer().getWorld(DimensionType.OVERWORLD);
		DimensionSavedDataManager storage = serverWorld.getSavedData();
		return storage.getOrCreate(FloocraftWorldData::new, DataReference.MODID);
	}

	public void addLocation(String name, BlockPos pos) {
		placeList.put(name, new int[]{pos.getX(), pos.getY(), pos.getZ()});
		FloocraftBase.info("[FLOOCRAFT-SERVER] Added fireplace at " + pos.toString() + ". Name: " + name);
		markDirty();
	}
	
	public void removeLocation(int x, int y, int z) {
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
            if(b instanceof FireBlock) {
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