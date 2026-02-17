package com.leafia.dev.machine;

import com.hbm.util.I18nUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class MachineTooltip {
	/// Gets called before addInformation().
	public static void addInfoASM(Item item,List<String> tooltip) {
		if (item.getRegistryName() != null) {
			switch(item.getRegistryName().getPath()) {
				// CONDENSERS
				case "machine_condenser":
				case "machine_condenser_powered":
				case "machine_tower_small":
				case "machine_tower_large":
					addCondenser(tooltip);
					break;

				// GENERATORS
				case "machine_turbine":
				case "machine_large_turbine":
				case "machine_chungus":
				case "machine_generator":
				case "machine_diesel":
				case "machine_rtg_grey":
				case "machine_flare":
				case "machine_turbofan":
				case "machine_radgen":
				case "machine_amgen":
				case "machine_geo":
				case "machine_minirtg":
				case "rtg_polonium":
				case "machine_spp_bottom":
				case "machine_spp_top":
					addGenerator(tooltip);
					break;

				// HEAT EXCHANGERS
				case "machine_solar_boiler":
				case "heat_boiler":
				case "machine_industrial_boiler":
				case "machine_hephaestus":
					addBoiler(tooltip);
					break;

				// FUSION
				case "machine_zirnox":
					addBoiler(tooltip);
					addNuclear(tooltip);
					break;
				case "fusion_boiler":
					addMultiblock(tooltip);
					addModular(tooltip);
					addBoiler(tooltip);
					break;
				case "fusion_torus":
					addMultiblock(tooltip);
					addModular(tooltip);
					addNuclear(tooltip);
					break;
				case "fusion_klystron":
					addMultiblock(tooltip);
					addModular(tooltip);
					break;
				case "fusion_breeder":
				case "fusion_collector":
				case "fusion_coupler":
					addMultiblock(tooltip);
					addModular(tooltip);
					break;
				case "fusion_mhdt":
					addMultiblock(tooltip);
					addModular(tooltip);
					addGenerator(tooltip);
					break;
				case "struct_torus_core":
					addMultiblock(tooltip);
					addCore(tooltip);
					break;
				case "fusion_component":
					addMultiblock(tooltip);
					break;

				// SOME RANDOM MULTIBLOCKS
				case "struct_launcher_core":
				case "struct_launcher_core_large":
				case "struct_soyuz_core":
					addMultiblock(tooltip);
					addCore(tooltip);
					break;
				case "struct_launcher":
				case "struct_scaffold":
					addMultiblock(tooltip);
					break;

				case "icf_controller":
					addMultiblock(tooltip);
					addModular(tooltip);
					addCore(tooltip);
					break;
				case "icf_laser_component":
					addMultiblock(tooltip);
					addModular(tooltip);
					break;

				// RBMK
				case "rbmk_blank":
				case "rbmk_moderator":
				case "rbmk_control":
				case "rbmk_control_mod":
				case "rbmk_control_auto":
				case "rbmk_reflector":
				case "rbmk_absorber":
				case "rbmk_outgasser":
				case "rbmk_cooler":
				case "rbmk_storage":
					addMultiblock(tooltip);
					addModular(tooltip);
					break;
				case "rbmk_rod":
				case "rbmk_rod_mod":
				case "rbmk_rod_reasim":
				case "rbmk_rod_reasim_mod":
					addMultiblock(tooltip);
					addModular(tooltip);
					addNuclear(tooltip);
					break;
				case "rbmk_boiler":
				case "rbmk_heater":
					addMultiblock(tooltip);
					addModular(tooltip);
					addBoiler(tooltip);
					break;

				// WATZ
				case "watz":
					addMultiblock(tooltip);
					addBoiler(tooltip);
					addNuclear(tooltip);
					break;
				case "watz_pump":
					addMultiblock(tooltip);
					addCore(tooltip);
					break;
				case "struct_watz_core":
					addMultiblock(tooltip);
					addCore(tooltip);
					addBoiler(tooltip);
					addNuclear(tooltip);
					break;
				case "watz_element":
				case "watz_cooler":
				case "watz_casing":
					addMultiblock(tooltip);
					break;

				// ICF
				case "struct_icf_core":
					addMultiblock(tooltip);
					addCore(tooltip);
					addNuclear(tooltip);
					break;
				case "icf_component":
					addMultiblock(tooltip);
					addNuclear(tooltip);
					break;

				// HADRON
				case "pa_source":
				case "pa_detector":
					addMultiblock(tooltip);
					addModular(tooltip);
					addCore(tooltip);
					break;
				case "pa_beamline":
				case "pa_rfc":
				case "pa_quadrupole":
				case "pa_dipole":
					addMultiblock(tooltip);
					addModular(tooltip);
					break;
			}
		}
	}

	public static void append(List<String> tooltip,String s) {
		tooltip.set(tooltip.size()-1,tooltip.get(tooltip.size()-1)+s);
	}

	public static void addGenerator(List<String> tooltip) {
		tooltip.add(TextFormatting.AQUA+"< "+I18nUtil.resolveKey("trait._machine.power")+" >");
	}

	public static void addBoiler(List<String> tooltip) {
		tooltip.add(TextFormatting.GOLD+"< "+I18nUtil.resolveKey("trait._machine.steam")+" >");
	}

	public static void addCondenser(List<String> tooltip) {
		tooltip.add(TextFormatting.LIGHT_PURPLE+"< "+I18nUtil.resolveKey("trait._machine.condenser")+" >");
	}

	public static void addNuclear(List<String> tooltip) {
		append(tooltip,TextFormatting.DARK_GREEN+" -"+I18nUtil.resolveKey("trait._machine.nuclear"));
	}

	public static void addMultiblock(List<String> tooltip) {
		tooltip.add(TextFormatting.GRAY+"< "+I18nUtil.resolveKey("trait._machine.multiblock")+" >");
	}

	public static void addModular(List<String> tooltip) {
		append(tooltip,TextFormatting.GREEN+" -"+I18nUtil.resolveKey("trait._machine.modular"));
	}

	public static void addCore(List<String> tooltip) {
		append(tooltip,TextFormatting.RED+" -"+I18nUtil.resolveKey("trait._machine.multiblock.core"));
	}

	public static void addShit(List<String> tooltip) {
		tooltip.add(TextFormatting.DARK_RED+"< Obsolete >");
	}

	public static void addWIP(List<String> tooltip) {
		tooltip.add(TextFormatting.DARK_RED+"< Unfinished / Extremely buggy >");
	}

	public static void addUpdate(List<String> tooltip,String... oldKeys) {
		String buffer = I18nUtil.resolveKey(oldKeys[0]);
		for (int i = 1; i < oldKeys.length; i++) {
			if (i >= oldKeys.length-1)
				buffer = buffer+" and ";
			else
				buffer = buffer+", ";
			buffer = buffer+I18nUtil.resolveKey(oldKeys[i]);
		}
		tooltip.add(TextFormatting.DARK_PURPLE+"(Formerly "+buffer+")");
	}
}
