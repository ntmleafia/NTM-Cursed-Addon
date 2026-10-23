// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.fluids.traits;

import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.util.I18nUtil;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class FT_LFTRCoolant extends FluidTrait {
	public float multiplier = 1;
	public boolean moderated = false;
	public FT_LFTRCoolant(float multiplier) {
		this.multiplier = multiplier;
		this.moderated = multiplier >= 2;
	}
	@Override
	public void addInfoHidden(List<String> info) {
		float perc = (multiplier-1)*100;
		String percs;
		if (perc >= 0)
			percs = "+"+perc+"%";
		else
			percs = perc+"%";
		info.add(TextFormatting.YELLOW+"["+I18nUtil.resolveKey("trait.leafia.lftr")+"] "+TextFormatting.AQUA+I18nUtil.resolveKey("trait.leafia.lftr.multiplier",percs));
	}
}
