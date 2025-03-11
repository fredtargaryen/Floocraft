package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.block.FlooFlamesBlock;
import com.fredtargaryen.floocraft.network.messages.FireplaceListResponseMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FloocraftLevelData extends SavedData {
    public static FloocraftLevelData getForLevel(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(
                new Factory<>(
                        FloocraftLevelData::new,
                        FloocraftLevelData::load,
                        DataFixTypes.LEVEL),
                DataReference.MODID);
    }

    public static FloocraftLevelData load(CompoundTag data, HolderLookup.Provider provider) {
        FloocraftLevelData floocraftLevelData = new FloocraftLevelData();
        ListTag places = data.getList(DataReference.MODID, 10); // TODO: 9 now?
        for (int i = 0; i < places.size(); ++i) {
            CompoundTag place = places.getCompound(i);
            int[] coords = new int[]{place.getInt("X"), place.getInt("Y"), place.getInt("Z")};
            floocraftLevelData.placeList.put(place.getString("NAME"), coords);
        }

        return floocraftLevelData;
    }

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag data, HolderLookup.Provider provider) {
        ListTag places = new ListTag();
        for (String nextName : this.placeList.keySet()) {
            CompoundTag place = new CompoundTag();
            place.putString("NAME", nextName);
            int[] coords = this.placeList.get(nextName);
            place.putInt("X", coords[0]);
            place.putInt("Y", coords[1]);
            place.putInt("Z", coords[2]);
            places.add(place);
        }
        data.put(DataReference.MODID, places);
        return data;
    }

    public final ConcurrentHashMap<String, int[]> placeList = new ConcurrentHashMap<>();


    public void addLocation(String name, BlockPos pos) {
        placeList.put(name, new int[]{pos.getX(), pos.getY(), pos.getZ()});
        FloocraftBase.info(String.format("[FLOOCRAFT-SERVER] Added fireplace '%s' at %s", name, pos));
        setDirty();
    }

    public void removeLocation(String locationName) {
        if (this.placeList.containsKey(locationName)) {
            int[] coords = this.placeList.remove(locationName);
            FloocraftBase.info(String.format("[FLOOCRAFT-SERVER] Removed fireplace '%s' at {%d, %d, %d}", locationName, coords[0], coords[1], coords[2]));
            setDirty();
        }
    }

    public FireplaceListResponseMessage assembleNewFireplaceList(Level level, BlockPos playerLocation) {
        List<Boolean> canTpList = new ArrayList<>();
        List<Boolean> canPeekList = new ArrayList<>();
        int playerPlaceIndex = -1;
        int index = -1;
        FlooFlamesBlock flooFlames = FloocraftBlocks.FLOO_FLAMES.get();
        for (String nextName : this.placeList.keySet()) {
            ++index;
            int[] coords = this.placeList.get(nextName);
            boolean canTp = false;
            boolean canPeek = false;
            if (playerLocation.getX() == coords[0]
                    && playerLocation.getY() == coords[1]
                    && playerLocation.getZ() == coords[2]) {
                playerPlaceIndex = index;
            }
            if (playerPlaceIndex != index) {
                BlockPos dest = new BlockPos(coords[0], coords[1], coords[2]);
                BlockState destBlockState = level.getBlockState(dest);
                if (destBlockState.is(BlockTags.FIRE)) {
                    canTp = flooFlames.isInFireplace(level, dest) != null;
                    canPeek = level.isLoaded(dest);
                } else if(destBlockState.getBlock() instanceof FlooFlamesBlock) {
                    canTp = true;
                    canPeek = level.isLoaded(dest);
                }
            }
            canTpList.add(canTp);
            canPeekList.add(canPeek);
        }
        return new FireplaceListResponseMessage(
                this.placeList.keySet().stream().toList(),
                canTpList,
                canPeekList,
                playerPlaceIndex
        );
    }
}