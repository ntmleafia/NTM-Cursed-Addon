package com.leafia.contents.consumable;

import com.hbm.main.MainRegistry;
import com.leafia.AddonBase;
import com.leafia.contents.AddonItems;
import net.minecraft.item.ItemFood;

public class AddonFoodBase extends ItemFood {
	public AddonFoodBase(String s,int hunger,boolean isWolfFood) {
		super(hunger,isWolfFood);
		this.setTranslationKey(s);
		this.setRegistryName(AddonBase.MODID, s);
		this.setCreativeTab(MainRegistry.consumableTab);
		AddonItems.ALL_ITEMS.add(this);
	}
}
