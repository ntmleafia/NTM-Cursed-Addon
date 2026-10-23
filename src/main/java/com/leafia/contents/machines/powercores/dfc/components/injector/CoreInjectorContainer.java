// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.dfc.components.injector;

import com.hbm.inventory.slot.SlotTakeOnly;
import com.hbm.tileentity.machine.TileEntityCoreInjector;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class CoreInjectorContainer extends LeafiaItemTransferable {
    private final TileEntityCoreInjector machine;

    public CoreInjectorContainer(InventoryPlayer player, TileEntityCoreInjector machine) {
        this.machine = machine;
        addSlotToContainer(new SlotItemHandler(machine.inventory, 0, 15, 16));
        addSlotToContainer(new SlotTakeOnly(machine.inventory, 1, 15, 52));
        addSlotToContainer(new SlotItemHandler(machine.inventory, 2, 146, 17));
        addSlotToContainer(new SlotTakeOnly(machine.inventory, 3, 146, 53));
        addPlayerInventory(player, 8, 84);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return shiftClick(index, 3, 4, NO_IMPORT_RANGES);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return machine.isUseableByPlayer(player);
    }
}
