// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.fluids.traits;

import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.util.I18nUtil;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class FT_Magnetic extends FluidTrait {
	@Override
	public void addInfoHidden(List<String> info) {
		info.add(TextFormatting.GOLD+"["+I18nUtil.resolveKey("trait.leafia.magnetic")+"]");
	}
}
