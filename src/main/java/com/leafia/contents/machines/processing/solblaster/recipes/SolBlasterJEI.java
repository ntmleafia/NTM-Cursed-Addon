package com.leafia.contents.machines.processing.solblaster.recipes;

import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;
import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.machines.processing.solblaster.recipes.SolBlasterJEI.Recipe;
import com.leafia.contents.machines.processing.solblaster.recipes.SolBlasterRecipes.ItemOrOreDict;
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
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SolBlasterJEI implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/blaster.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (Entry<ItemOrOreDict,Map<Integer,Pair<Item,Integer>>> entryItem : SolBlasterRecipes.recipes.entrySet()) {
				for (Entry<Integer,Pair<Item,Integer>> entryMeta : entryItem.getValue().entrySet()) {
					List<ItemStack> stacks = new ArrayList<>();
					if (!entryItem.getKey().isOreDict)
						stacks.add(new ItemStack(entryItem.getKey().item,1,entryMeta.getKey()));
					else
						stacks.addAll(OreDictionary.getOres(entryItem.getKey().dict));
					recipes.add(new Recipe(
							stacks,
							new ItemStack(entryMeta.getValue().getA(),1,entryMeta.getValue().getB())
					));
				}
			}
			return recipes;
		}

		final List<ItemStack> input;
		final ItemStack output;
		public Recipe(List<ItemStack> input,ItemStack output) {
			this.input = input;
			this.output = output;
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputs(VanillaTypes.ITEM,input);
			ingredients.setOutput(VanillaTypes.ITEM,output);
		}
	}

	protected final IDrawable background;
	public SolBlasterJEI(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,33,33,109,19);
	}

	@Override public String getUid() { return _AddonJEI.SOL_BLASTER; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey(AddonBlocks.sol_blaster.getTranslationKey()+".name");
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
