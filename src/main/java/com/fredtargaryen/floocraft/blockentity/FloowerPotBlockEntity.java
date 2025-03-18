package com.fredtargaryen.floocraft.blockentity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBlockEntityTypes;
import com.fredtargaryen.floocraft.inventory.FloowerPotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class FloowerPotBlockEntity extends BaseContainerBlockEntity {
    /**
     * For syncing the powder type and amount via @link{FloowerPotMenu}
     */
    private final ItemStackHandler powderStackHandler;

    /**
     * For syncing the horizontal range to search for fire blocks
     */
    private final DataSlot hRangeSlot;

    /**
     * For syncing the vertical range to search for fire blocks
     */
    private final DataSlot vRangeSlot;

    /**
     * For FloowerPotRenderer to display the level of powder in the pot on the client
     */
    private int powderLevel;

    public FloowerPotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(FloocraftBlockEntityTypes.FLOOWER_POT.get(), pPos, pBlockState);
        this.powderStackHandler = new ItemStackHandler(NonNullList.withSize(1, ItemStack.EMPTY));
        this.hRangeSlot = DataSlot.standalone();
        this.hRangeSlot.set(DataReference.POT_MAX_H_RANGE);
        this.vRangeSlot = DataSlot.standalone();
        this.vRangeSlot.set(DataReference.POT_MAX_V_RANGE);
        this.powderLevel = 0;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        BlockPos pos = this.getBlockPos();
        SimpleContainerData posData = new SimpleContainerData(3);
        posData.set(0, pos.getX());
        posData.set(1, pos.getY());
        posData.set(2, pos.getZ());
        return new FloowerPotMenu(
                containerId,
                playerInventory,
                this.powderStackHandler,
                this.hRangeSlot,
                this.vRangeSlot,
                posData);
    }

    /**
     * Why oh why don't ItemStackHandlers have a getAllItems method
     *
     * @return a copy of the list in powderStackHandler
     */
    @Override
    protected NonNullList<ItemStack> getItems() {
        return NonNullList.withSize(1, this.powderStackHandler.getStackInSlot(0).copy());
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.powderStackHandler.setStackInSlot(0, items.getFirst());
        this.setChanged();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.powderStackHandler.setStackInSlot(0, stack);
        super.setItem(slot, stack);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.floocraftft.floower_pot");
    }

    @Override
    public void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.powderStackHandler.deserializeNBT(provider, tag.getCompound("stack"));
        this.hRangeSlot.set(Math.clamp(tag.getInt("hRange"), DataReference.POT_MIN_H_RANGE, DataReference.POT_MAX_H_RANGE));
        this.vRangeSlot.set(Math.clamp(tag.getInt("vRange"), DataReference.POT_MIN_V_RANGE, DataReference.POT_MAX_V_RANGE));
        this.setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("stack", this.powderStackHandler.serializeNBT(provider));
        tag.putInt("hRange", this.hRangeSlot.get());
        tag.putInt("vRange", this.vRangeSlot.get());
    }

    public void adjustPotRange(boolean useVerticalRange, int amount) {
        if (useVerticalRange) {
            this.vRangeSlot.set(Math.clamp(this.vRangeSlot.get() + amount, DataReference.POT_MIN_V_RANGE, DataReference.POT_MAX_V_RANGE));
        } else {
            this.hRangeSlot.set(Math.clamp(this.hRangeSlot.get() + amount, DataReference.POT_MIN_H_RANGE, DataReference.POT_MAX_H_RANGE));
        }
        this.setChanged();
    }

    public int getHRange() {
        return this.hRangeSlot.get();
    }

    public int getVRange() {
        return this.vRangeSlot.get();
    }

    public int getPowderLevel() {
        return this.powderLevel;
    }

    // For syncing powder level to client
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putByte("level", (byte) this.powderStackHandler.getStackInSlot(0).getCount());
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // The packet uses the CompoundTag returned by #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
        this.powderLevel = Math.clamp(packet.getTag().getByte("level"), 0, 64);
    }
//
//    @Override
//    @Nonnull
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
//    {
//        if (!this.removed && facing == Direction.DOWN && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//            if (this.itemHandler == null)
//                this.itemHandler = net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.DOWN)[0];
//            return this.itemHandler.cast();
//        }
//        return super.getCapability(capability, facing);
//    }
//
//    /**
//     * invalidates a tile entity
//     */
//    @Override
//    public void remove() {
//        super.remove();
//        this.itemHandler.invalidate();
//    }
//
//    /////////////////////////////
//    //ISidedInventory overrides//
//    /////////////////////////////
//    @Override
//    public int[] getSlotsForFace(Direction side) {
//        return side == Direction.DOWN ? new int[] { 0 } : new int[0];
//    }
//
//    @Override
//    public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
//        if(index == 0 && direction == Direction.DOWN)
//        {
//            return this.isItemValidForSlot(index, itemStackIn);
//        }
//        return false;
//    }
//
//    @Override
//    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
//        return index == 0 && direction == Direction.DOWN;
//    }
}
