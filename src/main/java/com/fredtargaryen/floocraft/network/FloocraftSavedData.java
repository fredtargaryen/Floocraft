package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.block.FlooFlamesBlock;
import com.fredtargaryen.floocraft.network.messages.FireplaceListResponseMessage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FloocraftSavedData extends SavedData {
    public static MapCodec<FloocraftSavedData> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.unboundedMap(Codec.STRING, BlockPos.CODEC)
                            .fieldOf("place_list")
                            .forGetter(i -> i.placeList)
            ).apply(instance, FloocraftSavedData::new));

    public static SavedDataType<FloocraftSavedData> TYPE = new SavedDataType<>(
            // Resolves to 'saves/<world_name>/data/floocraftft.dat'
            DataReference.MODID,
            FloocraftSavedData::new,
            ctx -> CODEC.codec()
    );

    // For the new instance
    private FloocraftSavedData(Context ctx) {
        this(Map.of());
    }

    public FloocraftSavedData(Map<String, BlockPos> placeList) {
        this.placeList = new ConcurrentHashMap<>(placeList);
    }

    public static FloocraftSavedData getForLevel(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(FloocraftSavedData.TYPE);
    }

//    public static FloocraftSavedData load(CompoundTag data, HolderLookup.Provider provider) {
//        FloocraftSavedData floocraftSavedData = new FloocraftSavedData();
//        ListTag places = data.getList(DataReference.MODID, 10);
//        for (int i = 0; i < places.size(); ++i) {
//            CompoundTag place = places.getCompound(i);
//            int[] coords = new int[]{place.getInt("X"), place.getInt("Y"), place.getInt("Z")};
//            floocraftSavedData.placeList.put(place.getString("NAME"), coords);
//        }
//
//        return floocraftSavedData;
//    }
//
//    @Override
//    @Nonnull
//    public CompoundTag save(@Nonnull CompoundTag data, HolderLookup.Provider provider) {
//        ListTag places = new ListTag();
//        for (String nextName : this.placeList.keySet()) {
//            CompoundTag place = new CompoundTag();
//            place.putString("NAME", nextName);
//            int[] coords = this.placeList.get(nextName);
//            place.putInt("X", coords[0]);
//            place.putInt("Y", coords[1]);
//            place.putInt("Z", coords[2]);
//            places.add(place);
//        }
//        data.put(DataReference.MODID, places);
//        return data;
//    }

    public ConcurrentHashMap<String, BlockPos> placeList;


    public void addLocation(String name, BlockPos pos) {
        placeList.put(name, pos);
        FloocraftBase.info(String.format("[FLOOCRAFT-SERVER] Added fireplace '%s' at %s", name, pos));
        setDirty();
    }

    public void removeLocation(String locationName) {
        if (this.placeList.containsKey(locationName)) {
            BlockPos coords = this.placeList.remove(locationName);
            FloocraftBase.info(String.format("[FLOOCRAFT-SERVER] Removed fireplace '%s' at %s", locationName, coords));
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
            BlockPos coords = this.placeList.get(nextName);
            boolean canTp = false;
            boolean canPeek = false;
            if (playerLocation.getX() == coords.getX()
                    && playerLocation.getY() == coords.getY()
                    && playerLocation.getZ() == coords.getZ()) {
                playerPlaceIndex = index;
            }
            if (playerPlaceIndex != index) {
                BlockPos dest = new BlockPos(coords.getX(), coords.getY(), coords.getZ());
                BlockState destBlockState = level.getBlockState(dest);
                if (destBlockState.is(BlockTags.FIRE)) {
                    canTp = flooFlames.isInFireplace(level, dest) != null;
                    canPeek = level.isLoaded(dest);
                } else if (destBlockState.getBlock() instanceof FlooFlamesBlock) {
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