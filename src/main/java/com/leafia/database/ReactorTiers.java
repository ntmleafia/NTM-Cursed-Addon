package com.leafia.database;

import com.hbm.blocks.ModBlocks;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.PWR;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactorTiers {
	public static final List<String> names = new ArrayList<>();
	public static final List<String> classes = new ArrayList<>();
	public static final Map<Item,Integer> redirection = new HashMap<>();
	static int tier = 0;
	public static void register() {
		addTier("zirnox","fission",ModBlocks.reactor_zirnox);
		addTier("pwr","fission",
				PWR.element,PWR.element_old,PWR.element_old_blank,
				PWR.control,PWR.reactor_control,
				PWR.channel,PWR.conductor,
				PWR.exchanger,
				PWR.terminal,PWR.hatch,PWR.hatch_alt,
				PWR.port,PWR.occs_in,PWR.occs_out,
				PWR.computer
		);
		addTier("rbmk","fission",
				ModBlocks.rbmk_absorber,ModBlocks.rbmk_autoloader,
				ModBlocks.rbmk_blank,ModBlocks.rbmk_boiler,
				ModBlocks.rbmk_control,ModBlocks.rbmk_control_mod,
				ModBlocks.rbmk_control_auto,ModBlocks.rbmk_autoloader,
				ModBlocks.rbmk_console,ModBlocks.rbmk_crane_console,
				ModBlocks.rbmk_cooler,ModBlocks.rbmk_heater,ModBlocks.rbmk_storage,
				ModBlocks.rbmk_rod,ModBlocks.rbmk_rod_mod,
				ModBlocks.rbmk_rod_reasim,ModBlocks.rbmk_rod_reasim_mod,
				AddonBlocks.rbmk_rod_realersim,AddonBlocks.rbmk_rod_realersim_mod
		);
		addTier("watz","fission",
				ModBlocks.watz,ModBlocks.struct_watz_core,
				ModBlocks.watz_casing,ModBlocks.watz_cooler,
				ModBlocks.watz_element,ModBlocks.watz_pump
		);
		addTier("fusion","fusion",
				ModBlocks.fusion_torus,ModBlocks.fusion_klystron,
				ModBlocks.fusion_torus,ModBlocks.fusion_component,ModBlocks.struct_torus_core,
				ModBlocks.fusion_klystron,ModBlocks.fusion_boiler,
				ModBlocks.fusion_mhdt,ModBlocks.fusion_breeder,
				ModBlocks.fusion_collector,ModBlocks.fusion_coupler
		);
		addTier("icf","fusion",
				ModBlocks.icf,ModBlocks.struct_icf_core,
				ModBlocks.icf_component,
				ModBlocks.icf_controller,ModBlocks.icf_laser_component
		);
		addTier("ams","core",
				AddonBlocks.ams_base,AddonBlocks.ams_emitter,AddonBlocks.ams_limiter
		);
		addTier("dfc","core",
				ModBlocks.dfc_core,
				ModBlocks.dfc_emitter,ModBlocks.dfc_receiver,
				ModBlocks.dfc_stabilizer,ModBlocks.dfc_injector,
				AddonBlocks.dfc_exchanger,AddonBlocks.dfc_reinforced
		);
	}
	static void addTier(String translationKey,String reactorClass,Block... blocks) {
		names.add(translationKey);
		classes.add(reactorClass);
		for (Block block : blocks)
			redirection.put(Item.getItemFromBlock(block),tier);
		tier++;
	}
}
