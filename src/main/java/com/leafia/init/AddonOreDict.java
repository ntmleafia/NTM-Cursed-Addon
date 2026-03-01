package com.leafia.init;

import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;

import static com.hbm.inventory.OreDictManager.*;

public class AddonOreDict {
	public static final DictFrame K = new DictFrame("Potassium");
	public static final DictFrame RB = new DictFrame("Rubidium");
	public static final DictFrame FR = new DictFrame("Francium");
	public static final DictFrame XN = new DictFrame("Xanaxium");
	public static void registerOres() {
		K.ingot(AddonItems.ingot_potassium);
		RB.ingot(AddonItems.ingot_rubidium);
		FR.ingot(AddonItems.ingot_francium);
		SRN.nugget(AddonItems.nugget_schraranium);
		OSMIRIDIUM.dust(ModItems.powder_osmiridium);
		XN.ingot(AddonItems.ingot_xanaxium).dust(AddonItems.powder_xanaxium);
		//OSMIRIDIUM.block(AddonBlocks.block_welded_osmiridium); no you cannot cast it with 9 ingots
	}
}