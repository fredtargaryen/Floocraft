package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.block.GreenFlamesTemp;
import com.fredtargaryen.floocraft.block.GreenFlamesBase;
import com.fredtargaryen.floocraft.network.messages.MessageFireplaceList;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import java.util.ArrayList;
import java.util.List;

public class FloocraftWorldData extends WorldSavedData
{	
	public FloocraftWorldData(String key)
	{
		super(key);
	}
	
	public final List<String>placenamelist = new ArrayList<String>();
	public final List<Integer>xcoordlist = new ArrayList<Integer>();
    public final List<Integer>ycoordlist = new ArrayList<Integer>();
    public final List<Integer>zcoordlist = new ArrayList<Integer>();
	
	public static FloocraftWorldData forWorld(World world)
	{
        //Retrieves the FloocraftWorldData instance for the given world, creating it if necessary
		MapStorage storage = world.perWorldStorage;
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
		placenamelist.add(name);
		xcoordlist.add(x);
		ycoordlist.add(y);
		zcoordlist.add(z);
		FMLLog.info("[FLOOCRAFT-SERVER] Added fireplace at (" + x + ", " + y + ", " + z + "). Name: " + name);
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
			FMLLog.info("[FLOOCRAFT-SERVER] Removed fireplace at (" + x + ", " + y + ", " + z + "). Name: " + placenamelist.get(j));
			placenamelist.remove(j);
			xcoordlist.remove(j);
			ycoordlist.remove(j);
			zcoordlist.remove(j);
		}
		else
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
		nbt.setTag(DataReference.MODID, nbttaglist);
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
            int dx = xcoordlist.get(x);
            int dy = ycoordlist.get(x);
            int dz = zcoordlist.get(x);
			Block b = w.getBlock(dx, dy, dz);
            boolean ok = true;
            if(!(b instanceof GreenFlamesBase) && b instanceof BlockFire)
            {
                w.setBlock(dx, dy, dz, FloocraftBase.greenFlamesTemp);
                GreenFlamesTemp gfit = (GreenFlamesTemp) w.getBlock(dx, dy, dz);
                ok = gfit.approveOrDenyTeleport(w, dx, dy, dz);
                w.setBlock(dx, dy, dz, Blocks.fire);
            }
            else if(!(b instanceof GreenFlamesBase))
            {
                ok = false;
            }
            l.add(ok);
		}
		m.enabledlist = l;
		return m;
	}
}