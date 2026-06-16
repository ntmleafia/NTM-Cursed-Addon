package com.leafia.init.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.fluid.FluidStack;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.recipes.AssemblyMachineRecipes;
import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.items.ItemEnums.EnumCircuitType;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.Elevators;
import com.leafia.contents.AddonBlocks.LetterSigns;
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.AddonFluids;
import com.leafia.contents.AddonItems;
import com.leafia.contents.AddonItems.ElevatorStyles;
import com.leafia.contents.control.battery.AddonEnumBatteryPack;
import com.leafia.settings.AddonConfig;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.Map.Entry;

import static com.hbm.inventory.OreDictManager.*;

public class AddonAssemblerRecipes {
	public static final AssemblyMachineRecipes INSTANCE = AssemblyMachineRecipes.INSTANCE;
	public static void register() {
		if (!AddonConfig.disableAddonPWR) {
			INSTANCE.register(new GenericRecipe("ass.leafia.legacy_pwrcontrol").setup(100,100)
					.outputItems(new ItemStack(PWR.reactor_control))
					.inputItems(
							new OreDictStack(STEEL.ingot(),4),
							new OreDictStack(PB.ingot(),6),
							new OreDictStack(W.bolt(),6),
							new ComparableStack(ModItems.motor)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.legacy_pwrfuel").setup(150,100)
					.outputItems(new ItemStack(PWR.element_old))
					.inputItems(
							new OreDictStack(STEEL.ingot(),2),
							new OreDictStack(OreDictManager.getReflector(),4),
							new OreDictStack(PB.plate(),2),
							new OreDictStack(ZR.ingot(),2)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.legacy_pwrconductor").setup(130,100)
					.outputItems(new ItemStack(PWR.conductor))
					.inputItems(
							new OreDictStack(STEEL.ingot(),4),
							new OreDictStack(CU.plate(),12),
							new OreDictStack(W.wireFine(),4)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.legacy_pwrhatch").setup(130,150)
					.outputItems(new ItemStack(PWR.hatch))
					.inputItems(
							new ComparableStack(ModBlocks.brick_concrete,1),
							new OreDictStack(STEEL.plate(),6),
							new ComparableStack(ModItems.circuit, 4, EnumCircuitType.BASIC)
					)
			);
			replaceOutput("ass.pwrcontrol",new ItemStack(PWR.control));
			replaceOutput("ass.pwrfuel",new ItemStack(PWR.element));
			replaceOutput("ass.pwrchannel",new ItemStack(PWR.channel));
			replaceOutput("ass.pwrheatex",new ItemStack(PWR.exchanger));
			replaceOutput("ass.pwrreflector",new ItemStack(PWR.reflector));
			replaceOutput("ass.pwrcasing",new ItemStack(PWR.hull));
			replaceOutput("ass.pwrcontroller",new ItemStack(PWR.terminal));
			replaceOutput("ass.pwrport",new ItemStack(PWR.port));
			remove("ass.pwrneutronsource");
			remove("ass.pwrheatsink");
		}
		/*
		INSTANCE.register(new GenericRecipe("ass.leafia.msr_control").setup(100,100)
				.outputItems(new ItemStack(AddonBlocks.LFTR.control))
				.inputItems(
						new ComparableStack(ModItems.motor, 4),
						new OreDictStack(STEEL.plate(),6),
						new OreDictStack(PB.plateCast(),2)
				)
		);
		INSTANCE.register(new GenericRecipe("ass.leafia.msr_extension").setup(100,100)
				.outputItems(new ItemStack(AddonBlocks.LFTR.extension))
				.inputItems(
						new OreDictStack(B.ingot(), 2),
						new OreDictStack(STEEL.plate(),4)
				)
		);
		INSTANCE.register(new GenericRecipe("ass.leafia.msr_injector").setup(100,100)
				.outputItems(new ItemStack(AddonBlocks.LFTR.plug))
				.inputItems(
						new OreDictStack(PB.plateCast(), 4),
						new OreDictStack(STEEL.pipe(),4),
						new OreDictStack(STEEL.shell()),
						new ComparableStack(ModItems.circuit, 1, EnumCircuitType.ADVANCED)
				)
		);
		INSTANCE.register(new GenericRecipe("ass.leafia.msr_ejector").setup(100,100)
				.outputItems(new ItemStack(AddonBlocks.LFTR.ejector))
				.inputItems(
						new OreDictStack(PB.plateCast(), 4),
						new OreDictStack(STEEL.pipe(),4),
						new OreDictStack(STEEL.shell()),
						new ComparableStack(ModItems.motor, 4)
				)
		);
		INSTANCE.register(new GenericRecipe("ass.leafia.msr_arbitrary").setup(100,100)
				.outputItems(new ItemStack(AddonBlocks.LFTR.arbitrary))
				.inputItems(
						new OreDictStack(ZR.ingot(), 2),
						new OreDictStack(CU.plateCast(), 2),
						new OreDictStack(STEEL.pipe(),4),
						new OreDictStack(STEEL.bolt(), 16)
				)
		);
		INSTANCE.register(new GenericRecipe("ass.leafia.msr_reactor").setup(100,100)
				.outputItems(new ItemStack(AddonBlocks.LFTR.element))
				.inputItems(
						new OreDictStack(ZR.plateWelded(), 1),
						new OreDictStack(CU.plateCast(), 2),
						new OreDictStack(STEEL.pipe(),4),
						new OreDictStack(STEEL.bolt(), 16)
				)
		);*/
		INSTANCE.register(new GenericRecipe("ass.leafia.msr_heatex").setup(200,100)
				.outputItems(new ItemStack(AddonBlocks.coolant_heatex))
				.inputItems(
						new OreDictStack(STEEL.plate(), 16),
						new OreDictStack(CU.plate(), 24),
						new OreDictStack(PB.plateCast(), 4),
						new OreDictStack(STEEL.pipe(),4),
						new ComparableStack(ModItems.motor, 4),
						new ComparableStack(ModItems.circuit, 1, EnumCircuitType.BASIC)
				)
		);
		/*
		INSTANCE.register(new GenericRecipe("ass.leafia.ff_mixer").setup(200,100)
				.outputItems(new ItemStack(AddonBlocks.mixingvat))
				.inputItems(
						new ComparableStack(ModItems.motor, 2),
						new OreDictStack(STEEL.ingot(), 8),
						new OreDictStack(STEEL.pipe(), 2),
						new OreDictStack(PB.plate(), 4),
						new ComparableStack(ModItems.circuit, 1, EnumCircuitType.ADVANCED)
				)
		);
		INSTANCE.register(new GenericRecipe("ass.leafia.saltfuel_separator").setup(200,100)
				.outputItems(new ItemStack(AddonBlocks.salt_separator))
				.inputItems(
						new OreDictStack(STEEL.plateWelded(), 8),
						new OreDictStack(STEEL.pipe(), 8),
						new OreDictStack(STEEL.shell(), 2),
						new OreDictStack(PB.plateCast(), 4),
						new ComparableStack(ModItems.tank_steel, 6),
						new ComparableStack(ModItems.motor, 8),
						new ComparableStack(ModItems.circuit, 4, EnumCircuitType.ADVANCED)
				)
		);*/
		INSTANCE.register(new GenericRecipe("ass.leafia.light").setup(20,50)
				.outputItems(new ItemStack(AddonBlocks.lightUnlit))
				.inputItems(
						new OreDictStack(STEEL.plate()),
						new OreDictStack(IRON.nugget(),2),
						new ComparableStack(Blocks.GLASS_PANE),
						new OreDictStack(MINGRADE.wireFine())
				)
				.inputFluids(new FluidStack(Fluids.MERCURY,5))
		);
		INSTANCE.register(new GenericRecipe("ass.leafia.advisor").setup(20,50)
				.outputItems(new ItemStack(AddonItems.advisor))
				.inputItems(
						new OreDictStack(ANY_PLASTIC.ingot(),2),
						new ComparableStack(ModItems.circuit,1,EnumCircuitType.BASIC.ordinal()),
						new ComparableStack(Blocks.STONE_BUTTON)
				)
		);
		INSTANCE.register(new GenericRecipe("ass.leafia.cp").setup(130,100)
				.outputItems(new ItemStack(PWR.conductor))
				.inputItems(
						new OreDictStack(STEEL.plate(),4),
						new ComparableStack(ModItems.circuit,2,EnumCircuitType.BASIC),
						new OreDictStack(MINGRADE.wireFine(),2)
				)
		);
		if (Loader.isModLoaded("opencomputers")) {
			INSTANCE.register(new GenericRecipe("ass.leafia.pwrcomputer").setup(200, 500).outputItems(new ItemStack(PWR.computer, 1))
					.inputItems(
							new OreDictStack(PB.plateCast(), 4),
							new OreDictStack(REDSTONE.dust(), 12),
							new ComparableStack(ModItems.circuit, 8, EnumCircuitType.BASIC),
							new ComparableStack(ModItems.circuit, 2, EnumCircuitType.CAPACITOR)
					)
			);
		}
		for (Entry<String,Block> entry : LetterSigns.signs.entrySet()) {
			INSTANCE.register(new GenericRecipe("ass.leafia.sign."+entry.getKey()).setup(20,10)
					.outputItems(new ItemStack(entry.getValue()))
					.inputItems(
							new OreDictStack(IRON.plate(),1)
					)
			);
		}
		INSTANCE.register(new GenericRecipe("ass.leafia.radspice").setup(100,500).setIcon(ModItems.fluid_icon,AddonFluids.RADSPICE_SLOP.getID())
				.inputItems(
						new OreDictStack(CO60.dust()),
						new OreDictStack(SR90.dust()),
						new OreDictStack(I131.dust()),
						new OreDictStack(CS137.dust()),
						new OreDictStack(XE135.dust()),
						new OreDictStack(AU198.dust()),
						new OreDictStack(PB209.dust()),
						new OreDictStack(AC227.dust())
				)
				.inputFluids(
						new FluidStack(Fluids.SLOP,1_000)
				)
				.outputItems(
						new ItemStack(Blocks.DIRT) // fuck off it breaks without an item output
				)
				.outputFluids(
						new FluidStack(AddonFluids.RADSPICE_SLOP,1_000)
				)
		);
		makeRecipe("ass.leafia.ams_stabilizer_part",new ComparableStack(AddonItems.component_limiter, 1), new AStack[] { new ComparableStack(ModItems.part_barrel_heavy, 2,Mats.MAT_STEEL.id), new OreDictStack(STEEL.plate(), 32), new OreDictStack(TI.plate(), 18), new ComparableStack(ModItems.plate_desh, 12), new ComparableStack(ModItems.pipes_steel, 4), new ComparableStack(ModItems.circuit, 8, EnumCircuitType.CAPACITOR_BOARD), new ComparableStack(ModItems.circuit, 4, EnumCircuitType.BISMOID), new OreDictStack(STAR.ingot(), 14), new ComparableStack(ModItems.plate_dalekanium, 5), new ComparableStack(ModItems.powder_magic, 16), new ComparableStack(ModBlocks.icf_controller, 3), }, 2500);
		makeRecipe("ass.leafia.ams_limiter_part",new ComparableStack(AddonItems.component_emitter, 1), new AStack[] { new ComparableStack(ModItems.part_barrel_heavy, 3,Mats.MAT_STEEL.id), new ComparableStack(ModItems.part_barrel_heavy, 2,Mats.MAT_TCALLOY.id), new OreDictStack(STEEL.plate(), 32), new OreDictStack(PB.plate(), 24), new ComparableStack(ModItems.plate_desh, 24), new ComparableStack(ModItems.pipes_steel, 8), new ComparableStack(ModItems.circuit, 12, EnumCircuitType.CAPACITOR_BOARD), new ComparableStack(ModItems.circuit, 8, EnumCircuitType.BISMOID), new OreDictStack(STAR.ingot(), 26), new ComparableStack(ModItems.powder_magic, 48), new ComparableStack(ModBlocks.icf_controller, 2), new ComparableStack(ModItems.crystal_xen, 1), }, 2500);
		makeRecipe("ass.leafia.ams_stabilizer",new ComparableStack(AddonBlocks.ams_limiter, 1), new AStack[] { new ComparableStack(AddonItems.component_limiter, 5), new OreDictStack(STEEL.plate(), 64), new OreDictStack(TI.plate(), 64), new OreDictStack(TI.plate(), 64), new ComparableStack(ModItems.plate_dineutronium, 16), new ComparableStack(ModItems.circuit, 6, EnumCircuitType.BISMOID), new ComparableStack(ModItems.pipes_steel, 16), new ComparableStack(ModItems.motor, 12), new ComparableStack(ModItems.coil_advanced_torus, 12), new ComparableStack(ModItems.entanglement_kit, 1), }, 6000);
		makeRecipe("ass.leafia.ams_emitter",new ComparableStack(AddonBlocks.ams_emitter, 1), new AStack[] { new ComparableStack(AddonItems.component_emitter, 16), new OreDictStack(STEEL.plate(), 64), new OreDictStack(STEEL.plate(), 64), new OreDictStack(TI.plate(), 64), new OreDictStack(TI.plate(), 64), new OreDictStack(TI.plate(), 64), new ComparableStack(ModItems.plate_dineutronium, 32), new ComparableStack(ModItems.circuit, 12, EnumCircuitType.BISMOID), new ComparableStack(ModItems.coil_advanced_torus, 24), new ComparableStack(ModItems.entanglement_kit, 3), new ComparableStack(ModItems.crystal_horn, 1), new ComparableStack(ModBlocks.struct_icf_core,1) }, 6000);
		INSTANCE.register(new GenericRecipe("ass.leafia.ams_base").setup(6000,100)
				.outputItems(new ItemStack(AddonBlocks.ams_base))
				.inputItems(
						new ComparableStack(ModBlocks.icf_component),
						new OreDictStack(STEEL.plate(),32),
						new OreDictStack(TI.plate(),64),
						new ComparableStack(ModItems.plate_dineutronium,8),
						new ComparableStack(ModItems.circuit, 32, EnumCircuitType.BISMOID),
						new ComparableStack(ModItems.coil_advanced_torus, 6),

						new ComparableStack(ModItems.coil_magnetized_tungsten, 24),
						new ComparableStack(ModBlocks.barrel_tcalloy,2),
						new ComparableStack(ModBlocks.barrel_antimatter,2),
						new ComparableStack(ModItems.battery_pack,1,AddonEnumBatteryPack.BATTERY_SPK)
				)
		);
		makeRecipe("ass.leafia.supercooler",new ComparableStack(AddonItems.supercooler, 1), new AStack[] { new ComparableStack(ModItems.coil_copper_torus, 3), new OreDictStack(STEEL.ingot(), 3), new OreDictStack(TI.plate(), 6), new ComparableStack(ModItems.plate_polymer, 12), new OreDictStack(BIGMT.ingot(), 2), new ComparableStack(PWR.exchanger,32), new ComparableStack(ModBlocks.watz_cooler,16) }, 100);
		makeRecipe("ass.leafia.panel",new ComparableStack(ModBlocks.control_panel_custom, 1), new AStack[]{new ComparableStack(ModItems.circuit,1,EnumCircuitType.ANALOG), new OreDictStack(STEEL.plate(), 6), new ComparableStack(ModItems.wire_fine, 24, Mats.MAT_COPPER.id)}, 100);
		makeRecipe("ass.leafia.customnukemissile",new ComparableStack(AddonItems.missile_customnuke),new AStack[]{new ComparableStack(ModItems.missile_assembly),new ComparableStack(ModItems.circuit,8,EnumCircuitType.CONTROLLER_ADVANCED.ordinal()),new OreDictStack(KEY_GRAY,4)},100);
		{
			INSTANCE.register(new GenericRecipe("ass.leafia.elevator_floor_s6").setup(40,50)
					.outputItems(new ItemStack(Elevators.s6_floor))
					.inputItems(
							new OreDictStack(STEEL.plateWelded(),6),
							new OreDictStack(STEEL.plate(),2),
							new ComparableStack(ModItems.circuit,1,EnumCircuitType.BASIC)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.elevator_floor_skylift").setup(40,50)
					.outputItems(new ItemStack(Elevators.skylift_floor))
					.inputItems(
							new OreDictStack(STEEL.plateWelded(),6),
							new ComparableStack(ModItems.circuit,1,EnumCircuitType.BASIC)
					)
			);
		}
		INSTANCE.register(new GenericRecipe("ass.leafia.elevator_pulley").setup(100,50)
				.outputItems(new ItemStack(Elevators.pulley))
				.inputItems(
						new OreDictStack(ANY_CONCRETE.any(),2),
						new OreDictStack(STEEL.ingot(),3),
						new ComparableStack(ModItems.motor,1),
						new ComparableStack(ModItems.circuit,1,EnumCircuitType.BASIC)
				)
		);
		INSTANCE.register(new GenericRecipe("ass.leafia.elevator_buffer").setup(100,50)
				.outputItems(new ItemStack(Elevators.buffer))
				.inputItems(
						new OreDictStack(STEEL.ingot(),14)
				)
		);
		{
			INSTANCE.register(new GenericRecipe("ass.leafia.ev_s6wall").setup(40,50)
					.outputItems(new ItemStack(ElevatorStyles.s6wall))
					.inputItems(
							new OreDictStack(STEEL.plateWelded(),2)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.ev_s6floor").setup(40,50)
					.outputItems(new ItemStack(ElevatorStyles.s6floor))
					.inputItems(
							new OreDictStack(STEEL.plateWelded(),2)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.ev_s6ceiling").setup(40,50)
					.outputItems(new ItemStack(ElevatorStyles.s6ceiling))
					.inputItems(
							new OreDictStack(STEEL.plateWelded(),2)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.ev_s6window").setup(40,50)
					.outputItems(new ItemStack(ElevatorStyles.s6window))
					.inputItems(
							new OreDictStack(STEEL.ingot()),
							new ComparableStack(Blocks.GLASS_PANE,4)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.ev_s6door").setup(40,50)
					.outputItems(new ItemStack(ElevatorStyles.s6door))
					.inputItems(
							new OreDictStack(STEEL.plateWelded(),1),
							new OreDictStack(STEEL.plate(),2)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.ev_skyliftdoor").setup(40,50)
					.outputItems(new ItemStack(ElevatorStyles.skyliftdoor))
					.inputItems(
							new OreDictStack(STEEL.plateWelded(),1),
							new OreDictStack(STEEL.plate(),2)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.windtb.medium").setup(200,100)
					.outputItems(new ItemStack(AddonBlocks.wind_turbine_medium))
					.inputItems(
							new ComparableStack(ModBlocks.steel_beam,5),
							new OreDictStack(DURA.pipe(),4),
							new ComparableStack(ModItems.motor),
							new ComparableStack(ModItems.coil_gold_torus)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.reactordoor").setup(200,150)
					.outputItems(new ItemStack(AddonBlocks.reactor_door))
					.inputItems(
							new OreDictStack(STEEL.plateWelded(),4),
							new OreDictStack(PB.block(),2),
							new OreDictStack(W.bolt(),12),
							new OreDictStack(RUBBER.ingot(),4)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.crimsondoorlarge").setup(400,100)
					.outputItems(new ItemStack(AddonBlocks.crimson_door_large))
					.inputItems(
							new OreDictStack(STEEL.plateCast(),12),
							new OreDictStack(STEEL.pipe(),8),
							new OreDictStack(ALLOY.plate(),4),
							new ComparableStack(ModItems.plate_polymer,16),
							new ComparableStack(ModItems.motor,6),
							new OreDictStack(W.bolt(),16),
							new OreDictStack("dyeRed",6)
					)
			);
			INSTANCE.register(new GenericRecipe("ass.leafia.crimsondoorsmall").setup(300,100)
					.outputItems(new ItemStack(AddonBlocks.crimson_door_small))
					.inputItems(
							new OreDictStack(STEEL.plateCast(),6),
							new OreDictStack(STEEL.pipe(),4),
							new OreDictStack(ALLOY.plate(),2),
							new ComparableStack(ModItems.plate_polymer,8),
							new ComparableStack(ModItems.motor,4),
							new OreDictStack(W.bolt(),8),
							new OreDictStack("dyeRed",4)
					)
			);
		}
	}
	public static void makeRecipe(String s,ComparableStack out, AStack[] in, int duration) {
		INSTANCE.register(new GenericRecipe(s).setup(duration,100)
				.outputItems(new ItemStack(out.item))
				.inputItems(in)
		);
	}
	public static void remove(String entry) {
		GenericRecipe recipe = INSTANCE.recipeNameMap.get(entry);
		if (recipe != null) {
			INSTANCE.recipeOrderedList.remove(recipe);
			INSTANCE.recipeNameMap.remove(entry);
		}
	}
	public static void replaceOutput(String entry,ItemStack... outputs) {
		GenericRecipe recipe = INSTANCE.recipeNameMap.get(entry);
		if (recipe != null) {
			int count = recipe.outputItem[0].getSingle().getCount();
			for (ItemStack output : outputs)
				output.setCount(count);
			recipe.outputItems(outputs);
		} else
			throw new LeafiaDevFlaw("Could not find recipe \""+entry+"\" to replace");
	}
}
