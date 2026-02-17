package com.leafia.contents.machines.misc.heatex.container;

import com.hbm.inventory.slot.SlotBattery;
import com.leafia.contents.machines.misc.heatex.CoolantHeatexTE;
import com.leafia.dev.container_utility.LeafiaItemTransferable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class CoolantHeatexContainer extends LeafiaItemTransferable {

	private final CoolantHeatexTE heatex;

	public CoolantHeatexContainer(InventoryPlayer invPlayer,CoolantHeatexTE heatex) {

		this.heatex = heatex;

		//Fluid ID
		this.addSlotToContainer(new SlotItemHandler(heatex.inventory, 0, 17, 72));
		//Battery
		this.addSlotToContainer(new SlotBattery(heatex.inventory, 1, 152, 72));

		// Inventory
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer,j+i*9+9,8+j*18,122+i*18));
		}
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer,i,8+i*18,180));
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int clickIndex)
    {
	    LeafiaItemTransfer transfer = new LeafiaItemTransfer(2)._selected(clickIndex);
	    transfer.__forSlots(0,9999)
			    .__tryMoveToInventory(true)

			    .__forInventory()

			    .__tryMoveToSlot(0,2,false);
	    return transfer.__tryMoveToSlot(3,transfer.__maxIndex,false)
			    .__getReturn();
    }

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return heatex.isUseableByPlayer(player);
	}
}
