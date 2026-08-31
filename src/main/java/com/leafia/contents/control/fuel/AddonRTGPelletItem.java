package com.leafia.contents.control.fuel;

import com.hbm.items.ItemEnums.EnumDepletedRTGMaterial;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemRTGPellet;
import com.leafia.contents.AddonItems;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;

public class AddonRTGPelletItem extends ItemRTGPellet {
	public static Field decayItemPtr;
	static {
		try {
			decayItemPtr = ItemRTGPellet.class.getDeclaredField("decayItem");
			decayItemPtr.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new LeafiaDevFlaw(e);
		}
	}
	public AddonRTGPelletItem(int heatIn,String s) {
		super(heatIn,s);
		ModItems.ALL_ITEMS.remove(this);
		AddonItems.ALL_ITEMS.add(this);
	}
	public ItemRTGPellet setDecays(Item item,long halflife,int halflifes) {
		super.setDecays(EnumDepletedRTGMaterial.MERCURY,halflife,halflifes);
		ItemStack product = new ItemStack(item,1,0);
		try {
			decayItemPtr.set(this,product);
		} catch (IllegalAccessException e) {
			throw new LeafiaDevFlaw(e);
		}
		pelletMap.put(this,product);
		return this;
	}
}
