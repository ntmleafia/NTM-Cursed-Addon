package com.leafia.contents.machines.reactors.apr.blocks.core.container;

import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class APRMainContainer extends LeafiaItemTransferable {
	public final APRCoreTE te;
	public APRMainContainer(EntityPlayer player,APRCoreTE te) {
		this.te = te;
		InventoryPlayer invPlayer = player.inventory;
		// particle io
		this.addSlotToContainer(new SlotItemHandler(te.inventory,0,117,96));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,1,135,96));
		// main coolant
		this.addSlotToContainer(new SlotItemHandler(te.inventory,2,84,25));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,3,84,61));
		// chamber fluids
		this.addSlotToContainer(new SlotItemHandler(te.inventory,4,8,25));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,5,8,61));
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer,j+i*9+9,17+8+j*18,138+i*18));
		}
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer,i,17+8+i*18,196));
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn,int index) {
		LeafiaItemTransfer transfer = new LeafiaItemTransfer(6)._selected(index);
		return transfer.__forSlots(0,9999)
				.__tryMoveToInventory(true)

				.__forInventory()
				.__tryMoveToSlot(0,transfer.__maxIndex,false)

				.__getReturn();
	}
}
