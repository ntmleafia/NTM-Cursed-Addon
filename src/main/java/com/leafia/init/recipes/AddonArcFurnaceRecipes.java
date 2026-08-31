package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.ArcFurnaceRecipes;
import com.hbm.inventory.recipes.ArcFurnaceRecipes.ArcFurnaceRecipe;
import com.leafia.contents.AddonBlocks.Ores;

import static com.hbm.inventory.material.Mats.*;
import static com.leafia.init.AddonMats.*;
import static com.hbm.inventory.material.MaterialShapes.*;

public class AddonArcFurnaceRecipes {
	public static void register() {
		ArcFurnaceRecipes.register(new ComparableStack(Ores.ore_corium_chernobylite),
				new ArcFurnaceRecipe().fluidNull(
						new MaterialStack(MAT_URANIUM,INGOT.q(1)),
						new MaterialStack(MAT_ZIRCONIUM,INGOT.q(1)),
						new MaterialStack(MAT_SILICON,INGOT.q(1)),
						new MaterialStack(MAT_SCHRABIDIUM,NUGGET.q(1)),
						new MaterialStack(MAT_CORIUM,QUART.q(1))
				)
		);
		ArcFurnaceRecipes.register(new ComparableStack(Ores.ore_corium_zetalite),
				new ArcFurnaceRecipe().fluidNull(
						new MaterialStack(MAT_NC279,INGOT.q(2)),
						new MaterialStack(MAT_TAINTIUM,INGOT.q(2)),
						new MaterialStack(MAT_CORIUM,QUART.q(1))
				)
		);
	}
}
