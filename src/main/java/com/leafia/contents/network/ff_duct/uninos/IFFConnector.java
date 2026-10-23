// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.ff_duct.uninos;

import com.hbm.lib.ForgeDirection;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public interface IFFConnector {
	/**
	 * Whether the given side can be connected to
	 */
	default boolean canConnect(FluidStack stack,ForgeDirection dir) {
		if (this instanceof TileEntity te) {
			return te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,dir.toEnumFacing());
		}
		return dir != ForgeDirection.UNKNOWN;
	}
}
