// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.building.storage.broof.container;

import com.hbm.inventory.TransferStrategy;
import com.hbm.util.InventoryUtil;
import com.leafia.contents.building.storage.broof.BroofTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class BroofContainer extends Container {

	private BroofTE diFurnace;

	public BroofContainer(InventoryPlayer invPlayer,BroofTE te) {
		diFurnace = te;
		
		for(int i = 0; i < 6; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new SlotItemHandler(te.inventory, j + i * 9, 8 + j * 18, 18 + i * 18));
			}
		}
		
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + (18 * 3) + 2));
			}
		}
		
		for(int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142 + (18 * 3) + 2));
		}
	}

	private static final TransferStrategy TRANSFER_STRATEGY = TransferStrategy.builder(54)
			.genericMachineRange(0)
			.build();

	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return InventoryUtil.transferStack(this.inventorySlots,index,TRANSFER_STRATEGY,player);
    }

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
}
