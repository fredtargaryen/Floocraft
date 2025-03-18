package com.fredtargaryen.floocraft.inventory;

import com.fredtargaryen.floocraft.FloocraftBlocks;
import com.fredtargaryen.floocraft.FloocraftMenuTypes;
import com.fredtargaryen.floocraft.item.FlooPowderItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FloowerPotMenu extends AbstractContainerMenu {
    // Need to save any objects from the constructor that are used to interface with other code;
    // in this case, Screen and Message code
    private final DataSlot hRangeSlot;
    private final DataSlot vRangeSlot;
    private final ContainerData pos;

    /**
     * Client-side constructor, which calls the other one with placeholder args
     */
    public FloowerPotMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ItemStackHandler(1), DataSlot.standalone(), DataSlot.standalone(), new SimpleContainerData(3));
    }

    /**
     * Server-side constructor
     */
    public FloowerPotMenu(int containerId, Inventory playerInventory, IItemHandler dataInventory, DataSlot hRangeSlot, DataSlot vRangeSlot, SimpleContainerData pos) {
        super(FloocraftMenuTypes.FLOOWER_POT.get(), containerId);

        // Slot for Floo Powder to go into
        this.addSlot(new PowderSlotItemHandler(dataInventory));

        // Make slots for the player's inventory. Copied from CraftingMenu
        for (int k = 0; k < 3; k++) {
            for (int i1 = 0; i1 < 9; i1++) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }

        // Data slots for pot update range info
        this.hRangeSlot = hRangeSlot;
        this.addDataSlot(hRangeSlot);
        this.vRangeSlot = vRangeSlot;
        this.addDataSlot(vRangeSlot);

        // Position of pot block entity, for updating range info
        this.pos = pos;
        this.addDataSlots(this.pos);
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
                if (!this.moveItemStackTo(stackInSlot, 1, 37, true)) {
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

    private class PowderSlotItemHandler extends SlotItemHandler {
        public PowderSlotItemHandler(IItemHandler itemHandler) {
            super(itemHandler, 0, 80, 35);
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty() || stack.getItem() instanceof FlooPowderItem;
        }
    }

    /**
     * Used by @link{FloowerPotScreen} to update the displayed value of the pot's horizontal range
     */
    public int getHorizontalRange() {
        return this.hRangeSlot.get();
    }

    /**
     * Used by @link{FloowerPotScreen} to update the displayed value of the pot's vertical range
     */
    public int getVerticalRange() {
        return this.vRangeSlot.get();
    }

    /**
     * Used by @link{FloowerPotScreen} to get the pot's position
     */
    public BlockPos getBlockPosition() {
        return new BlockPos(
                pos.get(0),
                pos.get(1),
                pos.get(2));
    }
}
