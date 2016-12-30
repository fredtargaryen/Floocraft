package com.fredtargaryen.floocraft.inventory.container;

import com.fredtargaryen.floocraft.item.ItemFlooPowder;
import com.fredtargaryen.floocraft.tileentity.TileEntityFloowerPot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFloowerPot extends Container
{
    private final TileEntityFloowerPot potTE;

    private class PowderSlot extends Slot
    {
        public PowderSlot(IInventory par1IInventory)
        {
            super(par1IInventory, 0, 80, 35);
        }

        /**
         * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
         */
        public boolean isItemValid(ItemStack par1ItemStack)
        {
            return par1ItemStack.getItem() instanceof ItemFlooPowder;
        }
    }

    public ContainerFloowerPot (InventoryPlayer inventoryPlayer, TileEntityFloowerPot te)
    {
        potTE = te;
        //the Slot constructor takes the IInventory and the slot number in that it binds to
        //and the x-y coordinates it resides on-screen
        addSlotToContainer(new PowderSlot(potTE));
        //commonly used vanilla code that adds the player's inventory
        bindPlayerInventory(inventoryPlayer);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return potTE.isUseableByPlayer(player);
    }

    private void bindPlayerInventory(InventoryPlayer inventoryPlayer)
    {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                        8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        ItemStack stack = null;
        Slot slotObject = (Slot) inventorySlots.get(slot);

        //null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            //merges the item into player inventory since its in the tileEntity
            if (slot < 1) {
                if (!this.mergeItemStack(stackInSlot, 1, this.inventorySlots.size(), true)) {
                    return null;
                }
            }
            //places it into the tileEntity is possible since its in the player inventory
            else if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                return null;
            }

            if (stackInSlot.func_190916_E() == 0) {
                slotObject.putStack(ItemStack.field_190927_a);
            } else {
                slotObject.onSlotChanged();
            }

            if (stackInSlot.func_190916_E() == stack.func_190916_E()) {
                return null;
            }
            slotObject.func_190901_a(player, stackInSlot);
        }
        return stack;
    }
}