package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.inventory.container.ContainerFloowerPot;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class TileEntityFloowerPot extends TileEntity implements IInventory, IInteractionObject {
    private final ItemStack[] inv;

    public TileEntityFloowerPot() {
        super(FloocraftBase.POT_TYPE);
        this.inv = new ItemStack[1];
        this.inv[0] = ItemStack.EMPTY;
    }

    @Override
    public int getSizeInventory()
    {
        return this.inv.length;
    }

    @Override
    public boolean isEmpty()
    {
        return this.inv[0].isEmpty();
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack getStackInSlot(int slot)
    {
        return this.inv[slot];
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack decrStackSize(int slot, int amt)
    {
        ItemStack stack = getStackInSlot(slot);
        if (!stack.isEmpty())
        {
            if (stack.getCount() <= amt)
            {
                setInventorySlotContents(slot, ItemStack.EMPTY);
            }
            else
            {
                stack = stack.split(amt);
                if (stack.getCount() == 0)
                {
                    setInventorySlotContents(slot, ItemStack.EMPTY);
                }
            }
        }
        return stack;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack removeStackFromSlot(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        ItemStack nonnullstack = stack == null ? ItemStack.EMPTY : stack;
        this.inv[slot] = nonnullstack;

        if (!nonnullstack.isEmpty() && nonnullstack.getCount() > this.getInventoryStackLimit())
        {
            nonnullstack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return this.world.getTileEntity(this.pos) == this &&
                player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) < 64;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openInventory(EntityPlayer player) {

    }

    @Override
    @ParametersAreNonnullByDefault
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    @ParametersAreNonnullByDefault
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
    public void read(NBTTagCompound tagCompound) {
        super.read(tagCompound);
        NBTTagList tagList = tagCompound.getList("Inventory", 10);
        if(tagList.size() == 0) {
            this.inv[0] = ItemStack.EMPTY;
        }
        else {
            NBTTagCompound tag = tagList.getCompound(0);
            byte slot = tag.getByte("Slot");
            if (slot == 0) {
                this.inv[slot] = ItemStack.read(tag);
            }
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tagCompound) {
        tagCompound = super.write(tagCompound);
        NBTTagList itemList = new NBTTagList();
        ItemStack stack = inv[0];
        if (stack != null && !stack.isEmpty()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("Slot", (byte) 0);
            stack.write(tag);
            itemList.add(tag);
        }
        tagCompound.setTag("Inventory", itemList);
        return tagCompound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.write(new NBTTagCompound()));
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
    @OnlyIn(Dist.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ITextComponent getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ITextComponent getDisplayName() {
        return new TextComponentString("Floower Pot");
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return null;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerFloowerPot(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return DataReference.MODID + ":pot";
    }
}
