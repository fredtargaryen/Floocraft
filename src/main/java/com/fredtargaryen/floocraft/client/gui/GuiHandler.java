package com.fredtargaryen.floocraft.client.gui;

import com.fredtargaryen.floocraft.inventory.container.ContainerFloowerPot;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
    //returns an instance of the Container you made earlier
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if(tileEntity instanceof TileEntityFloowerPot)
        {
            return new ContainerFloowerPot(player.inventory, (TileEntityFloowerPot) tileEntity);
        }
        return null;
    }

    //returns an instance of the Gui you made earlier
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if(tileEntity instanceof TileEntityFloowerPot)
        {
            return new GuiFloowerPot(player.inventory, (TileEntityFloowerPot) tileEntity);
        }
        return null;
    }
}
