package com.fredtargaryen.floocraft.inventory;

import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftMenuTypes;
import com.fredtargaryen.floocraft.item.FlooPowderItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FloowerPotMenu extends AbstractContainerMenu {
    private final Container pot;

    /**
     * Client-side constructor
     */
    public FloowerPotMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(1));
    }

    /**
     * Server-side constructor
     */
    public FloowerPotMenu(int containerId, Inventory playerInventory, Container container) {
        super(FloocraftMenuTypes.FLOOWER_POT.get(), containerId);
        checkContainerSize(container, 1);
        this.pot = container;
        container.startOpen(playerInventory.player);

        this.addSlot(new PowderSlot(playerInventory));
    }

    // AbstractContainerMenu overrides
    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();
            if (index < 1) {
                if (!this.moveItemStackTo(stackInSlot, 1, 1, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return itemstack;
    }

    /**
     * Determines whether supplied player can use this container
     */
    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(ContainerLevelAccess.NULL, player, FloocraftBlocks.FLOOWER_POT.get());
    }

    private class PowderSlot extends Slot {
        public PowderSlot(Inventory inventory) {
            super(inventory, 0, 80, 35);
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty() || stack.getItem() instanceof FlooPowderItem;
        }
    }
}
