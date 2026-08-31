package com.leafia.init.recipes;

import com.hbm.inventory.material.MatDistribution;
import com.hbm.inventory.material.Mats;
import com.leafia.init.AddonMats;
import com.leafia.init.AddonOreDict;

import static com.hbm.inventory.material.MaterialShapes.*;

public class AddonMatDistribution {
	public static void register() {
		MatDistribution.registerOre(AddonOreDict.CHERNOBYL.ore(),
				Mats.MAT_URANIUM,INGOT.q(1),
				Mats.MAT_ZIRCONIUM,INGOT.q(1),
				Mats.MAT_SILICON,INGOT.q(1),
				Mats.MAT_SCHRABIDIUM,NUGGET.q(1),
				AddonMats.MAT_CORIUM,QUART.q(1)
		);
	}
}
