package com.leafia.init;

import com.hbm.items.ModItems;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;

public class ArmorInit {
	public static void postInit() {
		ArmorRegistry.registerHazard(ModItems.gas_mask_filter_mono,HazardClass.PARTICLE_COARSE,HazardClass.GAS_MONOXIDE);
	}
}
