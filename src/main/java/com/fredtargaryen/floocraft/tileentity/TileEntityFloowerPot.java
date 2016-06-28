package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityFloowerPot extends TileEntity implements IInventory
{
    private final ItemStack[] inv;

    public TileEntityFloowerPot()
    {
        this.inv = new ItemStack[1];
    }
    @Override
    public int getSizeInventory()
    {
        return this.inv.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return this.inv[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null)
        {
            if (stack.stackSize <= amt)
            {
                setInventorySlotContents(slot, null);
            }
            else
            {
                stack = stack.splitStack(amt);
                if (stack.stackSize == 0)
                {
                    setInventorySlotContents(slot, null);
                }
            }
        }
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        this.inv[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(this.pos) == this &&
                player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) < 64;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return stack.getItem() instanceof ItemFlooPowder;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        NBTTagList tagList = tagCompound.getTagList("Inventory", 10);
        if(tagList.tagCount() == 0)
        {
            this.inv[0] = null;
        }
        else
        {
            NBTTagCompound tag = tagList.getCompoundTagAt(0);
            byte slot = tag.getByte("Slot");
            if (slot == 0) {
                this.inv[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound = super.writeToNBT(tagCompound);
        NBTTagList itemList = new NBTTagList();
        ItemStack stack = inv[0];
        if (stack != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("Slot", (byte) 0);
            stack.writeToNBT(tag);
            itemList.appendTag(tag);
        }
        tagCompound.setTag("Inventory", itemList);
        return tagCompound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 0, this.writeToNBT(new NBTTagCompound()));
    }

    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("Floower Pot");
    }
}
