package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.recipes.ArcWelderRecipes;
import com.hbm.inventory.recipes.ArcWelderRecipes.ArcWelderRecipe;
import com.hbm.items.ModItems;
import com.leafia.init.AddonMats;
import net.minecraft.item.ItemStack;

import java.util.List;

import static com.leafia.init.AddonOreDict.XN;

public class AddonArcWelderRecipes {
	public static List<ArcWelderRecipe> recipes = ArcWelderRecipes.recipes;
	public static void register() {
		recipes.add(new ArcWelderRecipe(
				new ItemStack(ModItems.plate_welded, 1, AddonMats.MAT_XANAXIUM.id),
				6_000,
				20_000_000L,
				new FluidStack(Fluids.REFORMGAS, 16_000),
				new OreDictStack(XN.plateCast(), 2))
		);
	}
}
