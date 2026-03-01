package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.PUREXRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems;
import com.leafia.contents.AddonItems.DepletedFuels;
import net.minecraft.item.ItemStack;

public class AddonPUREXRecipes {
	public static final PUREXRecipes INSTANCE = PUREXRecipes.INSTANCE;
	public static void register() {
		long zirnoxPower = 1_000;
		long platePower = 1_500;
		long pwrPower = 2_500;
		long watzPower = 10_000;
		long vitrification = 1_000;
		// ZIRNOX
		String autoZirnox = "autoswitch.zirnox";
		INSTANCE.register(new GenericRecipe("purex.leafia.zirnoxu238").setup(100,zirnoxPower).setNameWrapper("purex.recycle").setGroup(autoZirnox,INSTANCE)
				.inputItems(new ComparableStack(DepletedFuels.waste_u238))
				.inputFluids(new FluidStack(Fluids.KEROSENE,500),new FluidStack(Fluids.NITRIC_ACID,250))
				.outputItems(
						new ItemStack(ModItems.nugget_u238,1),
						new ItemStack(ModItems.nugget_pu239,4),
						new ItemStack(AddonItems.nugget_schraranium,1),
						new ItemStack(ModItems.nuclear_waste_tiny,1)
				).setIconToFirstIngredient());
	}
}
