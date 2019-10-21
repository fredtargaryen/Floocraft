package com.fredtargaryen.floocraft.inventory.container;

import com.fredtargaryen.floocraft.FloocraftBase;
import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.tileentity.FloowerPotTileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.IContainerFactory;

public class FloowerPotContainer extends Container {
    private TileEntity potTE;

    private class PowderSlot extends Slot {
        public PowderSlot(IInventory par1IInventory) {
            super(par1IInventory, 0, 80, 35);
        }

        /**
         * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
         */
        public boolean isItemValid(ItemStack par1ItemStack) {
            return par1ItemStack.isEmpty() || par1ItemStack.getItem() instanceof ItemFlooPowder ;
        }
    }

    /**
     * CLIENT side constructor for the container
     * @param windowId
     * @param inv
     */
    public FloowerPotContainer(int windowId, PlayerInventory inv, World w, BlockPos pos) {
        super(FloocraftBase.POT_CONTAINER_TYPE, windowId);
        this.potTE = w.getTileEntity(pos);
        if(this.potTE != null) {
            this.addSlot(new PowderSlot((FloowerPotTileEntity) this.potTE));
        }
        this.addPlayerInventorySlots(inv);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }

    private void addPlayerInventorySlots(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9,
                        8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack transferStackInSlot(PlayerEntity player, int slot) {
        ItemStack stack = null;
        Slot slotObject = inventorySlots.get(slot);

        //null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            //merges the item into player inventory since its in the tileEntity
            if (slot < 1) {
                if (!this.mergeItemStack(stackInSlot, 1, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            //places it into the tileEntity is possible since its in the player inventory
            else if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.getCount() == 0) {
                slotObject.putStack(ItemStack.EMPTY);
            } else {
                slotObject.onSlotChanged();
            }

            if (stackInSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            slotObject.onTake(player, stackInSlot);
        }
        return stack == null ? ItemStack.EMPTY : stack;
    }

    public TileEntity getTileEntity() {
        return this.potTE;
    }
}