package com.leafia.init;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonBlocks.Ores;
import com.leafia.contents.AddonItems.Resources;

import static com.hbm.inventory.OreDictManager.*;
import static com.hbm.inventory.material.MaterialShapes.*;

public class AddonOreDict {
	public static final DictFrame K = new DictFrame("Potassium");
	public static final DictFrame RB = new DictFrame("Rubidium");
	public static final DictFrame FR = new DictFrame("Francium");
	public static final DictFrame XN = new DictFrame("Xanaxium");
	public static final DictFrame NC279 = new DictFrame("Nc279");
	public static final DictFrame NC273 = new DictFrame("Nc273");
	public static final DictFrame NC269 = new DictFrame("Nc269");
	public static final DictFrame TN = new DictFrame("Taintium");
	public static final DictFrame TNALLOY = new DictFrame("TnAlloy");
	public static final DictFrame MYSTICITE = new DictFrame("Mysticite");
	public static final DictFrame CH = new DictFrame("Chydalium");
	public static final DictFrame FS = new DictFrame("Fissium");
	public static final DictFrame FSALLOY = new DictFrame("Fissite");
	public static final DictFrame CORIUM = new DictFrame("Corium");
	public static final DictFrame ZETA = new DictFrame("Zetalite");
	public static final DictFrame CHERNOBYL = new DictFrame("Chernobylite");
	public static final DictFrame MANA = new DictFrame("Mana");
	public static final DictGroup ANY_ULTRAALLOY = new DictGroup("AnyUltraAlloy",MYSTICITE);
	public static void registerOres() {
		K.ingot(Resources.ingot_potassium);
		RB.ingot(Resources.ingot_rubidium);
		FR.ingot(Resources.ingot_francium);
		SRN.nugget(Resources.nugget_schraranium);
		OSMIRIDIUM.dust(ModItems.powder_osmiridium);
		XN.ingot(Resources.ingot_xanaxium).dust(Resources.powder_xanaxium);
		//OSMIRIDIUM.block(AddonBlocks.block_welded_osmiridium); no you cannot cast it with 9 ingots
		CORIUM.block(ModBlocks.block_corium);
		CORIUM.block(ModBlocks.block_corium_cobble);
		CHERNOBYL.ore(Ores.ore_corium_chernobylite);
		ZETA.ore(Ores.ore_corium_zetalite);
		MANA.dust(ModItems.powder_magic);
		NC279.ingot(Resources.ingot_nc279);
		NC279.dust(Resources.powder_nc279);
		NC279.billet(Resources.billet_nc279);
		TN.ingot(Resources.ingot_taintium);
		TN.dust(Resources.powder_taintium);
		FS.ingot(Resources.ingot_fissium);
		FS.billet(Resources.billet_fissium);
		FSALLOY.ingot(Resources.ingot_fissite);
		FSALLOY.plate(Resources.plate_fissite);
		MYSTICITE.ingot(Resources.ingot_mysticite);
		MYSTICITE.plate(Resources.plate_mysticite);
		TNALLOY.ingot(Resources.ingot_tnalloy);
		ANY_RESISTANTALLOY.addFrames(TNALLOY);
		CH.ingot(Resources.ingot_chydalium);
		CH.dust(Resources.powder_chydalium);
		CH.billet(Resources.billet_chydalium);
	}
	public static void registerGroups() {
		ANY_ULTRAALLOY.addPrefix(INGOT,true).addPrefix(LIGHTRECEIVER,true).addPrefix(PLATE,true);
	}
}