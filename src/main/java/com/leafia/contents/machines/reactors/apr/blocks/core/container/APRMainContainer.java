package com.leafia.contents.machines.reactors.apr.blocks.core.container;

import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

public class APRMainContainer extends LeafiaItemTransferable {
	public final APRCoreTE te;
	public APRMainContainer(EntityPlayer player,APRCoreTE te) {
		this.te = te;
		InventoryPlayer invPlayer = player.inventory;
		this.addSlotToContainer(new SlotItemHandler(te.inventory,0,117,96));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,1,135,96));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,2,84,25));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,3,84,61));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,4,8,25));
		this.addSlotToContainer(new SlotItemHandler(te.inventory,5,8,61));
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer,j+i*9+9,17+8+j*18,137+i*18));
		}
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer,i,17+8+i*18,195));
	}
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.isUseableByPlayer(playerIn);
	}
}
