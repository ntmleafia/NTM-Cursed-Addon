package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.recipes.CyclotronRecipes;
import com.hbm.items.ModItems;
import com.hbm.util.Tuple;
import com.hbm.util.Tuple.Pair;
import com.leafia.contents.AddonItems;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

import static com.hbm.inventory.recipes.CyclotronRecipes.makeRecipe;

public class AddonCyclotronRecipes {
	public static HashMap<Pair<ComparableStack, AStack>, Pair<ItemStack, Integer>> recipes = CyclotronRecipes.recipes;
	public static void register() {
		int coA = 15;
		makeRecipe(new ComparableStack(ModItems.part_copper),
				new ComparableStack(AddonItems.particle_taint),
				new ItemStack(AddonItems.particle_cloud),
				coA
		);
	}
}
