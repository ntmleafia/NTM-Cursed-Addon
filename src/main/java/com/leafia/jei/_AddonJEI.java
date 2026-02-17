package com.leafia.jei;

import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodCraftingJEI;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Objects;

@JEIPlugin
public class _AddonJEI implements IModPlugin {
	public static String GENERIC_ROD_UNCRAFTING = "leafia.generic_rod_uncrafting";
	@Override
	public void register(IModRegistry registry) {
		registry.addRecipeCatalyst(
				new ItemStack(Objects.requireNonNull(Blocks.CRAFTING_TABLE)),
				GENERIC_ROD_UNCRAFTING
		);
		registry.addRecipes(LeafiaRodCraftingJEI.Recipe.buildRecipes(),GENERIC_ROD_UNCRAFTING);
	}
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper help = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(
				new LeafiaRodCraftingJEI(help)
		);
	}
}
