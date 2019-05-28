package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.inventory.container.ContainerFloowerPot;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

import javax.annotation.Nullable;

public class GuiHandler {
    //returns an instance of the Gui you made earlier
    @Nullable
    public static GuiScreen getClientGuiElement(FMLPlayMessages.OpenContainer container) {
        PacketBuffer buffer = container.getAdditionalData();
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        EntityPlayer player = FloocraftBase.proxy.getClientPlayer();
        World world = FloocraftBase.proxy.getClientWorld();
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if(tileEntity instanceof TileEntityFloowerPot) {
            TileEntityFloowerPot pot = (TileEntityFloowerPot) tileEntity;
            return new GuiFloowerPot(pot, new ContainerFloowerPot(player.inventory, pot));
        }
        return null;
    }
}
