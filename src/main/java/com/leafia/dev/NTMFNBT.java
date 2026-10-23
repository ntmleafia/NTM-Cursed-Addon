// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev;

import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.leafia.overwrite_contents.interfaces.IMixinFluidTankNTM;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Fuck off NTMF
 */
@Deprecated
public class NTMFNBT {
	public static NBTTagCompound getNBT(FluidTankNTM tank) {
        return ((IMixinFluidTankNTM) tank).leafia$getAddonNbt();
	}
	public static void setNBT(FluidTankNTM tank,NBTTagCompound nbt) {
        ((IMixinFluidTankNTM) tank).leafia$setAddonNbt(nbt);
	}

	/**
	 * Should be called while calculating fluid distribution in networks to see if tank contents are compatible.
	 * @param sending the sending tank
	 * @param receiving the receiving tank
	 * @return true if compatible
	 */
	public boolean areTagsCompatible(FluidTankNTM sending,FluidTankNTM receiving) {
		NBTTagCompound sendingTag = getNBT(sending);
		NBTTagCompound receivingTag = getNBT(receiving);
		if (receivingTag == null) return true;
		if (receivingTag.equals(sendingTag)) return true;
		//if (sending.getTankType().equals(AddonFluids.FLUORIDE)) return true; for future; MSR
		return false;
	}

	/**
	 * Should be called whilst doing transferFluid.
	 * For example MSR would need this to calculate combined temperature and fuel mixture.
	 * @param sending the sending tank
	 * @param receiving the receiving tank
	 */
	public void transferTags(FluidTankNTM sending,FluidTankNTM receiving) {
		NBTTagCompound sendingTag = getNBT(sending);
		NBTTagCompound receivingTag = getNBT(receiving);
		if (receivingTag == null) {
			receivingTag = new NBTTagCompound();
			for (String s : sendingTag.getKeySet())
				receivingTag.setTag(s,sendingTag.getTag(s));
		}
	}
}
