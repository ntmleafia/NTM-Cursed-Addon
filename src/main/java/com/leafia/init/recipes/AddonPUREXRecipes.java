package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.PUREXRecipe;
import com.hbm.inventory.recipes.PUREXRecipes;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems.DepletedFuels;
import com.leafia.contents.AddonItems.Resources;
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
		INSTANCE.register((PUREXRecipe) new PUREXRecipe("purex.leafia.zirnoxu238").setup(100,zirnoxPower).setNameWrapper("purex.recycle").setGroup(autoZirnox,INSTANCE)
				.inputItems(new ComparableStack(DepletedFuels.waste_u238))
				.inputFluids(new FluidStack(Fluids.KEROSENE,500),new FluidStack(Fluids.NITRIC_ACID,250))
				.outputItems(
						new ItemStack(ModItems.nugget_u238,1),
						new ItemStack(ModItems.nugget_pu239,4),
						new ItemStack(Resources.nugget_schraranium,1),
						new ItemStack(ModItems.nuclear_waste_tiny,1)
				).setIconToFirstIngredient());
		INSTANCE.register((PUREXRecipe) new PUREXRecipe("purex.leafia.zirnoxmes").setup(100,zirnoxPower).setNameWrapper("purex.recycle").setGroup(autoZirnox,INSTANCE)
				.inputItems(new ComparableStack(DepletedFuels.waste_mes))
				.inputFluids(new FluidStack(Fluids.KEROSENE,500),new FluidStack(Fluids.NITRIC_ACID,250))
				.outputItems(
						new ItemStack(ModItems.nugget_solinium,1),
						new ItemStack(ModItems.nugget_beryllium,2),
						new ItemStack(ModItems.nuclear_waste_tiny,1),
						new ItemStack(ModItems.nuclear_waste_tiny,2)
				).setIconToFirstIngredient());
		INSTANCE.register((PUREXRecipe) new PUREXRecipe("purex.leafia.zirnoxhes").setup(100,zirnoxPower).setNameWrapper("purex.recycle").setGroup(autoZirnox,INSTANCE)
				.inputItems(new ComparableStack(DepletedFuels.waste_hes))
				.inputFluids(new FluidStack(Fluids.KEROSENE,500),new FluidStack(Fluids.NITRIC_ACID,250))
				.outputItems(
						new ItemStack(ModItems.nugget_solinium,2),
						new ItemStack(ModItems.nugget_beryllium,1),
						new ItemStack(ModItems.nuclear_waste_tiny,1),
						new ItemStack(ModItems.nuclear_waste_tiny,2)
				).setIconToFirstIngredient());
		INSTANCE.register((PUREXRecipe) new PUREXRecipe("purex.leafia.zirnoxsch").setup(100,zirnoxPower).setNameWrapper("purex.recycle").setGroup(autoZirnox,INSTANCE)
				.inputItems(new ComparableStack(DepletedFuels.waste_sch))
				.inputFluids(new FluidStack(Fluids.KEROSENE,500),new FluidStack(Fluids.NITRIC_ACID,250))
				.outputItems(
						new ItemStack(ModItems.nugget_solinium,2),
						new ItemStack(ModItems.nugget_solinium,1),
						new ItemStack(ModItems.nuclear_waste_tiny,1),
						new ItemStack(ModItems.nuclear_waste_tiny,2)
				).setIconToFirstIngredient());
		INSTANCE.register((PUREXRecipe) new PUREXRecipe("purex.leafia.zirnoxsol").setup(100,zirnoxPower).setNameWrapper("purex.recycle").setGroup(autoZirnox,INSTANCE)
				.inputItems(new ComparableStack(DepletedFuels.waste_sol))
				.inputFluids(new FluidStack(Fluids.KEROSENE,500),new FluidStack(Fluids.NITRIC_ACID,250))
				.outputItems(
						new ItemStack(ModItems.nugget_australium,2),
						new ItemStack(ModItems.nugget_euphemium,1),
						new ItemStack(ModItems.nuclear_waste_tiny,1),
						new ItemStack(ModItems.nuclear_waste_tiny,2)
				).setIconToFirstIngredient());
	}
}
