package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;
import com.hbm.handler.jei.JeiRecipes;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.recipes.PUREXRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;
import com.hbm.items.machine.ItemBlueprints;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.I18nUtil;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.jei.JEIPUREX.Recipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
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

public class JEIPUREX implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/purex.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (GenericRecipe recipe : PUREXRecipes.INSTANCE.recipeOrderedList) {
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
			for (int i = 0; i < 6; i++) {
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
			ingredients.setOutputs(VanillaTypes.ITEM,outputs);
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
			if (!outputFluid.isEmpty())
				LeafiaClientUtil.jeiFluidRenderTank(stacks,outputFluid.get(0),145,1,16,52,false);
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
			if (!outputFluid.isEmpty())
				LeafiaClientUtil.jeiFluidRenderInfo(outputFluid.get(0),list,mouseX,mouseY,145,1,16,52);
			return list;
		}
	}

	protected final IDrawable background;
	public JEIPUREX(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,6,15,163,55);
	}

	@Override public String getUid() { return "hbm.purex"; } // for some reason purex is package private
	@Override public String getTitle() {
		return I18nUtil.resolveKey(ModBlocks.machine_purex.getTranslationKey()+".name");
	}
	@Override public String getModName() { return "hbm"; }
	@Override public IDrawable getBackground() { return background; }

	@Override
	public void drawExtras(Minecraft minecraft) {
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,Recipe recipeWrapper,IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(10,true,1,37);
		for (int x = 0; x < 3; x++)
			stacks.init(x,true,37+x*18,37);
		stacks.init(3,true,19,1);
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 2; x++)
				stacks.init(x+y*2+4,false,109+x*18,37-18*2+18*y);
		}
		stacks.set(ingredients);
		stacks.set(10,JeiRecipes.getBatteries());
	}
}
