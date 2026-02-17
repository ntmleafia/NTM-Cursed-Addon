package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.jei.JEIConfig;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.recipes.ArcWelderRecipes;
import com.hbm.inventory.recipes.ArcWelderRecipes.ArcWelderRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;
import com.hbm.items.machine.ItemFluidIcon;
import com.hbm.util.I18nUtil;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.dev.LeafiaUtil;
import com.leafia.jei.JEIArcWelder.Recipe;
import com.llib.math.SIPfx;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.gui.IDrawableAnimated.StartDirection;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JEIArcWelder implements IRecipeCategory<Recipe> {
	public static final ResourceLocation gui_rl
			= new ResourceLocation("leafia","textures/gui/jei/arc_welder.png");

	public static class Recipe implements IRecipeWrapper {
		public static final List<Recipe> recipes = new ArrayList<>();
		public static List<Recipe> buildRecipes() {
			for (ArcWelderRecipe recipe : ArcWelderRecipes.recipes)
				recipes.add(new Recipe(recipe));
			return recipes;
		}

		final List<List<ItemStack>> inputs = new ArrayList<>();
		final ItemStack output;
		final FluidStack inputFluid;
		final int duration;
		final long consumption;
		public Recipe(ArcWelderRecipe recipe) {
			duration = recipe.duration;
			consumption = recipe.consumption;
			inputFluid = recipe.fluid;
			List<AStack> inputItem =
					(recipe.ingredients == null ? new ArrayList<>() : Arrays.asList(recipe.ingredients));
			output = recipe.output;
			for (int i = 0; i < 3; i++) {
				if (inputItem.size() > i) {
					List<ItemStack> stacks = inputItem.get(i).getStackList();
					inputs.add(stacks);
				} else
					inputs.add(Collections.singletonList(new ItemStack(Items.AIR)));
			}
			// for searching
			if (inputFluid != null) {
				ItemStack icon = ItemFluidIcon.make(inputFluid);
				inputs.add(Collections.singletonList(icon));
			}
		}

		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputLists(VanillaTypes.ITEM,inputs);
			ingredients.setOutputs(VanillaTypes.ITEM,Collections.singletonList(output));
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void drawInfo(Minecraft minecraft,int recipeWidth,int recipeHeight,int mouseX,int mouseY) {
			LeafiaClientUtil.jeiFluidRenderTank(Collections.singletonList(inputFluid),inputFluid,38-1,38-1,52,16,true);
			FontRenderer font = minecraft.fontRenderer;
			boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
			String duration =
					I18nUtil.resolveKey(
							"desc.leafia._jei.time_"+(shift ? "t" : "s"),
							String.format("%,"+LeafiaUtil.getFormatDecimal(
									shift ? this.duration : this.duration/20f,
									0,3
							),shift ? this.duration : this.duration/20f)
					);
			String consumption =
					I18nUtil.resolveKey(
							"desc.leafia._jei.energy_"+(shift ? "t" : "s"),
							SIPfx.format("%,"+LeafiaUtil.getFormatDecimal(
									shift ? this.consumption : this.consumption/20f,
									0,3
							),shift ? this.consumption : this.consumption/20f,false)+"HE"
					);
			int height = 9;
			font.drawString(duration,recipeWidth-font.getStringWidth(duration),recipeHeight-height*2,0x404040);
			font.drawString(consumption,recipeWidth-font.getStringWidth(consumption),recipeHeight-height,0x404040);
		}
		@SideOnly(Side.CLIENT)
		@Override
		public List<String> getTooltipStrings(int mouseX,int mouseY) {
			List<String> list = new ArrayList<>();
			LeafiaClientUtil.jeiFluidRenderInfo(inputFluid,list,mouseX,mouseY,38-1,38-1,52,16);
			return list;
		}
	}

	protected final IDrawable background;
	protected final IDrawableStatic powerStatic;
	protected final IDrawableAnimated powerAnimated;
	protected final IDrawableStatic progressStatic;
	protected final IDrawableAnimated progressAnimated;
	public JEIArcWelder(IGuiHelper help) {
		this.background = help.createDrawable(gui_rl,6,15,163,55);
		powerStatic = help.createDrawable(gui_rl, 176, 0, 16, 34);
		powerAnimated = help.createAnimatedDrawable(powerStatic, 480, StartDirection.TOP, true);
		progressStatic = help.createDrawable(gui_rl,192,0,32,14);
		progressAnimated = help.createAnimatedDrawable(progressStatic,48*3,StartDirection.LEFT,false);
	}

	@Override public String getUid() { return JEIConfig.ARC_WELDER; }
	@Override public String getTitle() {
		return I18nUtil.resolveKey(ModBlocks.machine_arc_welder.getTranslationKey()+".name");
	}
	@Override public String getModName() { return "hbm"; }
	@Override public IDrawable getBackground() { return background; }

	@Override
	public void drawExtras(Minecraft minecraft) {
		powerAnimated.draw(minecraft, 2, 2);
		progressAnimated.draw(minecraft,93,11);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,Recipe recipeWrapper,IIngredients ingredients) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		stacks.init(0,true,38-1,11-1);
		stacks.init(1,true,38+18-1,11-1);
		stacks.init(2,true,38+18*2-1,11-1);
		stacks.init(3,false,128-1,11-1);
		stacks.set(ingredients);
	}
}
