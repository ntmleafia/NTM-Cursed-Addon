package com.leafia.init;

import com.hbm.hazard.HazardData;
import static com.hbm.hazard.HazardRegistry.*;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.type.IHazardType;
import com.hbm.inventory.OreDictManager;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems;
import com.leafia.dev.hazards.ItemRads;
import com.leafia.dev.hazards.types.HazardTypeSharpEdges;

public class AddonHazards {
	public static final IHazardType SHARP = new HazardTypeSharpEdges();
	//call after com.hbm.hazard.HazardRegistry.registerItems
	public static void register() {
		//cobalt60.register(ModItems.ingot_co60);
		//HashMap<String,HazardData> dat = HazardSystem.oreMap;
		//Map<String,Float> fuck = dictMap.get(OreDictManager.CO60);
		//System.out.println(fuck);
		ItemRads.actinium227.register(OreDictManager.AC227);
		ItemRads.americium241.register(OreDictManager.AM241);
		ItemRads.americium242.register(OreDictManager.AM242);
		ItemRads.americiumRG.register(OreDictManager.AMRG);
		ItemRads.cobalt60.register(OreDictManager.CO60);
		ItemRads.gold198.register(OreDictManager.AU198);
		ItemRads.lead209.register(OreDictManager.PB209);
		ItemRads.neptunium237.register(OreDictManager.NP237);
		ItemRads.plutonium.register(OreDictManager.PU);
		ItemRads.plutoniumRG.register(OreDictManager.PURG);
		ItemRads.plutonium238.register(OreDictManager.PU238);
		ItemRads.plutonium239.register(OreDictManager.PU239);
		ItemRads.plutonium240.register(OreDictManager.PU240);
		ItemRads.plutonium241.register(OreDictManager.PU241);
		ItemRads.polonium210.register(OreDictManager.PO210);
		ItemRads.radium226.register(OreDictManager.RA226);
		ItemRads.schrabidium326.register(OreDictManager.SA326);
		ItemRads.solinium327.register(OreDictManager.SA327);
		ItemRads.schrabidate.register(OreDictManager.SBD);
		ItemRads.schraranium.register(OreDictManager.SRN);
		ItemRads.technetium99.register(OreDictManager.TC99);
		ItemRads.thorium232.register(OreDictManager.TH232);
		ItemRads.uranium.register(OreDictManager.U);
		ItemRads.uranium233.register(OreDictManager.U233);
		ItemRads.uranium235.register(OreDictManager.U235);
		ItemRads.uranium238.register(OreDictManager.U238);
		ItemRads.waste.copy().multiply(3).register(ModItems.nuclear_waste);
		ItemRads.waste_v.copy().multiply(3).register(ModItems.nuclear_waste_vitrified);
		ItemRads.waste.copy().multiply(0.3).register(ModItems.nuclear_waste_tiny);
		ItemRads.waste_v.copy().multiply(0.3).register(ModItems.nuclear_waste_vitrified_tiny);
		ItemRads.waste.register(ModItems.billet_nuclear_waste);

		HazardSystem.register(AddonItems.dfcsh_cable,makeData(SHARP,5).addEntry(DIGAMMA,0.003));
		HazardSystem.register(AddonItems.dfcsh_core,makeData(HOT,10));
		HazardSystem.register(AddonItems.dfcsh_corner,makeData(SHARP,5).addEntry(DIGAMMA,0.005));
		HazardSystem.register(AddonItems.dfcsh_front,makeData(DIGAMMA,0.004F));
		HazardSystem.register(AddonItems.dfcsh_beam,makeData(SHARP,25).addEntry(DIGAMMA,0.002));
	}
	// why'd you had to make these private
	public static HazardData makeData(IHazardType hazard) { return (new HazardData()).addEntry(hazard); }
	public static HazardData makeData(IHazardType hazard, float level) { return (new HazardData()).addEntry(hazard, (double)level); }
	public static HazardData makeData(IHazardType hazard, float level, boolean override) { return (new HazardData()).addEntry(hazard, (double)level, override); }
}
