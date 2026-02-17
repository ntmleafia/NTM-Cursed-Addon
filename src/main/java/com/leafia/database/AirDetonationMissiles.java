package com.leafia.database;

import com.hbm.items.ModItems;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class AirDetonationMissiles {
	public static final List<Item> defaultAirDetonationMissiles = new ArrayList<>();
	public static void init() {
		defaultAirDetonationMissiles.add(ModItems.missile_n2);
		defaultAirDetonationMissiles.add(ModItems.missile_micro);
		defaultAirDetonationMissiles.add(ModItems.missile_nuclear);
		defaultAirDetonationMissiles.add(ModItems.missile_nuclear_cluster);
		defaultAirDetonationMissiles.add(ModItems.missile_emp);
		defaultAirDetonationMissiles.add(ModItems.missile_emp_strong);
	}
}
