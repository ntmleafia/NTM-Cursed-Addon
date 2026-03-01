package com.leafia.contents.control.fuel.nuclearfuel;

import com.hbm.util.I18nUtil;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodDecayJEI.Recipe;
import com.leafia.jei._AddonJEI;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class LeafiaRodDecayJEI implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/gui_nei_fusion.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (LeafiaRodItem rod : LeafiaRodItem.fromResourceMap.values()) {
				if (rod.newFuel != null)
					recipes.add(new Recipe(rod,rod.newFuel));
			}
			return recipes;
		}

		final Item input;
		final Item output;
		public Recipe(Item input,Item output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInput(VanillaTypes.ITEM,new ItemStack(input));
			ingredients.setOutput(VanillaTypes.ITEM,new ItemStack(output));
		}
	}

	protected final IDrawable background;
	public LeafiaRodDecayJEI(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,33,33,109,19);
	}

	@Override public String getUid() { return _AddonJEI.GENERIC_ROD_DECAY; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey("info.leafiarod.jei.decay");
	}
	@Override public String getModName() { return "leafia"; }
	@Override public IDrawable getBackground() { return background; }

	@Override
	public void drawExtras(Minecraft minecraft) {
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,Recipe recipeWrapper,IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0,true,1,1);
		stacks.init(1,false,91,1);
		stacks.set(ingredients);
	}
}
