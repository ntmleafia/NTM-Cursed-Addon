package com.leafia.init;

import com.hbm.inventory.OreDictManager.DictFrame;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;

import static com.hbm.inventory.OreDictManager.*;

public class AddonOreDict {
	public static final DictFrame K = new DictFrame("Potassium");
	public static final DictFrame RB = new DictFrame("Rubidium");
	public static final DictFrame FR = new DictFrame("Francium");
	public static void registerOres() {
		K.ingot(AddonItems.ingot_potassium);
		RB.ingot(AddonItems.ingot_rubidium);
		FR.ingot(AddonItems.ingot_francium);
		//OSMIRIDIUM.block(AddonBlocks.block_welded_osmiridium); no you cannot cast it with 9 ingots
	}
}