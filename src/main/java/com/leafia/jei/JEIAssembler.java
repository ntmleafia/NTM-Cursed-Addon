// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.recipes.AssemblyMachineRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;
import com.hbm.items.machine.ItemBlueprints;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.I18nUtil;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.jei.JEIAssembler.Recipe;
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

public class JEIAssembler implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/assembler.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (GenericRecipe recipe : AssemblyMachineRecipes.INSTANCE.recipeOrderedList) {
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
			for (int i = 0; i < 12; i++) {
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
			ItemStack[] stacks = outputItem.get(0).getAllPossibilities();
			if (stacks != null) {
				outputs.add(stacks[0]);
			}
			// for searching
			if (!inputFluid.isEmpty()) {
				ItemStack icon = ItemFluidIcon.make(inputFluid.get(0));
				inputs.add(Collections.singletonList(icon));
			} else {
				inputs.add(Collections.singletonList(new ItemStack(Items.AIR)));
			}
			if (!outputFluid.isEmpty()) {
				ItemStack icon = ItemFluidIcon.make(outputFluid.get(0));
				outputs.add(icon);
			} else {
				outputs.add(new ItemStack(Items.AIR));
			}
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(VanillaTypes.ITEM,inputs);
			ingredients.setOutput(VanillaTypes.ITEM,outputs.get(0));
			if (!inputFluid.isEmpty())
				ingredients.setInput(VanillaTypes.FLUID,_JEIFluidHelper.toForge(inputFluid.get(0)));
			if (!outputFluid.isEmpty())
				ingredients.setOutput(VanillaTypes.FLUID,_JEIFluidHelper.toForge(outputFluid.get(0)));
		}
		@SideOnly(Side.CLIENT)
		@Override
		public void drawInfo(Minecraft minecraft,int recipeWidth,int recipeHeight,int mouseX,int mouseY) {
			List<FluidStack> stacks = new ArrayList<>();
			stacks.addAll(inputFluid);
			stacks.addAll(outputFluid);
			if (!inputFluid.isEmpty())
				LeafiaClientUtil.jeiFluidRenderTank(stacks,inputFluid.get(0),28,55,52,4,true);
			if (!outputFluid.isEmpty())
				LeafiaClientUtil.jeiFluidRenderTank(stacks,outputFluid.get(0),100,55,52,4,true);
		}
		@SideOnly(Side.CLIENT)
		@Override
		public List<String> getTooltipStrings(int mouseX,int mouseY) {
			List<String> list = new ArrayList<>();
			if (!inputFluid.isEmpty())
				LeafiaClientUtil.jeiFluidRenderInfo(inputFluid.get(0),list,mouseX,mouseY,28,55,52,4);
			if (!outputFluid.isEmpty())
				LeafiaClientUtil.jeiFluidRenderInfo(outputFluid.get(0),list,mouseX,mouseY,100,55,52,4);
			return list;
		}
		/*@Override
		public boolean handleClick(Minecraft minecraft,int mouseX,int mouseY,int mouseButton) {
			return (!inputFluid.isEmpty() && _JEIFluidHelper.handleClick(inputFluid.get(0),mouseX,mouseY,28,55,52,4,mouseButton)) ||
					(!outputFluid.isEmpty() && _JEIFluidHelper.handleClick(outputFluid.get(0),mouseX,mouseY,100,55,52,4,mouseButton));
		}*/
	}

	protected final IDrawable background;
	protected final IDrawableStatic staticArrow;
	protected final IDrawableAnimated animatedArrow;
	protected final IDrawableStatic staticPower;
	protected final IDrawableAnimated animatedPower;
	public JEIAssembler(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,6,15,163,61);
		staticArrow = help.createDrawable(gui_rl, 16, 86+18, 36, 18);
		animatedArrow = help.createAnimatedDrawable(staticArrow, 48, StartDirection.LEFT, false);
		staticPower = help.createDrawable(gui_rl, 0, 86+18, 16, 58);
		animatedPower = help.createAnimatedDrawable(staticPower, 480, StartDirection.TOP, true);
	}

	@Override public String getUid() { return JEIConfig.ASSEMBLY_MACHINE; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey(ModBlocks.machine_assembly_machine.getTranslationKey()+".name");
	}
	@Override public String getModName() { return "hbm"; }
	@Override public IDrawable getBackground() { return background; }

	@Override
	public void drawExtras(Minecraft minecraft) {
		animatedArrow.draw(minecraft, 100, 19);
		animatedPower.draw(minecraft, 2, 2);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,Recipe recipeWrapper,IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 3; y++)
				stacks.init(x+y*4,true,28+x*18,1+y*18);
		}
		//stacks.init(12,true,46,55);
		stacks.init(12,true,109,1);
		stacks.init(13,false,136,19);
		//stacks.init(15,false,118,55);
		//stacks.init(14,true,1,55);
		stacks.set(ingredients);
		//stacks.set(14,JeiRecipes.getBatteries());
	}
}
