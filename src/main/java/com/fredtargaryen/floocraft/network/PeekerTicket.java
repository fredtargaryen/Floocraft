package com.fredtargaryen.floocraft.network;

import com.fredtargaryen.floocraft.entity.EntityPeeker;
import net.minecraftforge.common.ticket.SimpleTicket;

public class PeekerTicket extends SimpleTicket<EntityPeeker> {
    @Override
    public boolean matches(EntityPeeker toMatch) {
        return false;
    }
}
