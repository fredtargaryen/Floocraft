package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.inventory.container.FloowerPotContainer;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class FloowerPotTileEntity extends TileEntity implements IInventory, INamedContainerProvider {
    private final ItemStack[] inv;

    public FloowerPotTileEntity() {
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
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        return this.world.getTileEntity(this.pos) == this &&
                player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) < 64;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openInventory(PlayerEntity player) {

    }

    @Override
    @ParametersAreNonnullByDefault
    public void closeInventory(PlayerEntity player) {

    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return stack.getItem() instanceof ItemFlooPowder;
    }

    @Override
    public void clear() {

    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        ListNBT tagList = tagCompound.getList("Inventory", 10);
        if(tagList.size() == 0) {
            this.inv[0] = ItemStack.EMPTY;
        }
        else {
            CompoundNBT tag = tagList.getCompound(0);
            byte slot = tag.getByte("Slot");
            if (slot == 0) {
                this.inv[slot] = ItemStack.read(tag);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        tagCompound = super.write(tagCompound);
        ListNBT itemList = new ListNBT();
        ItemStack stack = inv[0];
        if (stack != null && !stack.isEmpty()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putByte("Slot", (byte) 0);
            stack.write(tag);
            itemList.add(tag);
        }
        tagCompound.put("Inventory", itemList);
        return tagCompound;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.write(new CompoundNBT()));
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
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.floocraftft.floowerpot");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player) {
        return new FloowerPotContainer(windowId, inventory, player.world, this.pos);
    }
}
