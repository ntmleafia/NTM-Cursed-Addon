// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;

import com.hbm.util.I18nUtil;
import com.leafia.jei._JEIBase.Recipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class _JEIBase implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			return recipes;
		}

		public Recipe() {

		}

		@Override
		public void getIngredients(IIngredients ingredients) {

		}
	}

	protected final IDrawable background;
	public _JEIBase(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,6,15,163,55);
	}

	@Override public String getUid() { return JEIConfig.CHEMICAL_PLANT; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey(ModBlocks.machine_chemical_plant.getTranslationKey()+".name");
	}
	@Override public String getModName() { return "hbm"; }
	@Override public IDrawable getBackground() { return background; }

	@Override
	public void drawExtras(Minecraft minecraft) {
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,Recipe recipeWrapper,IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.set(ingredients);
	}
}
