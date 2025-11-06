package com.leafia.dev.hazards;

import com.hbm.hazard.HazardData;
import static com.hbm.hazard.HazardRegistry.*;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.type.IHazardType;
import com.hbm.inventory.OreDictManager;
import com.leafia.contents.AddonItems;
import com.leafia.dev.hazards.types.HazardTypeSharpEdges;

public class AddonHazards {
	public static final IHazardType SHARP = new HazardTypeSharpEdges();
	//call after com.hbm.hazard.HazardRegistry.registerItems
	public static void register() {
		//cobalt60.register(ModItems.ingot_co60);
		//HashMap<String,HazardData> dat = HazardSystem.oreMap;
		//Map<String,Float> fuck = dictMap.get(OreDictManager.CO60);
		//System.out.println(fuck);
		ItemRads.cobalt60.register(OreDictManager.CO60);

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
