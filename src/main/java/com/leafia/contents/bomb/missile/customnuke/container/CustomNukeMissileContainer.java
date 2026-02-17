package com.leafia.contents.bomb.missile.customnuke.container;

import com.hbm.tileentity.bomb.TileEntityNukeCustom;
import com.hbm.util.InventoryUtil;
import com.leafia.contents.bomb.missile.customnuke.CustomNukeMissileItem.CustomNukeMissileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class CustomNukeMissileContainer extends Container {

	private CustomNukeMissileInventory nukeBoy;
	private boolean isMainHand;

	public CustomNukeMissileContainer(InventoryPlayer invPlayer,CustomNukeMissileInventory tedf) {
		
		nukeBoy = tedf;
		this.isMainHand = tedf.player.getHeldItemMainhand() == tedf.box;

		this.addSlotToContainer(new SlotItemHandler(tedf, 0, 8, 18));
		this.addSlotToContainer(new SlotItemHandler(tedf, 1, 26, 18));
		this.addSlotToContainer(new SlotItemHandler(tedf, 2, 44, 18));
		this.addSlotToContainer(new SlotItemHandler(tedf, 3, 62, 18));
		this.addSlotToContainer(new SlotItemHandler(tedf, 4, 80, 18));
		this.addSlotToContainer(new SlotItemHandler(tedf, 5, 98, 18));
		this.addSlotToContainer(new SlotItemHandler(tedf, 6, 116, 18));
		this.addSlotToContainer(new SlotItemHandler(tedf, 7, 134, 18));
		this.addSlotToContainer(new SlotItemHandler(tedf, 8, 152, 18));
		this.addSlotToContainer(new SlotItemHandler(tedf, 9, 8, 36));
		this.addSlotToContainer(new SlotItemHandler(tedf, 10, 26, 36));
		this.addSlotToContainer(new SlotItemHandler(tedf, 11, 44, 36));
		this.addSlotToContainer(new SlotItemHandler(tedf, 12, 62, 36));
		this.addSlotToContainer(new SlotItemHandler(tedf, 13, 80, 36));
		this.addSlotToContainer(new SlotItemHandler(tedf, 14, 98, 36));
		this.addSlotToContainer(new SlotItemHandler(tedf, 15, 116, 36));
		this.addSlotToContainer(new SlotItemHandler(tedf, 16, 134, 36));
		this.addSlotToContainer(new SlotItemHandler(tedf, 17, 152, 36));
		this.addSlotToContainer(new SlotItemHandler(tedf, 18, 8, 54));
		this.addSlotToContainer(new SlotItemHandler(tedf, 19, 26, 54));
		this.addSlotToContainer(new SlotItemHandler(tedf, 20, 44, 54));
		this.addSlotToContainer(new SlotItemHandler(tedf, 21, 62, 54));
		this.addSlotToContainer(new SlotItemHandler(tedf, 22, 80, 54));
		this.addSlotToContainer(new SlotItemHandler(tedf, 23, 98, 54));
		this.addSlotToContainer(new SlotItemHandler(tedf, 24, 116, 54));
		this.addSlotToContainer(new SlotItemHandler(tedf, 25, 134, 54));
		this.addSlotToContainer(new SlotItemHandler(tedf, 26, 152, 54));
		
		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 9; j++)
			{
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 56));
			}
		}
		
		for(int i = 0; i < 9; i++)
		{
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142 + 56));
		}
	}


	@Override
	public ItemStack slotClick(int slotId,int dragType,ClickType clickTypeIn,EntityPlayer player) {
		if(isMainHand) {
			if(clickTypeIn == ClickType.SWAP && dragType == player.inventory.currentItem) return ItemStack.EMPTY;
			int heldSlot = 27+27 + player.inventory.currentItem;
			if(slotId == heldSlot) return ItemStack.EMPTY;
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
		return InventoryUtil.transferStack(this.inventorySlots, index, 27);
    }

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}