package com.leafia.contents.machines.powercores.dfc.components.pulser;

import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class CoreDetonatorContainer extends LeafiaItemTransferable {
	public final CoreDetonatorTE te;
	public CoreDetonatorContainer(EntityPlayer player,CoreDetonatorTE te) {
		this.te = te;
		InventoryPlayer invPlayer = player.inventory;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer,j+i*9+9,8+j*18,88+i*18));
		}
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer,i,8+i*18,146));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return te.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int par2) {
		ItemStack var3 = ItemStack.EMPTY;
		Slot var4 = (Slot) this.inventorySlots.get(par2);

		if(var4 != null && var4.getHasStack()) {
			return ItemStack.EMPTY;
		}

		return var3;
	}
}
