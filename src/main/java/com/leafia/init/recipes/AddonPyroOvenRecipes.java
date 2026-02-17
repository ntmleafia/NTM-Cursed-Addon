package com.leafia.init.recipes;

import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.PyroOvenRecipes;
import com.hbm.inventory.recipes.PyroOvenRecipes.PyroOvenRecipe;
import com.leafia.contents.AddonFluids;

import java.util.List;

public class AddonPyroOvenRecipes {
	static List<PyroOvenRecipe> recipes = PyroOvenRecipes.recipes;
	public static void register() {
		recipes.add(new PyroOvenRecipe(50)
				.in(new OreDictStack(OreDictManager.F.dust()))
				.in(new FluidStack(Fluids.SULFURIC_ACID,1000))
				.out(new FluidStack(AddonFluids.HF,1000))
		);
	}
}
