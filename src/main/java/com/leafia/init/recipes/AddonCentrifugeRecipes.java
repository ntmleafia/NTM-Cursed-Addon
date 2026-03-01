package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class AddonCentrifugeRecipes {
	public static HashMap<AStack, ItemStack[]> recipes = CentrifugeRecipes.recipes;
	public static void register() {
		recipes.put(
				new ComparableStack(AddonBlocks.block_xanaxium),new ItemStack[]{
						new ItemStack(AddonItems.powder_xanaxium,2),
						new ItemStack(AddonItems.powder_xanaxium,2),
						new ItemStack(ModItems.powder_osmiridium,25),
						new ItemStack(ModItems.powder_osmiridium,25)}
		);
	}
}
