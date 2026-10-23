// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.base.container;

import com.leafia.contents.machines.powercores.ams.base.AMSBaseTE;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IContainerListener;

public class AMSBaseContainer extends LeafiaItemTransferable {
    private static final int[] MACHINE_POSITIONS = {8,18,8,54,152,18,152,54,8,72,8,108,152,72,152,108,80,45,62,63,98,63,80,81,80,63,62,108,80,108,98,108};
    private final AMSBaseTE machine;

    public AMSBaseContainer(InventoryPlayer player, AMSBaseTE machine) {
        this.machine = machine;
        addMachineSlots(machine.inventory, MACHINE_POSITIONS);
        addPlayerInventory(player, 8, 140);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return shiftClick(index, 3, 4, NO_IMPORT_RANGES);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return machine.isUseableByPlayer(player);
    }
    private int sentHeat;
    private int sentWarning;
    private int sentMode;

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        sendProperties(listener, true);
    }

    private void sendProperties(IContainerListener listener, boolean initial) {
        if (initial || sentHeat != machine.heat) listener.sendWindowProperty(this, 0, machine.heat);
        if (initial || sentWarning != machine.warning) listener.sendWindowProperty(this, 2, machine.warning);
        if (initial || sentMode != machine.mode) listener.sendWindowProperty(this, 4, machine.mode);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (var listener : listeners) sendProperties(listener, false);
        sentHeat = machine.heat;
        sentWarning = machine.warning;
        sentMode = machine.mode;
    }

    @Override
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0 -> machine.heat = value;
            case 1 -> machine.efficiency = value;
            case 2 -> machine.warning = value;
            case 3 -> machine.field = value;
            case 4 -> machine.mode = value;
        }
    }
}
