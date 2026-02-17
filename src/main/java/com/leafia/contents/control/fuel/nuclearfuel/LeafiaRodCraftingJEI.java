package com.leafia.contents.control.fuel.nuclearfuel;

import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.AddonItems.LeafiaRods;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodCraftingJEI.Recipe;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeafiaRodCraftingJEI implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/generic_fuel_uncrafting.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (LeafiaRodItem rod : LeafiaRodItem.fromResourceMap.values()) {
				if (rod.baseItem != null)
					recipes.add(new Recipe(rod));
			}
			return recipes;
		}
		final LeafiaRodItem rod;
		public Recipe(LeafiaRodItem rod) {
			this.rod = rod;
		}
		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInput(VanillaTypes.ITEM,new ItemStack(rod));
			ingredients.setOutputs(VanillaTypes.ITEM,Arrays.asList(new ItemStack(rod.baseItem,1,rod.baseMeta),new ItemStack(LeafiaRods.leafRod)));
		}
		@Override
		public void drawInfo(Minecraft minecraft,int recipeWidth,int recipeHeight,int mouseX,int mouseY) {
			int ypos = 39;
			for (String s : I18nUtil.resolveKey("info.leafiarod.jei.message").split("\\$")) {
				minecraft.fontRenderer.drawString(s,96/2-minecraft.fontRenderer.getStringWidth(s)/2,ypos,4210752);
				ypos += 9;
			}
		}
	}

	protected final IDrawable background;
	public LeafiaRodCraftingJEI(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,0,0,96,56);
	}

	@Override public String getUid() { return _AddonJEI.GENERIC_ROD_UNCRAFTING; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey("info.leafiarod.jei.name");
	}
	@Override public String getModName() { return "leafia"; }
	@Override public IDrawable getBackground() { return background; }

	@Override
	public void drawExtras(Minecraft minecraft) {
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,Recipe recipeWrapper,IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0,true,1+1,1+1);
		stacks.init(1,false,57+1,11+1);
		stacks.init(2,false,75+1,11+1);
		stacks.set(ingredients);
	}
}
