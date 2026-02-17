package com.leafia.jei;

import com.hbm.handler.jei.JEIConfig;
import com.hbm.handler.jei.JeiRecipes;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.recipes.CentrifugeRecipes;

import com.hbm.util.I18nUtil;
import com.leafia.jei.JEICentrifuge.Recipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class JEICentrifuge implements IRecipeCategory<Recipe> {
	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (Entry<AStack,ItemStack[]> entry : CentrifugeRecipes.recipes.entrySet()) {
				List<ItemStack> stacks = entry.getKey().getStackList();
				if (stacks.size() > 1)
					recipes.add(new Recipe(stacks,Arrays.asList(entry.getValue())));
				else if (stacks.size() == 1)
					recipes.add(new Recipe(stacks.get(0),Arrays.asList(entry.getValue())));
			}
			return recipes;
		}

		private final ItemStack input;
		private final List<ItemStack> outputs;
		public final List<ItemStack> inputs;

		public Recipe(ItemStack input,List<ItemStack> outputs) {
			this.input = input;
			this.inputs = null;
			this.outputs = outputs;
		}

		public Recipe(List<ItemStack> inputs,List<ItemStack> outputs) {
			this.inputs = inputs;
			this.input = ItemStack.EMPTY;
			this.outputs = outputs;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			if (inputs != null) {
				ingredients.setInputLists(VanillaTypes.ITEM,Arrays.asList(inputs));
			} else {
				ingredients.setInput(VanillaTypes.ITEM,input);
			}
			ingredients.setOutputs(VanillaTypes.ITEM,outputs);
		}

	}

	public static final ResourceLocation gui_rl = new ResourceLocation("leafia","textures/gui/jei/centrifuge.png");

	protected final IDrawable background;
	protected final IDrawableStatic progressStatic;
	protected final IDrawableAnimated progressAnimated;
	protected final IDrawableStatic powerStatic;
	protected final IDrawableAnimated powerAnimated;
	protected final IDrawableStatic flameStatic;
	protected final IDrawableAnimated flameAnimated;

	public JEICentrifuge(IGuiHelper help) {
		background = help.createDrawable(gui_rl,6,15,163,55);

		progressStatic = help.createDrawable(gui_rl,176,0,54,54);
		progressAnimated = help.createAnimatedDrawable(progressStatic,48*3,StartDirection.LEFT,false);

		powerStatic = help.createDrawable(gui_rl,176,54,17,54);
		powerAnimated = help.createAnimatedDrawable(powerStatic,480,StartDirection.TOP,true);

		flameStatic = help.createDrawable(gui_rl,194,54,18,18);
		flameAnimated = help.createAnimatedDrawable(flameStatic,48,StartDirection.TOP,true);
	}

	@Override
	public String getUid() {
		return JEIConfig.CENTRIFUGE;
	}

	@Override
	public String getTitle() {
		return I18nUtil.resolveKey("tile.machine_centrifuge.name");
	}

	@Override
	public String getModName() {
		return "hbm";
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		progressAnimated.draw(minecraft,55,1);
		flameAnimated.draw(minecraft,19,19);
		powerAnimated.draw(minecraft,1,1);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,Recipe recipeWrapper,IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0,true,19,1);

		guiItemStacks.init(1,false,127,1);
		guiItemStacks.init(2,false,145,1);
		guiItemStacks.init(3,false,127,37);
		guiItemStacks.init(4,false,145,37);

		guiItemStacks.init(5,true,19,37);

		guiItemStacks.set(ingredients);
		if (recipeWrapper.inputs != null)
			guiItemStacks.set(0,recipeWrapper.inputs);
		guiItemStacks.set(5,JeiRecipes.getBatteries());
	}
}