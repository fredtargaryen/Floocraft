package com.fredtargaryen.floocraft.proxy;

import com.fredtargaryen.floocraft.network.messages.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {
    void onMessage(MessageApproval ma);
    void onMessage(MessageDoGreenFlash mdgf);
    void onMessage(MessageFireplaceList mfl);
    void onMessage(MessageStartPeek msp);
    void registerGUIs();
    void registerRenderers();
    void registerTickHandlers();
    void setUUIDs(MessagePlayerID message);
    void setupRenderTypes();
}
