package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.recipes.ShredderRecipes;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class AddonShredderRecipes {
	public static HashMap<ComparableStack,ItemStack> shredderRecipes = ShredderRecipes.shredderRecipes;
	public static void register() {
		shredderRecipes.put(new ComparableStack(AddonBlocks.digammitite,1),
				new ItemStack(AddonItems.powder_digammitite_tiny,1)
		);
		shredderRecipes.put(new ComparableStack(AddonBlocks.digammitite,1,1),
				new ItemStack(AddonItems.powder_digammitite_tiny,4)
		);
		shredderRecipes.put(new ComparableStack(AddonBlocks.digammitite,1,2),
				new ItemStack(AddonItems.powder_digammitite_tiny,7)
		);
		shredderRecipes.put(new ComparableStack(AddonBlocks.digammitite,1,3),
				new ItemStack(AddonItems.powder_digammitite_tiny,10)
		);
		shredderRecipes.put(new ComparableStack(AddonBlocks.digammitite,1,4),
				new ItemStack(AddonItems.powder_digammitite_tiny,13)
		);
		shredderRecipes.put(new ComparableStack(AddonBlocks.digammitite,1,5),
				new ItemStack(AddonItems.powder_digammitite_tiny,16)
		);
	}
}
