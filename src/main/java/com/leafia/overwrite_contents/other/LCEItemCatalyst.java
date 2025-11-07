package com.leafia.overwrite_contents.other;

import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemCatalyst;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LCEItemCatalyst {
	public static Map<Item,Integer> meltingPoints = new HashMap<>();
	public static void registerMeltingPoints() {
		meltingPoints.put(ModItems.ams_catalyst_iron,2900);
		meltingPoints.put(ModItems.ams_catalyst_copper,2250);
		meltingPoints.put(ModItems.ams_catalyst_aluminium,2250);
		meltingPoints.put(ModItems.ams_catalyst_lithium,600);
		meltingPoints.put(ModItems.ams_catalyst_beryllium,1287);
		meltingPoints.put(ModItems.ams_catalyst_tungsten,3422);
		meltingPoints.put(ModItems.ams_catalyst_cobalt,1495);
		meltingPoints.put(ModItems.ams_catalyst_niobium,2900);
		meltingPoints.put(ModItems.ams_catalyst_cerium,2900);
		meltingPoints.put(ModItems.ams_catalyst_thorium,800);
		meltingPoints.put(ModItems.ams_catalyst_strontium,3200);
		meltingPoints.put(ModItems.ams_catalyst_caesium,1952);
		meltingPoints.put(ModItems.ams_catalyst_schrabidium,3000);
		meltingPoints.put(ModItems.ams_catalyst_euphemium,4000);
		meltingPoints.put(ModItems.ams_catalyst_dineutronium,6000);
	}
	public static int getMelting(ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof ItemCatalyst))
			return 1500000;
		Integer value = meltingPoints.get(stack.getItem());
		if (value == null)
			throw new LeafiaDevFlaw("Melting point for catalyst type "+stack.getItem().getRegistryName()+" not found");
		return value;
	}
}
