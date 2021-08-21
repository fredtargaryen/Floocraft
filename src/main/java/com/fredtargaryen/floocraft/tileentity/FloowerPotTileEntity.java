package com.fredtargaryen.floocraft.tileentity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.inventory.container.FloowerPotContainer;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class FloowerPotTileEntity extends TileEntity implements IInventory, ISidedInventory, INamedContainerProvider {
    public boolean justUpdated;
    private final ItemStack[] inv;
    private int hRange;
    private int vRange;

    private net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandlerModifiable> itemHandler;

    public FloowerPotTileEntity() {
        super(FloocraftBase.POT_TYPE.get());
        this.inv = new ItemStack[1];
        this.inv[0] = ItemStack.EMPTY;
        this.hRange = DataReference.POT_MAX_H_RANGE;
        this.vRange = 5;
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
    public void read(BlockState state, CompoundNBT tagCompound) {
        super.read(state, tagCompound);
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
        //Clamp ranges between 2 and 5 inclusive
        this.hRange = Math.max(DataReference.POT_MIN_H_RANGE, Math.min(DataReference.POT_MAX_H_RANGE, tagCompound.getInt("hRange")));
        this.vRange = Math.max(DataReference.POT_MIN_V_RANGE, Math.min(DataReference.POT_MAX_V_RANGE, tagCompound.getInt("vRange")));
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
        tagCompound.putInt("hRange", this.hRange);
        tagCompound.putInt("vRange", this.vRange);
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
        this.read(null, pkt.getNbtCompound());
        this.justUpdated = true;
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

    public void adjustPotRange(char range, int amount) {
        if(range == 'h') {
            //Adjust horizontal range
            if(amount == 1 && this.hRange < DataReference.POT_MAX_H_RANGE) ++this.hRange;
            else if(amount == -1 && this.hRange > DataReference.POT_MIN_H_RANGE) --this.hRange;
            this.markDirty();
        }
        else if(range == 'v') {
            //Adjust horizontal range
            if(amount == 1 && this.vRange < DataReference.POT_MAX_V_RANGE) ++this.vRange;
            else if(amount == -1 && this.vRange > DataReference.POT_MIN_V_RANGE) --this.vRange;
            this.markDirty();
        }
    }

    public int getHRange() { return this.hRange; }

    public int getVRange() { return this.vRange; }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (!this.removed && facing == Direction.DOWN && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.itemHandler == null)
                this.itemHandler = net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.DOWN)[0];
            return this.itemHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void remove() {
        super.remove();
        this.itemHandler.invalidate();
    }

    /////////////////////////////
    //ISidedInventory overrides//
    /////////////////////////////
    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[] { 0 } : new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
        if(index == 0 && direction == Direction.DOWN)
        {
            return this.isItemValidForSlot(index, itemStackIn);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return index == 0 && direction == Direction.DOWN;
    }
}
