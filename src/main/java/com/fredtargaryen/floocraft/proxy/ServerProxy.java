package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.network.messages.*;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class ServerProxy implements IProxy {
    @Override
    public void onMessage(MessageApproval ma) {}
    @Override
    public void onMessage(MessageDoGreenFlash mdgf) {}
    @Override
    public void onMessage(MessageFireplaceList mfl) {}
    @Override
    public void onMessage(MessageStartPeek msp) {}
    @Override
    public void registerGUIs() {}
    @Override
    public void registerRenderers(){}
    @Override
    public void registerTickHandlers(){}
    @Override
    public void setUUIDs(MessagePlayerID message){}
    @Override
    public void setupRenderTypes(){}
    @Override
    public Entity getEntityWithUUID(World world, UUID uuid){
        if(world != null && uuid != null) {
            return ((ServerWorld) world).getEntityByUuid(uuid);
        }
        return null;
    }
}
