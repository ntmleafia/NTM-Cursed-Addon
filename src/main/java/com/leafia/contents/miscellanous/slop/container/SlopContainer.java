// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.miscellanous.slop.container;

import com.hbm.interfaces.IContainerOpenEventListener;
import com.leafia.contents.miscellanous.slop.SlopTE;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlopContainer extends LeafiaItemTransferable implements IContainerOpenEventListener {

	private SlopTE entity;


	public SlopContainer(InventoryPlayer invPlayer,SlopTE entity) {
		this.entity = entity;
		this.addSlotToContainer(new SlotItemHandler(entity.inventory, 0, 8, 35-18));
		this.addSlotToContainer(new SlotItemHandler(entity.inventory, 1, 80, 53));

		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
		}
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int clickIndex)
    {
		// stupid obfuscated code, let me fix it
		// apparently this is what they use for when you Shift+click item in inventory to transfer it.
		// ACTUALLY i don't know much about what's going on here. Scram!
		// UPDATE: Here, I fixed this godforsaken code.
		LeafiaItemTransfer transfer = new LeafiaItemTransfer(2)._selected(clickIndex);
		return transfer.__forSlots(0,9999)
				.__tryMoveToInventory(true)

				.__forInventory()
				.__tryMoveToSlot(0,transfer.__maxIndex,false)

				.__getReturn();
		/*
		ItemStack _signalStack = ItemStack.EMPTY;
		Slot clickSlot = (Slot) this.inventorySlots.get(clickIndex);
		
		if (clickSlot != null && clickSlot.getHasStack())
		{
			ItemStack stack = clickSlot.getStack();
			_signalStack = stack.copy(); // setting it to a backup of the original tells the game to continue trying
			// stupid, but that's how it works :/

			// indexes greater than the machine's inventory is considered the player's inventory.
			// mergeItemStack will try and move item to specied slots (and merge if possible).
			//    They return false if the item did not transfer.

			//    When absolutely none of the slots were changed, this process should immediately quit,
			//      otherwise the game keeps on doing the same thing forever as nothing happens to stop it!
            if (clickIndex < 28) {
				if (!this.mergeItemStack(stack, 28, this.inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			} else {
				return ItemStack.EMPTY;
			}
            
			if (stack.isEmpty())
			{
				clickSlot.putStack(ItemStack.EMPTY);
			}
			else
			{
				clickSlot.onSlotChanged();
			}
		}
		
		return _signalStack;*/
    }

	@Override
	public void onContainerOpened(EntityPlayer entityPlayer) {
		entity.listeners.add(entityPlayer);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		entity.listeners.remove(playerIn);
		super.onContainerClosed(playerIn);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return entity.isUseableByPlayer(player);
	}
}
