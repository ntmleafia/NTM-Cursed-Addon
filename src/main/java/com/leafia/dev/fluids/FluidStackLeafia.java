// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.fluids;

import com.hbm.inventory.fluid.FluidType;
import net.minecraft.nbt.NBTTagCompound;

public class FluidStackLeafia {
	public final FluidType type;
	public final int amount;
	public final NBTTagCompound nbt;
	public FluidStackLeafia(FluidType type,int amount,NBTTagCompound nbt) {
		this.type = type;
		this.amount = amount;
		this.nbt = nbt;
	}
	public FluidStackLeafia(FluidType type,int amount) {
		this(type,amount,null);
	}
}
