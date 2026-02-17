package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.ChemicalPlantRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.AddonItems;
//import com.leafia.contents.control.battery.AddonBatteryPackItem.AddonEnumBatteryPack;
import com.leafia.contents.control.battery.AddonEnumBatteryPack;
import net.minecraft.item.ItemStack;

import static com.hbm.inventory.OreDictManager.*;

public class AddonChemplantRecipes {
	public static final ChemicalPlantRecipes INSTANCE = ChemicalPlantRecipes.INSTANCE;
	public static void register() {
		INSTANCE.register(new GenericRecipe("chem.leafia.flibe").setupNamed(60,400).setIcon(ModItems.fluid_icon,AddonFluids.FLUORIDE.getID())
				.inputItems(new RecipesCommon.OreDictStack(BE.ingot(),1),new OreDictStack(LI.ingot(),1),new OreDictStack(F.dust(),2))
				.inputFluids(new FluidStack(Fluids.SULFURIC_ACID,2_000))
				.outputItems(new ItemStack(ModItems.ingot_calcium,2),new ItemStack(ModItems.sulfur,2))
				.outputFluids(new FluidStack(AddonFluids.FLUORIDE,2700),new FluidStack(Fluids.WATER,2_000),new FluidStack(Fluids.OXYGEN,2_000)));
		/*INSTANCE.register(new GenericRecipe("chem.leafia.fluorine").setupNamed(100,600).setIcon(ModItems.fluid_icon,Fluids.FLUORINE.getID())
				.inputItems(new RecipesCommon.OreDictStack(F.dust(),1))
				.outputItems(new ItemStack(ModItems.ingot_calcium))
				.outputFluids(new FluidStack(Fluids.FLUORINE,1000))
		);*/
		INSTANCE.register(new GenericRecipe("chem.leafia.battery_desh").setup(100, 15_000)
				.inputItems(
						new OreDictStack(DESH.ingot(),48),
						new OreDictStack(BAKELITE.dust(),24),
						new OreDictStack(P_RED.dust(),24)
				)
				.inputFluids(
						new FluidStack(Fluids.COALGAS,3000)
				)
				.outputItems(new ItemStack(ModItems.battery_pack,1,AddonEnumBatteryPack.BATTERY_DESH.ordinal()))
		);
		INSTANCE.register(new GenericRecipe("chem.leafia.battery_euphemium").setup(100, 50_000)
				.inputItems(
						new OreDictStack(EUPH.ingot(),24),
						new OreDictStack(XE135.dust(),16),
						new OreDictStack(SBD.dust(),16)
				)
				.inputFluids(
						new FluidStack(Fluids.HELIUM4, 8_000)
				)
				.outputItems(new ItemStack(ModItems.battery_pack,1,AddonEnumBatteryPack.BATTERY_EUPHEMIUM.ordinal()))
		);
		INSTANCE.register(new GenericRecipe("chem.leafia.battery_slop").setup(100, 300_000)
				.inputItems(
						new ComparableStack(ModItems.powder_chlorophyte,24),
						new ComparableStack(ModItems.powder_balefire,24),
						new OreDictStack(AS.nugget(),8)
				)
				.inputFluids(
						new FluidStack(AddonFluids.RADSPICE_SLOP,32_000)
				)
				.outputItems(new ItemStack(ModItems.battery_pack,1,AddonEnumBatteryPack.BATTERY_SLOP.ordinal()))
		);
		INSTANCE.register(new GenericRecipe("chem.leafia.battery_spk").setup(100, 500_000)
				.inputItems(
						new OreDictStack(DNT.ingot(),48),
						new ComparableStack(ModItems.powder_spark_mix,32),
						new ComparableStack(ModItems.coil_magnetized_tungsten,12)
				)
				.inputFluids(
						new FluidStack(Fluids.PERFLUOROMETHYL_COLD,8_000),
						new FluidStack(Fluids.STELLAR_FLUX,4_000)
				)
				.outputItems(new ItemStack(ModItems.battery_pack,1,AddonEnumBatteryPack.BATTERY_SPK.ordinal()))
				.outputFluids(
						new FluidStack(Fluids.PERFLUOROMETHYL_HOT,8_000)
				)
		);
		INSTANCE.register(new GenericRecipe("chem.leafia.battery_electro").setup(100, 1_000_000)
				.inputItems(
						new ComparableStack(ModItems.ingot_electronium,64),
						new ComparableStack(ModItems.powder_osmiridium,48),
						new ComparableStack(ModItems.nugget_u238m2,16)
				)
				.inputFluids(
						new FluidStack(Fluids.PERFLUOROMETHYL_COLD,16_000)
				)
				.outputItems(new ItemStack(ModItems.battery_pack,1,AddonEnumBatteryPack.BATTERY_ELECTRO.ordinal()))
				.outputFluids(
						new FluidStack(Fluids.PERFLUOROMETHYL_HOT,16_000)
				)
		);
	}
}
