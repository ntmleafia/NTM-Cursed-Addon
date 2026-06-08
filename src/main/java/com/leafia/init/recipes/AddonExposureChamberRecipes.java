package com.leafia.init.recipes;

import com.hbm.config.GeneralConfig;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.ExposureChamberRecipes;
import com.hbm.inventory.recipes.ExposureChamberRecipes.ExposureChamberRecipe;
import com.hbm.items.ItemEnums;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

import static com.hbm.inventory.OreDictManager.SBD;

public class AddonExposureChamberRecipes {
	public static List<ExposureChamberRecipe> recipes = ExposureChamberRecipes.recipes;
	public static void register() {
		removeRecipeFor(ModItems.ingot_dineutronium);
		if(GeneralConfig.enableExpensiveMode)
			recipes.add(new ExposureChamberRecipe(new ComparableStack(AddonItems.particle_dineutron), new ComparableStack(ModItems.item_expensive, 1, ItemEnums.EnumExpensiveType.DEGENERATE_MATTER), new ItemStack(ModItems.ingot_dineutronium)));
		else
			recipes.add(new ExposureChamberRecipe(new ComparableStack(AddonItems.particle_dineutron), new OreDictStack(SBD.ingot()), new ItemStack(ModItems.ingot_dineutronium)));
	}
	public static void removeRecipeFor(Item item) {
		recipes.removeIf(recipe->recipe.output.getItem().equals(item));
	}
}
