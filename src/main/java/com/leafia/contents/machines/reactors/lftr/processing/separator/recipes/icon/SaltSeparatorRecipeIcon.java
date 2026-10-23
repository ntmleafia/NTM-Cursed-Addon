// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.lftr.processing.separator.recipes.icon;

import com.leafia.dev.items.itembase.AddonItemBaked;

/// yes i know this is ass, i ain't bothering doing all the meta shit when i'm rushed
/// <p>if it works, it works
public class SaltSeparatorRecipeIcon extends AddonItemBaked {
	public SaltSeparatorRecipeIcon(String s) {
		super("separator_icon_"+s,"separator_icons/"+s);
		setCreativeTab(null);
	}
}
