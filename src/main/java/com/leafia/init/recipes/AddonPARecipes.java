package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.recipes.LeafiaPARecipe;
import com.hbm.inventory.recipes.ParticleAcceleratorRecipes;
import com.hbm.inventory.recipes.ParticleAcceleratorRecipes.ParticleAcceleratorRecipe;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AddonPARecipes {
	public static final List<ParticleAcceleratorRecipe> recipes = ParticleAcceleratorRecipes.recipes;
	public static void register() {
		recipes.add(new LeafiaPARecipe(
				new RecipesCommon.ComparableStack(ModItems.particle_sparkticle),
				new RecipesCommon.ComparableStack(AddonItems.powder_digammitite),
				13_000,
				new ItemStack(AddonItems.particle_dineutron),
				new ItemStack(ModItems.dust)
		));
	}
}
