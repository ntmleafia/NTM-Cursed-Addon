package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;
import com.hbm.handler.jei.JeiRecipes;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.gui.element.GUIElements;
import com.hbm.inventory.recipes.ChemicalPlantRecipes;
import com.hbm.inventory.recipes.PlasmaForgeRecipe;
import com.hbm.inventory.recipes.PlasmaForgeRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;
import com.hbm.items.machine.ItemBlueprints;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.I18nUtil;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.dev.LeafiaUtil;
import com.leafia.jei.JEIPlasmaForge.Recipe;
import com.llib.math.SIPfx;
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

public class JEIPlasmaForge implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/plasma_forge.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (PlasmaForgeRecipe recipe : PlasmaForgeRecipes.INSTANCE.recipeOrderedList)
				recipes.add(new Recipe(recipe));
			return recipes;
		}

		final List<List<ItemStack>> inputs = new ArrayList<>();
		final List<ItemStack> outputs = new ArrayList<>();
		final List<FluidStack> inputFluid;
		final long ignition;
		static double maxIgnition = 1;
		public Recipe(PlasmaForgeRecipe recipe) {
			ignition = recipe.ignitionTemp;
			maxIgnition = Math.max(ignition,maxIgnition);
			inputFluid = (recipe.inputFluid == null ? new ArrayList<>() : Arrays.asList(recipe.inputFluid));
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
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(VanillaTypes.ITEM,inputs);
			ingredients.setOutput(VanillaTypes.ITEM,outputs.get(0));
			if (!inputFluid.isEmpty())
				ingredients.setInput(VanillaTypes.FLUID,_JEIFluidHelper.toForge(inputFluid.get(0)));
		}
		@SideOnly(Side.CLIENT)
		@Override
		public void drawInfo(Minecraft minecraft,int recipeWidth,int recipeHeight,int mouseX,int mouseY) {
			List<FluidStack> stacks = new ArrayList<>();
			stacks.addAll(inputFluid);
			if (!inputFluid.isEmpty())
				LeafiaClientUtil.jeiFluidRenderTank(stacks,inputFluid.get(0),109,1,16,52,false);
			GUIElements.drawSmoothGauge(28,28,0,ignition/maxIgnition,5, 2, 1, 0xA00000);
		}
		@SideOnly(Side.CLIENT)
		@Override
		public List<String> getTooltipStrings(int mouseX,int mouseY) {
			List<String> list = new ArrayList<>();
			if (!inputFluid.isEmpty())
				LeafiaClientUtil.jeiFluidRenderInfo(inputFluid.get(0),list,mouseX,mouseY,109,1,16,52);
			if (mouseX >= 19 && mouseY >= 19 && mouseX <= 19+18 && mouseY <= 19+18) {
				list.add(SIPfx.formatNoSpace("%,"+LeafiaUtil.getFormatDecimal(
						ignition,
						0,3
				),ignition,false)+"TU");
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
	public JEIPlasmaForge(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,6,15,163,55);
		powerStatic = help.createDrawable(gui_rl, 176, 0, 16, 34);
		powerAnimated = help.createAnimatedDrawable(powerStatic, 480, StartDirection.TOP, true);
	}

	@Override public String getUid() { return JEIConfig.PLASMA_FORGE; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey(ModBlocks.fusion_plasma_forge.getTranslationKey()+".name");
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
		stacks.init(14,true,1,36);
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 3; y++)
				stacks.init(x+y*4,true,37+x*18,1+y*18);
		}
		stacks.init(12,true,127,1);
		stacks.init(13,false,145,19);
		stacks.set(ingredients);
		stacks.set(14,JeiRecipes.getBatteries());
	}
}
