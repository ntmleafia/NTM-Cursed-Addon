// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.items.itembase;

import com.hbm.main.MainRegistry;
import com.leafia.AddonBase;
import com.leafia.contents.AddonItems;
import net.minecraft.item.Item;

public class AddonItemBase extends Item {
	public AddonItemBase(String s) {
		this.setTranslationKey(s);
		this.setRegistryName(AddonBase.MODID, s);
		this.setCreativeTab(MainRegistry.controlTab);
		AddonItems.ALL_ITEMS.add(this);
	}
}
