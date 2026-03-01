package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.leafia.contents.AddonItems.DepletedFuels;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static com.hbm.inventory.recipes.WasteDrumRecipes.addRecipe;

public class AddonWasteDrumRecipes {
	public static void register() {
		add(DepletedFuels.waste_u238);
	}
	static void add(Item item) {
		addRecipe(new ComparableStack(item,1,1),new ItemStack(item));
	}
}
