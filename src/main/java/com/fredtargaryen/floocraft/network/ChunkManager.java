package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.entity.EntityPeeker;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.HashMap;
import java.util.List;

public class ChunkManager {
    private static HashMap<EntityPeeker, ForgeChunkManager.Ticket> tickets = new HashMap<>();

    public static boolean loadChunks(World world, BlockPos dest, EntityPeeker ep, EnumFacing direction) {
        ChunkPos cp = new ChunkPos(dest);
        int minX;
        int minZ;
        int maxX;
        int maxZ;
        //A 3x2 array of chunks around the peeker, depending on direction
        switch(direction) {
            case NORTH:
                minX = cp.x - 1;
                minZ = cp.z;
                maxX = cp.x + 1;
                maxZ = cp.z + 1;
                break;
            case SOUTH:
                minX = cp.x - 1;
                minZ = cp.z - 1;
                maxX = cp.x + 1;
                maxZ = cp.z;
                break;
            case EAST:
                minX = cp.x;
                minZ = cp.z - 1;
                maxX = cp.x + 1;
                maxZ = cp.z + 1;
                break;
            default:
                //Can assume directions are all horizontal at this point so default case doubles as WEST
                minX = cp.x - 1;
                minZ = cp.z - 1;
                maxX = cp.x;
                maxZ = cp.z + 1;
                break;
        }
        ChunkPos[] chunks = new ChunkPos[6];
        int index = 0;
        for(int x = minX; x <= maxX; ++x) {
            for(int z = minZ; z <= maxZ; ++z) {
                chunks[index++] = new ChunkPos(x, z);
            }
        }
        ForgeChunkManager.Ticket t = ForgeChunkManager.requestTicket(FloocraftBase.instance, world, ForgeChunkManager.Type.ENTITY);
        if(t == null) {
            return false;
        }
        else {
            t.bindEntity(ep);
            tickets.put(ep, t);
            for(ChunkPos chunkPos : chunks) {
                ForgeChunkManager.forceChunk(t, chunkPos);
            }
            return true;
        }
    }

    /**
     * Retrieve the peeker's ticket and release it. This automatically unforces any chunks held by the ticket.
     * @param ep the EntityPeeker that has just been removed and is calling this.
     */
    public static void releaseTicket(EntityPeeker ep) {
        ForgeChunkManager.Ticket t = tickets.get(ep);
        tickets.remove(ep);
        ForgeChunkManager.releaseTicket(t);
    }

    public static void setCallback() {
        ForgeChunkManager.setForcedChunkLoadingCallback(FloocraftBase.instance, new ForgeChunkManager.LoadingCallback() {
            @Override
            public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
                //Do nothing, because peekers aren't supposed to be persistent
            }
        });
    }
}
