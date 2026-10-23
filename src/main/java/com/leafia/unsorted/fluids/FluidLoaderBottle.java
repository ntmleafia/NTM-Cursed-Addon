// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted.fluids;

import com.hbm.capability.NTMFluidCapabilityHandler;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.inventory.fluid.tank.IFluidLoadingHandler;
import com.leafia.contents.AddonItems;
import com.leafia.contents.gear.ntmfbottle.ItemNTMFBottle;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class FluidLoaderBottle implements IFluidLoadingHandler {
	@Override
	public boolean fillItem(IItemHandler slots,int in,int out,FluidTankNTM tank) {
		if(tank.getPressure() != 0) return false;
		ItemStack inputStack = slots.extractItem(in, 1, true);
		if(inputStack.isEmpty()) return true;
		if (!(inputStack.getItem().equals(Items.GLASS_BOTTLE))) return false;
		FluidType type = tank.getTankType();
		if (!ItemNTMFBottle.isValidFluid(type)) return false;
		ItemStack bottle = new ItemStack(AddonItems.ntmfbottle,1,type.getID());
		int remainder = tank.getFill() - 333;
		if(remainder >= 0) {
			if(slots.insertItem(out, bottle, true).isEmpty()) {
				tank.setFill(remainder);
				slots.insertItem(out, bottle, false);
				slots.extractItem(in, 1, false);
			}
		}
		return false;
	}
	@Override
	public boolean emptyItem(IItemHandler slots, int in, int out, FluidTankNTM tank) {
		ItemStack inputStack = slots.extractItem(in, 1, true);
		if (inputStack.isEmpty()) return true;
		if (!(inputStack.getItem() instanceof ItemNTMFBottle)) return false;
		FluidType itemFluidType = ItemNTMFBottle.getFluidFromStack(inputStack);
		if (itemFluidType == Fluids.NONE) return false;
		//1.7 uses FluidContainerRegistry.getFluidContent(stackIn, tankType) here. logic changed to make
		//empty tanks change its type automatically to the fluid type of the container
		int amount = 333;
		if (tank.getTankType() != Fluids.NONE && tank.getTankType() != itemFluidType) return false;
		if (tank.getFill() + amount > tank.getMaxFill()) return false;

		ItemStack emptyContainer = new ItemStack(Items.GLASS_BOTTLE);//a copy
		if (slots.insertItem(out, emptyContainer, true).isEmpty()) {
			if (tank.getTankType() == Fluids.NONE) tank.setTankType(itemFluidType);
			tank.setFill(tank.getFill() + amount);
			slots.extractItem(in, 1, false);
			slots.insertItem(out, emptyContainer, false);
		}
		return true;
	}
}
