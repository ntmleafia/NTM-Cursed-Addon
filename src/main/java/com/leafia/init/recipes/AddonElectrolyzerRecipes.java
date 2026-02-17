package com.leafia.init.recipes;

import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes;
import com.hbm.inventory.recipes.ElectrolyserFluidRecipes.ElectrolysisRecipe;
import com.leafia.contents.AddonFluids;

import java.util.HashMap;

public class AddonElectrolyzerRecipes {
	static HashMap<FluidType,ElectrolysisRecipe> recipes = ElectrolyserFluidRecipes.recipes;
	public static void register() {
		recipes.put(AddonFluids.HF,new ElectrolysisRecipe(4000,new FluidStack(Fluids.HYDROGEN,2000),new FluidStack(AddonFluids.FLUORINE,2000)));
	}
}
