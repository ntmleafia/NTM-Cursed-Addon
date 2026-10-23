// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.CrackingRecipes;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.I18nUtil;
import com.hbm.util.Tuple.Pair;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.jei.JEICracking.Recipe;
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
import java.util.List;
import java.util.Map.Entry;

public class JEICracking implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/cracking.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (Entry<FluidType,Pair<FluidStack,FluidStack>> entry : CrackingRecipes.cracking.entrySet()) {
				recipes.add(new Recipe(entry.getKey(),entry.getValue()));
			}
			return recipes;
		}

		final List<FluidStack> inputFluid = new ArrayList<>();
		final List<FluidStack> outputFluid = new ArrayList<>();
		public Recipe(FluidType in,Pair<FluidStack,FluidStack> out) {
			inputFluid.add(new FluidStack(in,100));
			inputFluid.add(new FluidStack(Fluids.STEAM,200));
			outputFluid.add(out.key);
			outputFluid.add(out.value);
			outputFluid.add(new FluidStack(Fluids.SPENTSTEAM,2));
		}
		static ItemStack makeIcon(FluidStack stack) {
			if (stack.type.equals(Fluids.NONE))
				return new ItemStack(Items.AIR);
			else
				return ItemFluidIcon.make(stack);
		}
		@Override
		public void getIngredients(IIngredients ingredients) {
			List<ItemStack> in = new ArrayList<>();
			List<ItemStack> out = new ArrayList<>();
			// for searching
			for (FluidStack f : inputFluid)
				in.add(makeIcon(f));
			for (FluidStack f : outputFluid)
				out.add(makeIcon(f));

			ingredients.setInputs(VanillaTypes.ITEM,in);
			ingredients.setOutputs(VanillaTypes.ITEM,out);

			// mov you can't just make changes relying on indev CE before you release it
			ingredients.setInputs(VanillaTypes.FLUID,_JEIFluidHelper.toForge(inputFluid));
			ingredients.setOutputs(VanillaTypes.FLUID,_JEIFluidHelper.toForge(outputFluid));
		}
		@SideOnly(Side.CLIENT)
		@Override
		public void drawInfo(Minecraft minecraft,int recipeWidth,int recipeHeight,int mouseX,int mouseY) {
			List<FluidStack> stacks = new ArrayList<>();
			stacks.addAll(inputFluid);
			stacks.addAll(outputFluid);
			for (int i = 0; i < inputFluid.size(); i++)
				LeafiaClientUtil.jeiFluidRenderTank(stacks,inputFluid.get(i),37+i*18,1,16,34,false);
			for (int i = 0; i < outputFluid.size(); i++)
				LeafiaClientUtil.jeiFluidRenderTank(stacks,outputFluid.get(i),91+i*18,1,16,34,false);
		}
		@SideOnly(Side.CLIENT)
		@Override
		public List<String> getTooltipStrings(int mouseX,int mouseY) {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < inputFluid.size(); i++) {
				FluidStack stack = inputFluid.get(i);
				LeafiaClientUtil.jeiFluidRenderInfo(stack,list,mouseX,mouseY,37+i*18,1,16,34);
			}
			for (int i = 0; i < outputFluid.size(); i++) {
				FluidStack stack = outputFluid.get(i);
				LeafiaClientUtil.jeiFluidRenderInfo(stack,list,mouseX,mouseY,91+i*18,1,16,34);
			}
			return list;
		}
		/*@Override
		public boolean handleClick(Minecraft minecraft,int mouseX,int mouseY,int mouseButton) {
			for (int i = 0; i < inputFluid.size(); i++) {
				if (_JEIFluidHelper.handleClick(inputFluid.get(i),mouseX,mouseY,37+i*18,1,16,34,mouseButton))
					return true;
			}
			for (int i = 0; i < outputFluid.size(); i++) {
				if (_JEIFluidHelper.handleClick(outputFluid.get(i),mouseX,mouseY,91+i*18,1,16,34,mouseButton))
					return true;
			}
			return false;
		}*/
	}

	protected final IDrawable background;
	protected final IDrawableStatic powerStatic;
	protected final IDrawableAnimated powerAnimated;
	public JEICracking(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,6,15,163-18,55-18);
		powerStatic = help.createDrawable(gui_rl, 176, 0, 16, 34);
		powerAnimated = help.createAnimatedDrawable(powerStatic, 480, StartDirection.TOP, true);
	}

	@Override public String getUid() { return JEIConfig.CRACKING; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey(ModBlocks.machine_catalytic_cracker.getTranslationKey()+".name");
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
		stacks.set(ingredients);
	}
}
