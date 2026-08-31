package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.PressRecipes;
import static com.hbm.inventory.recipes.PressRecipes.*;
import com.hbm.items.machine.ItemStamp;
import com.hbm.items.machine.ItemStamp.StampType;
import com.hbm.util.Tuple.Pair;
import static com.leafia.init.AddonOreDict.*;

import com.leafia.contents.AddonItems.Resources;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class AddonPressRecipes {
	public static HashMap<Pair<AStack, StampType>,ItemStack> recipes = PressRecipes.recipes;
	public static void register() {
		makeRecipe(StampType.PLATE,new OreDictStack(FSALLOY.plate()),Resources.plate_fissite);
	}
}
