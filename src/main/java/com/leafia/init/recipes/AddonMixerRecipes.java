// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.init.recipes;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.MixerRecipes;
import com.hbm.inventory.recipes.MixerRecipes.MixerRecipe;

import java.util.HashMap;

public class AddonMixerRecipes {
	public static HashMap<FluidType, MixerRecipe[]> recipePtr = MixerRecipes.recipes;
	public static void register() {
		recipePtr.remove(Fluids.CRYOGEL); // upon Bob's request
	}
}
