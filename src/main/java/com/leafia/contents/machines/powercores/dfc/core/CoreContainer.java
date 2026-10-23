// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.dfc.core;

import com.hbm.tileentity.machine.TileEntityCore;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class CoreContainer extends LeafiaItemTransferable {
    private static final int[] MACHINE_POSITIONS = {80,7,80,39,80,71};
    private static final int[] IMPORT_RANGES = {0,1,1,2,2,3};
    private final TileEntityCore machine;

    public CoreContainer(InventoryPlayer player, TileEntityCore machine) {
        this.machine = machine;
        addMachineSlots(machine.inventory, MACHINE_POSITIONS);
        addPlayerInventory(player, 8, 122);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return shiftClick(index, 2, 2, IMPORT_RANGES);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return machine.isUseableByPlayer(player);
    }
}
