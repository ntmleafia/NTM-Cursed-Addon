// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.container_utility;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
public abstract class LeafiaItemTransferable extends Container {
    protected static final int[] NO_IMPORT_RANGES = {};

    protected final void addPlayerInventory(InventoryPlayer inventory, int left, int top) {
        for (var index = 0; index < 27; index++) {
            addSlotToContainer(new Slot(inventory, index + 9, left + index % 9 * 18, top + index / 9 * 18));
        }
        for (var index = 0; index < 9; index++) {
            addSlotToContainer(new Slot(inventory, index, left + index * 18, top + 58));
        }
    }

    protected final void addMachineSlots(net.minecraftforge.items.IItemHandler inventory, int[] coordinates) {
        for (var index = 0; index < coordinates.length / 2; index++) {
            addSlotToContainer(new net.minecraftforge.items.SlotItemHandler(
                    inventory, index, coordinates[index * 2], coordinates[index * 2 + 1]));
        }
    }

    /** Ranges are explicit because some machine interfaces have specialized routing. */
    protected final ItemStack shiftClick(int index, int lastExportSlot, int exportStart, int[] importRanges) {
        var slot = inventorySlots.get(index);
        if (!slot.getHasStack()) return ItemStack.EMPTY;
        var remaining = slot.getStack();
        var original = remaining.copy();
        var moved = false;
        if (index <= lastExportSlot) {
            moved = mergeItemStack(remaining, exportStart, inventorySlots.size(), true);
        } else {
            for (var range = 0; range < importRanges.length && !moved; range += 2) {
                moved = mergeItemStack(remaining, importRanges[range], importRanges[range + 1], false);
            }
        }
        if (!moved) return ItemStack.EMPTY;
        if (remaining.isEmpty()) slot.putStack(ItemStack.EMPTY);
        else slot.onSlotChanged();
        return original;
    }

    public class LeafiaItemTransfer {
        short slots;
        boolean __changed = false; // "true" when the affected stack was changed
        short selectedIndex;
        Slot selectedSlot;
        ItemStack backup;
        ItemStack stack;
        ItemStack __return = null;
        short sourceSlotStart = 0;
        short sourceSlotEnd = 0;

        public short __maxIndex = 0; // the maximum index of this machine's own inventory

        public LeafiaItemTransfer(int slots) {
            // INFO: in other words, the "slots" argument should be the highest slot index plus 1.
            this.slots = (short)slots;
            this.__maxIndex = (short)(slots-1);
        }

        public LeafiaItemTransfer _selected(int clickIndex) {
            this.selectedIndex = (short) clickIndex;
            this.selectedSlot = inventorySlots.get(clickIndex);
            if (selectedSlot == null || !selectedSlot.getHasStack())
                __return = ItemStack.EMPTY;
            else {
                this.stack = this.selectedSlot.getStack();
                this.backup = this.stack.copy();
            }
            return this;
        }

        boolean moveItem(int targetSlotStart, int targetSlotEnd, boolean reverse) {
            return mergeItemStack(stack,targetSlotStart,targetSlotEnd+1,reverse);
        }

        public LeafiaItemTransfer __forSlots(int sourceSlotStart, int sourceSlotEnd) {
            this.sourceSlotStart = (short)Math.min(sourceSlotStart,__maxIndex);
            this.sourceSlotEnd = (short)Math.min(sourceSlotEnd,__maxIndex);
            return this;
        }
        public LeafiaItemTransfer __forInventory() {
            this.sourceSlotStart = __maxIndex;
            this.sourceSlotEnd = (short)inventorySlots.size();
            return this;
        }

        public LeafiaItemTransfer __tryMoveToSlot(int targetSlotStart, int targetSlotEnd, boolean reverse) {
            if ((__return == null) && !__changed && !stack.isEmpty() && (selectedIndex >= sourceSlotStart) && (selectedIndex <= sourceSlotEnd)) {
                __changed = moveItem(targetSlotStart, targetSlotEnd, reverse);
            }
            return this;
        }
        public LeafiaItemTransfer __tryMoveToInventory(boolean reverse) {
            return this.__tryMoveToSlot(this.slots,inventorySlots.size()-1,reverse);
        }

        public ItemStack __getReturn() {
            if (__return != null) return __return;
            if (!__changed)
                return ItemStack.EMPTY; // tell it to stop
            if (stack.isEmpty())
                this.selectedSlot.putStack(ItemStack.EMPTY);
            else
                this.selectedSlot.onSlotChanged();
            return this.backup; // tell it to continue
        }
    }
}
