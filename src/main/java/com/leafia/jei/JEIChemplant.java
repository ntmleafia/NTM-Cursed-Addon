// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;
import com.hbm.handler.jei.JeiRecipes;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.recipes.ChemicalPlantRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;
import com.hbm.items.machine.ItemBlueprints;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.I18nUtil;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.jei.JEIChemplant.Recipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JEIChemplant implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/chemplant.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (GenericRecipe recipe : ChemicalPlantRecipes.INSTANCE.recipeOrderedList) {
				recipes.add(new Recipe(recipe));
			}
			return recipes;
		}

		final List<List<ItemStack>> inputs = new ArrayList<>();
		final List<ItemStack> outputs = new ArrayList<>();
		final List<FluidStack> inputFluid;
		final List<FluidStack> outputFluid;
		public Recipe(GenericRecipe recipe) {
			inputFluid = (recipe.inputFluid == null ? new ArrayList<>() : Arrays.asList(recipe.inputFluid));
			outputFluid = (recipe.outputFluid == null ? new ArrayList<>() : Arrays.asList(recipe.outputFluid));
			List<AStack> inputItem =
					(recipe.inputItem == null ? new ArrayList<>() : Arrays.asList(recipe.inputItem));
			List<IOutput> outputItem =
					(recipe.outputItem == null ? new ArrayList<>() : Arrays.asList(recipe.outputItem));
			for (int i = 0; i < 3; i++) {
				if (inputItem.size() > i) {
					List<ItemStack> stacks = inputItem.get(i).getStackList();
					inputs.add(stacks);
				} else
					inputs.add(Collections.singletonList(new ItemStack(Items.AIR)));
			}
			if (recipe.isPooled()) {
				String[] pools = recipe.getPools();
				if (pools.length > 0)
					inputs.add(Collections.singletonList(ItemBlueprints.make(pools[0])));
				else
					inputs.add(Collections.singletonList(new ItemStack(Items.AIR)));
			} else
				inputs.add(Collections.singletonList(new ItemStack(Items.AIR)));
			for (int i = 0; i < 3; i++) {
				if (outputItem.size() > i) {
					ItemStack[] stacks = outputItem.get(i).getAllPossibilities();
					if (stacks != null) {
						outputs.add(stacks[0]);
						continue;
					}
				}
				outputs.add(new ItemStack(Items.AIR));
			}
			// for searching
			for (int i = 0; i < 3; i++) {
				if (inputFluid.size() > i) {
					ItemStack icon = ItemFluidIcon.make(inputFluid.get(i));
					inputs.add(Collections.singletonList(icon));
				} else
					inputs.add(Collections.singletonList(new ItemStack(Items.AIR)));
			}
			for (int i = 0; i < 3; i++) {
				if (outputFluid.size() > i) {
					ItemStack icon = ItemFluidIcon.make(outputFluid.get(i));
					outputs.add(icon);
				} else
					outputs.add(new ItemStack(Items.AIR));
			}
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(VanillaTypes.ITEM,inputs);
			ingredients.setOutputs(VanillaTypes.ITEM,outputs);
			if (!inputFluid.isEmpty())
				ingredients.setInputs(VanillaTypes.FLUID,_JEIFluidHelper.toForge(inputFluid));
			if (!outputFluid.isEmpty())
				ingredients.setOutputs(VanillaTypes.FLUID,_JEIFluidHelper.toForge(outputFluid));
		}
		@SideOnly(Side.CLIENT)
		@Override
		public void drawInfo(Minecraft minecraft,int recipeWidth,int recipeHeight,int mouseX,int mouseY) {
			List<FluidStack> stacks = new ArrayList<>();
			stacks.addAll(inputFluid);
			stacks.addAll(outputFluid);
			for (int i = 0; i < 3; i++) {
				if (inputFluid.size() > i)
					LeafiaClientUtil.jeiFluidRenderTank(stacks,inputFluid.get(i),37+i*18,1,16,26,false);
			}
			for (int i = 0; i < 3; i++) {
				if (outputFluid.size() > i)
					LeafiaClientUtil.jeiFluidRenderTank(stacks,outputFluid.get(i),109+i*18,1,16,26,false);
			}
		}
		@SideOnly(Side.CLIENT)
		@Override
		public List<String> getTooltipStrings(int mouseX,int mouseY) {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				if (inputFluid.size() > i) {
					FluidStack stack = inputFluid.get(i);
					LeafiaClientUtil.jeiFluidRenderInfo(stack,list,mouseX,mouseY,37+i*18,1,16,26);
				}
			}
			for (int i = 0; i < 3; i++) {
				if (outputFluid.size() > i) {
					FluidStack stack = outputFluid.get(i);
					LeafiaClientUtil.jeiFluidRenderInfo(stack,list,mouseX,mouseY,109+i*18,1,16,26);
				}
			}
			return list;
		}
		/*@Override
		public boolean handleClick(Minecraft minecraft,int mouseX,int mouseY,int mouseButton) {
			for (int i = 0; i < inputFluid.size(); i++) {
				if (_JEIFluidHelper.handleClick(inputFluid.get(i),mouseX,mouseY,37+i*18,1,16,26,mouseButton))
					return true;
			}
			for (int i = 0; i < outputFluid.size(); i++) {
				if (_JEIFluidHelper.handleClick(outputFluid.get(i),mouseX,mouseY,109+i*18,1,16,26,mouseButton))
					return true;
			}
			return false;
		}*/
	}

	protected final IDrawable background;
	protected final IDrawableStatic powerStatic;
	protected final IDrawableAnimated powerAnimated;
	public JEIChemplant(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,6,15,163,55);
		powerStatic = help.createDrawable(gui_rl, 176, 0, 16, 34);
		powerAnimated = help.createAnimatedDrawable(powerStatic, 480, StartDirection.TOP, true);
	}

	@Override public String getUid() { return JEIConfig.CHEMICAL_PLANT; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey(ModBlocks.machine_chemical_plant.getTranslationKey()+".name");
	}
	@Override public String getModName() { return "hbm"; }
	@Override public IDrawable getBackground() { return background; }

	@Override
	public void drawExtras(Minecraft minecraft) {
		powerAnimated.draw(minecraft,2,2);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,Recipe recipeWrapper,IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(7,true,1,37);
		for (int x = 0; x < 3; x++) {
			stacks.init(x,true,37+x*18,37);
			//stacks.init(x,true,37+x*18,6);
		}
		stacks.init(3,true,19,1);
		for (int x = 0; x < 3; x++) {
			stacks.init(x+4,false,109+x*18,37);
			//stacks.init(x+7,false,109+x*18,6);
		}
		stacks.set(ingredients);
		stacks.set(7,JeiRecipes.getBatteries());
	}
}
