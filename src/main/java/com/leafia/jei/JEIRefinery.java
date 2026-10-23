// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.recipes.RefineryRecipes;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.I18nUtil;
import com.hbm.util.Tuple;
import com.hbm.util.Tuple.Quintet;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.jei.JEIRefinery.Recipe;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class JEIRefinery implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/refinery.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (Entry<FluidType,Quintet<FluidStack,FluidStack,FluidStack,FluidStack,ItemStack>> entry : RefineryRecipes.refinery.entrySet()) {
				recipes.add(new Recipe(entry.getKey(),entry.getValue()));
			}
			return recipes;
		}

		final ItemStack output;
		final FluidStack inputFluid;
		final List<FluidStack> outputFluid;
		public Recipe(FluidType in,Tuple.Quintet<FluidStack, FluidStack, FluidStack, FluidStack, ItemStack> out) {
			inputFluid = new FluidStack(in,1000);
			outputFluid = new ArrayList<>();
			outputFluid.add(out.getV());
			outputFluid.add(out.getW());
			outputFluid.add(out.getX());
			outputFluid.add(out.getY());
			output = out.getZ();
		}

		/*@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setOutput(VanillaTypes.ITEM,output);
			ingredients.setInput(VanillaTypes.FLUID,_JEIFluidHelper.toForge(inputFluid));
			ingredients.setOutputs(VanillaTypes.FLUID,_JEIFluidHelper.toForge(outputFluid));
		}*/
		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInput(VanillaTypes.ITEM,ItemFluidIcon.make(inputFluid));
			List<ItemStack> outs = new ArrayList<>();
			outs.add(output);
			// for searching
			for (FluidStack f : outputFluid)
				outs.add(ItemFluidIcon.make(f));

			ingredients.setOutputs(VanillaTypes.ITEM,outs);
		}
		@SideOnly(Side.CLIENT)
		@Override
		public void drawInfo(Minecraft minecraft,int recipeWidth,int recipeHeight,int mouseX,int mouseY) {
			List<FluidStack> stacks = new ArrayList<>();
			stacks.add(inputFluid);
			stacks.addAll(outputFluid);
			LeafiaClientUtil.jeiFluidRenderTank(stacks,inputFluid,37,1,16,34,false);
			for (int i = 0; i < outputFluid.size(); i++)
				LeafiaClientUtil.jeiFluidRenderTank(stacks,outputFluid.get(i),73+i*18,1,16,34,false);
		}
		@SideOnly(Side.CLIENT)
		@Override
		public List<String> getTooltipStrings(int mouseX,int mouseY) {
			List<String> list = new ArrayList<>();
			LeafiaClientUtil.jeiFluidRenderInfo(inputFluid,list,mouseX,mouseY,37,1,16,34);
			for (int i = 0; i < outputFluid.size(); i++) {
				FluidStack stack = outputFluid.get(i);
				LeafiaClientUtil.jeiFluidRenderInfo(stack,list,mouseX,mouseY,73+i*18,1,16,34);
			}
			return list;
		}
		/*@Override
		public boolean handleClick(Minecraft minecraft,int mouseX,int mouseY,int mouseButton) {
			if (_JEIFluidHelper.handleClick(inputFluid,mouseX,mouseY,37,1,16,34,mouseButton))
				return true;
			for (int i = 0; i < outputFluid.size(); i++) {
				if (_JEIFluidHelper.handleClick(outputFluid.get(i),mouseX,mouseY,73+i*18,1,16,34,mouseButton))
					return true;
			}
			return false;
		}*/
	}

	protected final IDrawable background;
	protected final IDrawableStatic powerStatic;
	protected final IDrawableAnimated powerAnimated;
	public JEIRefinery(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,6,15,163,55-18);
		powerStatic = help.createDrawable(gui_rl, 176, 0, 16, 34);
		powerAnimated = help.createAnimatedDrawable(powerStatic, 480, StartDirection.TOP, true);
	}

	@Override public String getUid() { return JEIConfig.REFINERY; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey(ModBlocks.machine_refinery.getTranslationKey()+".name");
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
		stacks.init(0,false,145,10);
		stacks.set(ingredients);
	}
}
