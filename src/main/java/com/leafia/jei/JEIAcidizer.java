package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;
import com.hbm.handler.jei.JeiRecipes;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.recipes.CrystallizerRecipes;
import com.hbm.inventory.recipes.CrystallizerRecipes.CrystallizerRecipe;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.I18nUtil;
import com.hbm.util.Tuple.Pair;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.jei.JEIAcidizer.Recipe;
import com.llib.exceptions.LeafiaDevFlaw;
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

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public class JEIAcidizer implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/acidizer.png");

	static final HashMap<Pair<Object, FluidType>,CrystallizerRecipe> crystallizerRecipes;
	static {
		try {
			Field f = CrystallizerRecipes.class.getDeclaredField("recipes");
			f.setAccessible(true);
			Object bruh = f.get(null);
			crystallizerRecipes = (HashMap<Pair<Object, FluidType>,CrystallizerRecipe>)bruh;
		} catch (NoSuchFieldException | IllegalAccessException | ClassCastException exception) {
			throw new LeafiaDevFlaw(exception);
		}
	}

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (Entry<Pair<Object,FluidType>,CrystallizerRecipe> entry : crystallizerRecipes.entrySet())
				recipes.add(new Recipe(entry.getKey(),entry.getValue()));
			return recipes;
		}

		final FluidStack inputFluid;
		final List<ItemStack> inputs;
		final ItemStack output;

		public Recipe(Pair<Object,FluidType> data,CrystallizerRecipe recipe) {
			output = recipe.output;
			inputFluid = new FluidStack(data.getValue(),recipe.acidAmount);
			Object thing = data.getKey();
			if (thing instanceof String s)
				inputs = new OreDictStack(s).getStackList();
			else if (thing instanceof AStack aStack)
				inputs = aStack.getStackList();
			else
				throw new LeafiaDevFlaw("Invalid acidizer recipe item input type");
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(VanillaTypes.ITEM,Arrays.asList(inputs,Collections.singletonList(ItemFluidIcon.make(inputFluid))));
			ingredients.setOutput(VanillaTypes.ITEM,output);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void drawInfo(Minecraft minecraft,int recipeWidth,int recipeHeight,int mouseX,int mouseY) {
			LeafiaClientUtil.jeiFluidRenderTank(Collections.singletonList(inputFluid),inputFluid,38-1,2-1,16,52,false);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public List<String> getTooltipStrings(int mouseX,int mouseY) {
			List<String> list = new ArrayList<>();
			LeafiaClientUtil.jeiFluidRenderInfo(inputFluid,list,mouseX,mouseY,38-1,2-1,16,52);
			return list;
		}
	}

	protected final IDrawable background;
	protected final IDrawableStatic progressStatic;
	protected final IDrawableAnimated progressAnimated;
	protected final IDrawableStatic powerStatic;
	protected final IDrawableAnimated powerAnimated;
	public JEIAcidizer(IGuiHelper help) {
		background = help.createDrawable(gui_rl,6,15,155,55);

		progressStatic = help.createDrawable(gui_rl,192,0,22,16);
		progressAnimated = help.createAnimatedDrawable(progressStatic,600,StartDirection.LEFT,false);

		powerStatic = help.createDrawable(gui_rl,176,0,16,34);
		powerAnimated = help.createAnimatedDrawable(powerStatic,60,StartDirection.TOP,true);
	}

	@Override public String getUid() { return JEIConfig.CRYSTALLIZER; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey(ModBlocks.machine_crystallizer.getTranslationKey()+".name");
	}
	@Override public String getModName() { return "hbm"; }
	@Override public IDrawable getBackground() { return background; }

	@Override
	public void drawExtras(Minecraft minecraft) {
		powerAnimated.draw(minecraft, 2, 2);
		progressAnimated.draw(minecraft, 98, 19);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,Recipe recipeWrapper,IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0,true,73,19);
		stacks.init(2,false,133,19);
		stacks.init(3,true,1,37);

		stacks.set(ingredients);
		stacks.set(3,JeiRecipes.getBatteries());
	}
}
