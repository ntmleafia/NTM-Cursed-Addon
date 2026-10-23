// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.heat.rtheater.container;

import com.leafia.contents.machines.heat.rtheater.HeaterRTGTE;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class HeaterRTGContainer extends LeafiaItemTransferable {
    private static final int[] MACHINE_POSITIONS = {26,22,44,22,62,22,80,22,98,22,26,40,44,40,62,40,80,40,98,40,26,58,44,58,62,58,80,58,98,58,4,40};
    private static final int[] IMPORT_RANGES = {0,15};
    private final HeaterRTGTE machine;

    public HeaterRTGContainer(InventoryPlayer player, HeaterRTGTE machine) {
        this.machine = machine;
        addMachineSlots(machine.inventory, MACHINE_POSITIONS);
        addPlayerInventory(player, 8, 94);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return shiftClick(index, 14, 15, IMPORT_RANGES);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return machine.isUseableByPlayer(player);
    }
}
