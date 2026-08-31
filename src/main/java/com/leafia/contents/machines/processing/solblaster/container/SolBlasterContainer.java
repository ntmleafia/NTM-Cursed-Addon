package com.leafia.contents.machines.processing.solblaster.container;

import com.hbm.interfaces.IContainerOpenEventListener;
import com.leafia.contents.machines.processing.solblaster.SolBlasterTE;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SolBlasterContainer extends LeafiaItemTransferable implements IContainerOpenEventListener {
	public final SolBlasterTE te;
	public SolBlasterContainer(EntityPlayer player,SolBlasterTE te) {
		this.te = te;
		InventoryPlayer invPlayer = player.inventory;
		this.addSlotToContainer(new SlotItemHandler(te.inventory,0,23,23));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,1,8,77));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,2,8+18,77));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,3,8,77+18));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,4,8+18,77+18));
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 3; y++)
				this.addSlotToContainer(new SlotItemHandler(te.inventory,5+x+y*5,80+x*18,59+y*18));
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer,j+i*9+9,8+j*18,145+i*18));
		}
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer,i,8+i*18,203));
	}
	@Override
	public void onContainerOpened(EntityPlayer entityPlayer) {
		te.listeners.add(entityPlayer);
	}
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		te.listeners.remove(playerIn);
		super.onContainerClosed(playerIn);
	}
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return te.isUseableByPlayer(player);
	}
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn,int index) {
		LeafiaItemTransfer transfer = new LeafiaItemTransfer(20)._selected(index);
		return transfer.__forSlots(0,9999)
				.__tryMoveToInventory(true)

				.__forInventory()
				.__tryMoveToSlot(0,transfer.__maxIndex,false)

				.__getReturn();
	}
}
