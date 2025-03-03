package com.fredtargaryen.floocraft.blockentity;

import com.fredtargaryen.floocraft.DataReference;
import com.fredtargaryen.floocraft.FloocraftBlockEntityTypes;
import com.fredtargaryen.floocraft.inventory.FloowerPotMenu;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.slf4j.Logger;

import javax.annotation.Nonnull;

public class FloowerPotBlockEntity extends BaseContainerBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private NonNullList<ItemStack> powderStack;

    private ItemStackHandler powderStackHandler;

    public FloowerPotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(FloocraftBlockEntityTypes.FLOOWER_POT.get(), pPos, pBlockState);
        this.inv = new ItemStack[1];
        this.inv[0] = ItemStack.EMPTY;
        this.powderStack = NonNullList.withSize(1, ItemStack.EMPTY);
        this.powderStackHandler = new ItemStackHandler(1);
        this.hRange = DataReference.POT_MAX_H_RANGE;
        this.vRange = DataReference.POT_MAX_V_RANGE;
        this.hRangeSlot = DataSlot.standalone();
        this.hRangeSlot.set(DataReference.POT_MAX_H_RANGE);
        this.vRangeSlot = DataSlot.standalone();
        this.vRangeSlot.set(DataReference.POT_MAX_V_RANGE);
    }

    //    public boolean justUpdated;
    private ItemStack[] inv;
    private int hRange;
    private int vRange;

    private DataSlot hRangeSlot;
    private DataSlot vRangeSlot;

    // BaseContainerBlockEntity overrides
    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.floocraftft.floower_pot");
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

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.powderStack;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.powderStack = items;
        this.setChanged();
    }

    @Override

    public int getContainerSize() {
        return 1;
    }

    //
//    private net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandlerModifiable> itemHandler;
//
//
//    @Override
//    public int getSizeInventory()
//    {
//        return this.inv.length;
//    }
//
//    @Override
//    public boolean isEmpty()
//    {
//        return this.inv[0].isEmpty();
//    }
//
//    @Override
//    @MethodsReturnNonnullByDefault
//    public ItemStack getStackInSlot(int slot)
//    {
//        return this.inv[slot];
//    }
//
//    @Override
//    @MethodsReturnNonnullByDefault
//    public ItemStack decrStackSize(int slot, int amt)
//    {
//        ItemStack stack = getStackInSlot(slot);
//        if (!stack.isEmpty())
//        {
//            if (stack.getCount() <= amt)
//            {
//                setInventorySlotContents(slot, ItemStack.EMPTY);
//            }
//            else
//            {
//                stack = stack.split(amt);
//                if (stack.getCount() == 0)
//                {
//                    setInventorySlotContents(slot, ItemStack.EMPTY);
//                }
//            }
//        }
//        return stack;
//    }
//
//    @Override
//    @MethodsReturnNonnullByDefault
//    public ItemStack removeStackFromSlot(int index) {
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    @ParametersAreNonnullByDefault
//    public void setInventorySlotContents(int slot, ItemStack stack)
//    {
//        ItemStack nonnullstack = stack == null ? ItemStack.EMPTY : stack;
//        this.inv[slot] = nonnullstack;
//
//        if (!nonnullstack.isEmpty() && nonnullstack.getCount() > this.getInventoryStackLimit())
//        {
//            nonnullstack.setCount(this.getInventoryStackLimit());
//        }
//        this.markDirty();
//    }
//
//    @Override
//    public int getInventoryStackLimit() {
//        return 64;
//    }
//
//    @Override
//    @ParametersAreNonnullByDefault
//    public boolean isUsableByPlayer(PlayerEntity player)
//    {
//        return this.world.getTileEntity(this.pos) == this &&
//                player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) < 64;
//    }
//
//    @Override
//    @ParametersAreNonnullByDefault
//    public void openInventory(PlayerEntity player) {
//
//    }
//
//    @Override
//    @ParametersAreNonnullByDefault
//    public void closeInventory(PlayerEntity player) {
//
//    }
//
//    @Override
//    @ParametersAreNonnullByDefault
//    public boolean isItemValidForSlot(int slot, ItemStack stack)
//    {
//        return stack.getItem() instanceof ItemFlooPowder;
//    }
//
//    @Override
//    public void clear() {
//
//    }
//
    @Override
    public void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        //boolean stackNullOrEmpty = tag.getBoolean("StackNullOrEmpty");
        //this.inv[0] = stackNullOrEmpty ? ItemStack.EMPTY : ItemStack.EMPTY;
        //DynamicOps<Tag> dynamicOps = provider.createSerializationContext(NbtOps.INSTANCE);
        //DataResult<ItemStack> result = ItemStack.OPTIONAL_CODEC.parse(dynamicOps, tag.getCompound("stack"));
        this.powderStackHandler.deserializeNBT(provider, tag.getCompound("stack"));
        //Clamp ranges between 2 and 5 inclusive
        this.hRangeSlot.set(Math.clamp(tag.getInt("hRange"), DataReference.POT_MIN_H_RANGE, DataReference.POT_MAX_H_RANGE));
        this.vRangeSlot.set(Math.clamp(tag.getInt("vRange"), DataReference.POT_MIN_V_RANGE, DataReference.POT_MAX_V_RANGE));
    }

    @Override
    public void saveAdditional(CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        //ItemStack stack = inv[0];
        //boolean stackNullOrEmpty = stack == null || stack.isEmpty();
        //DynamicOps<Tag> dynamicOps = provider.createSerializationContext(NbtOps.INSTANCE);
        //tag.putBoolean("StackNullOrEmpty", stackNullOrEmpty);
        //if (!stackNullOrEmpty) {
        //DataResult<Tag> result = ItemStack.OPTIONAL_CODEC.encodeStart(dynamicOps, stack);
        //result.resultOrPartial(LOGGER::error).ifPresent(encodedStack -> tag.put("stack", encodedStack));
        //}
        tag.put("stack", this.powderStackHandler.serializeNBT(provider));
        tag.putInt("hRange", this.hRangeSlot.get());
        tag.putInt("vRange", this.vRangeSlot.get());
    }

//    @Override
//    public SUpdateTileEntityPacket getUpdatePacket() {
//        return new SUpdateTileEntityPacket(this.pos, 0, this.write(new CompoundNBT()));
//    }
//
//    /**
//     * Called when you receive a TileEntityData packet for the location this
//     * TileEntity is currently in. On the client, the NetworkManager will always
//     * be the remote server. On the server, it will be whomever is responsible for
//     * sending the packet.
//     *
//     * @param net The NetworkManager the packet originated from
//     * @param pkt The data packet
//     */
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
//        this.read(null, pkt.getNbtCompound());
//        this.justUpdated = true;
//    }
//


    public void adjustPotRange(boolean useVerticalRange, int amount) {
        if (useVerticalRange) {
            this.vRangeSlot.set(Math.clamp(this.vRange + amount, DataReference.POT_MIN_V_RANGE, DataReference.POT_MAX_V_RANGE));
        } else {
            this.hRangeSlot.set(Math.clamp(this.hRange + amount, DataReference.POT_MIN_H_RANGE, DataReference.POT_MAX_H_RANGE));
        }
        this.setChanged();
    }

    public int getHRange() {
        return this.hRangeSlot.get();
    }

    public int getVRange() {
        return this.vRangeSlot.get();
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
