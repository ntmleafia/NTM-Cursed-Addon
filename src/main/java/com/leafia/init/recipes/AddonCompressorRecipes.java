// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.init.recipes;

import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.CompressorRecipes;
import com.hbm.inventory.recipes.CompressorRecipes.CompressorRecipe;
import com.hbm.util.Tuple.Pair;
import com.leafia.contents.AddonFluids;

import java.util.HashMap;

public class AddonCompressorRecipes {
	public static HashMap<Pair<FluidType, Integer>,CompressorRecipe> recipePtr = CompressorRecipes.recipes;
	public static void register() {
		recipePtr.put(new Pair<>(AddonFluids.CRYOINTER, 0), new CompressorRecipe(1_000, new FluidStack(AddonFluids.CRYOINTER, 1_000, 1), 50));
		recipePtr.put(new Pair<>(AddonFluids.CRYOINTER, 1), new CompressorRecipe(1_000, new FluidStack(Fluids.CRYOGEL, 1_000, 0), 50));
	}
}
