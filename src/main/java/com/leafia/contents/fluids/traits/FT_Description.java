// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.fluids.traits;

import com.hbm.inventory.fluid.trait.FluidTrait;
import com.hbm.util.I18nUtil;

import java.util.Arrays;
import java.util.List;

public class FT_Description extends FluidTrait {
	final String description;
	final boolean hidden;
	public FT_Description(String translationKey,boolean hidden) {
		this.description = translationKey;
		this.hidden = hidden;
	}
	@Override
	public void addInfo(List<String> info) {
		if (!hidden)
			info.addAll(Arrays.asList(I18nUtil.resolveKey(description).split("\\$")));
	}
	@Override
	public void addInfoHidden(List<String> info) {
		if (hidden)
			info.addAll(Arrays.asList(I18nUtil.resolveKey(description).split("\\$")));
	}
}
