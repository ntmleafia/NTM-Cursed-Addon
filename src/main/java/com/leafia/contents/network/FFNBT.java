// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.fluids.traits.FT_LFTRCoolant;
import com.leafia.contents.machines.reactors.lftr.components.MSRTEBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FFNBT {
	/**
	 * Should be called while calculating fluid distribution in networks to see if tank contents are compatible.
	 * @param sending the sending stack
	 * @param receiving the receiving tank
	 * @return true if compatible
	 */
	public static boolean areTagsCompatible(FluidStack sending,FluidTank receiving) {
		if (receiving.getFluid() == null) return true;
		NBTTagCompound sendingTag = sending.tag;
		NBTTagCompound receivingTag = receiving.getFluid().tag;
		if (receivingTag == null) return true;
		if (receivingTag.equals(sendingTag)) return true;
		FluidType ntmf = AddonFluids.fromFF(sending.getFluid());
		if (!ntmf.equals(Fluids.NONE)) {
			if (ntmf.hasTrait(FT_LFTRCoolant.class)) return true;
		}
		return false;
	}

	/**
	 * Should be called whilst doing transferFluid.
	 * For example MSR would need this to calculate combined temperature and fuel mixture.
	 * @param sending the sending stack
	 * @param receiving the receiving tank
	 */
	public static void transferTags(FluidTank sending,FluidTank receiving) {
		if (sending.getFluid() == null) return;
		if (receiving.getFluid() == null) return;
		NBTTagCompound sendingTag = sending.getFluid().tag;
		NBTTagCompound receivingTag = receiving.getFluid().tag;
		if (sendingTag == null) return;
		if (receivingTag == null) {
			receivingTag = new NBTTagCompound();
			for (String s : sendingTag.getKeySet())
				receivingTag.setTag(s,sendingTag.getTag(s));
		}
		FluidType ntmf = AddonFluids.fromFF(sending.getFluid().getFluid());
		if (!ntmf.equals(Fluids.NONE)) {
			if (ntmf.hasTrait(FT_LFTRCoolant.class))
				MSRTEBase.transferStats(sending.getFluid(),receiving.getFluid(),1);
		}
		receiving.getFluid().tag = receivingTag;
	}
}
