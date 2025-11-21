package com.leafia.dev.machine;

import com.hbm.lib.ItemStackHandlerWrapper;
import com.hbm.tileentity.TileEntityMachineBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class LCETileEntityMachineBase extends TileEntityMachineBase {
	public LCETileEntityMachineBase(int scount) {
		super(scount);
	}
	public LCETileEntityMachineBase(int scount,boolean enableFluidWrapper,boolean enableEnergyWrapper) {
		super(scount,enableFluidWrapper,enableEnergyWrapper);
	}
	public LCETileEntityMachineBase(int scount,int slotlimit) {
		super(scount,slotlimit);
	}
	public LCETileEntityMachineBase(int scount,int slotlimit,boolean enableFluidWrapper,boolean enableEnergyWrapper) {
		super(scount,slotlimit,enableFluidWrapper,enableEnergyWrapper);
	}

	public void slotContentsChanged(int slot,ItemStack newStack) { }
	public ItemStackHandler getNewInventory(int scount,int slotlimit){
		return new ItemStackHandler(scount){
			@Override
			protected void onContentsChanged(int slot) {
				super.onContentsChanged(slot);
				slotContentsChanged(slot,this.getStackInSlot(slot));
				markDirty();
			}

			@Override
			public int getSlotLimit(int slot) {
				return slotlimit;
			}

			@Override
			public boolean isItemValid(int i, ItemStack itemStack) {
				return isItemValidForSlot(i,itemStack);
			}
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if(canInsertItem(slot, stack, stack.getCount()))
					return super.insertItem(slot, stack, simulate);
				return stack;
			}
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				validateSlotIndex(slot);
				ItemStack stack = stacks.get(slot);
				if (stack.isEmpty()) return ItemStack.EMPTY;
				return canExtractItem(slot,stack,amount) ? super.extractItem(slot,amount,simulate) : ItemStack.EMPTY;
			}
		};
	}
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return true;
	}
	public boolean isItemValidForSlotHopper(int i, ItemStack stack) {
		return true;
	}
	public boolean canInsertItem(int slot, ItemStack itemStack, int amount) {
		return this.isItemValidForSlot(slot, itemStack);
	}
	public boolean canExtractItem(int slot, ItemStack itemStack, int amount) {
		return true;
	}
	public boolean canInsertItemHopper(int slot, ItemStack itemStack, int amount) {
		return this.isItemValidForSlotHopper(slot, itemStack);
	}
	public boolean canExtractItemHopper(int slot, ItemStack itemStack, int amount) {
		return true;
	}
	@Override
	public <T> T getCapability(Capability<T> capability,EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && inventory != null){
			if(facing == null)
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new ItemStackHandlerWrapper(inventory, getAccessibleSlotsFromSide(facing)){
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					if(canExtractItemHopper(slot, inventory.getStackInSlot(slot), amount) && canExtractItem(slot, inventory.getStackInSlot(slot), amount))
						return super.extractItem(slot, amount, simulate);
					return ItemStack.EMPTY;
				}

				@Override
				public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
					if(canInsertItemHopper(slot, stack, stack.getCount()) && canInsertItem(slot, stack, stack.getCount()))
						return super.insertItem(slot, stack, simulate);
					return stack;
				}
			});
		}
		return super.getCapability(capability, facing);
	}
}
