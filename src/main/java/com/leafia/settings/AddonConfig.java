package com.leafia.settings;

import net.minecraftforge.common.config.Configuration;

public class AddonConfig {
	public static boolean useLeafiaTorex = true;
	public static boolean enableHealthMod = true;
	public static void loadFromConfig(Configuration config){
		final String CATEGORY_GENERAL = "01_general";
		//enableHealthMod = config.get(CATEGORY_GENERAL, "1.00_enableRadClassification", true).getBoolean(true);
		final String CATEGORY_CLIENT = "02_client";
		//useLeafiaTorex = config.get(CATEGORY_CLIENT, "1.00_useLeafiaTorex", true).getBoolean(true);
	}
}
