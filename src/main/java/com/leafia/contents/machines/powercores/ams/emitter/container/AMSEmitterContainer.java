// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.powercores.ams.emitter.container;

import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterTE;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IContainerListener;

public class AMSEmitterContainer extends LeafiaItemTransferable {
    private static final int[] MACHINE_POSITIONS = {44,17,44,53,80,53,116,53};
    private final AMSEmitterTE machine;

    public AMSEmitterContainer(InventoryPlayer player, AMSEmitterTE machine) {
        this.machine = machine;
        addMachineSlots(machine.inventory, MACHINE_POSITIONS);
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
    private int sentHeat;
    private int sentWarning;

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        sendProperties(listener, true);
    }

    private void sendProperties(IContainerListener listener, boolean initial) {
        if (initial || sentHeat != machine.heat) listener.sendWindowProperty(this, 0, machine.heat);
        if (initial || sentWarning != machine.warning) listener.sendWindowProperty(this, 2, machine.warning);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (var listener : listeners) sendProperties(listener, false);
        sentHeat = machine.heat;
        sentWarning = machine.warning;
    }

    @Override
    public void updateProgressBar(int id, int value) {
        switch (id) {
            case 0 -> machine.heat = value;
            case 2 -> machine.warning = value;
        }
    }
}
