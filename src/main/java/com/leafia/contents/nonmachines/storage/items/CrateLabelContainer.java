// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.nonmachines.storage.items;

import com.hbm.tileentity.machine.storage.TileEntityCrateBase;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCrate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class CrateLabelContainer extends Container {
	final TileEntityCrateBase te;
	public CrateLabelContainer(InventoryPlayer invPlayer,TileEntityCrateBase te) {
		this.te = te;
		this.addSlotToContainer(new SlotItemHandler(((IMixinTileEntityCrate)te).leafia$getIconHandler(), 0, 62, 35));
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 103 + i * 18));
			}
		}

		for(int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 161));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn,int index) {
		return ItemStack.EMPTY;
	}
}
