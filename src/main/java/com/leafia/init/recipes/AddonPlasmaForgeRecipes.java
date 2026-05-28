package com.leafia.init.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.PlasmaForgeRecipe;
import com.hbm.inventory.recipes.PlasmaForgeRecipes;
import com.hbm.items.ItemEnums.EnumCircuitType;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;
import net.minecraft.item.ItemStack;

import static com.hbm.inventory.OreDictManager.*;
import static com.leafia.init.AddonOreDict.*;

public class AddonPlasmaForgeRecipes {
	public static final PlasmaForgeRecipes INSTANCE = PlasmaForgeRecipes.INSTANCE;
	public static void register() {
		INSTANCE.register((PlasmaForgeRecipe)new PlasmaForgeRecipe("plsm.leafia.dfcreinabsorber").setInputEnergy(50_000_000).setup(1_200, 10_000_000)
				.outputItems(new ItemStack(AddonBlocks.dfc_reinforced))
				.inputFluids(new FluidStack(Fluids.STELLAR_FLUX, 4_000))
				.inputItems(
						new OreDictStack(XN.plateWelded(), 16),
						new OreDictStack(SBD.wireDense(), 4),
						new ComparableStack(ModItems.circuit, 4,EnumCircuitType.CONTROLLER_QUANTUM),
						new ComparableStack(ModBlocks.dfc_receiver),
						new ComparableStack(AddonItems.supercooler)
				)
		);
		INSTANCE.register((PlasmaForgeRecipe)new PlasmaForgeRecipe("plsm.leafia.dfcexchanger").setInputEnergy(50_000_000).setup(1_200, 10_000_000)
				.outputItems(new ItemStack(AddonBlocks.dfc_exchanger))
				.inputFluids(new FluidStack(Fluids.STELLAR_FLUX, 4_000))
				.inputItems(
						new OreDictStack(OSMIRIDIUM.plateWelded(), 16),
						new OreDictStack(BIGMT.plateCast(), 16),
						new ComparableStack(ModItems.motor_bismuth),
						new ComparableStack(ModBlocks.heater_heatex),
						new OreDictStack(STEEL.pipe(),24)
				)
		);
	}
}
