package com.leafia.dev.items.itembase;

import com.hbm.main.MainRegistry;
import com.leafia.contents.AddonItems;
import net.minecraft.item.Item;

public class AddonItemBase extends Item {
	public AddonItemBase(String s) {
		this.setTranslationKey(s);
		this.setRegistryName(s);
		this.setCreativeTab(MainRegistry.controlTab);
		AddonItems.ALL_ITEMS.add(this);
	}
}
