package com.leafia.settings;

public class AddonConfig {
	public static boolean useLeafiaTorex = true;
	public static boolean enableHealthMod = true;
    public static int dfcComponentRange = 50;
	public static boolean enableFirestorm = false;
	public static boolean enableWackySplashes = true;
	public static boolean enableAcidRainRender = true;
	static {
		loadFromConfig();
	}
	public static void loadFromConfig(){
		_ConfigBuilder builder = new _ConfigBuilder();
		builder.createEmptyFile();
		builder.loadConfig();
		builder._separator();
		builder._category("ASM");
		{
			enableWackySplashes = builder._boolean("enableWackySplashes",true);
			enableAcidRainRender = builder._boolean("enableAcidRainRender",true);
		}
		builder._separator();
		builder._category("GENERAL");
		{
			builder._comment("Replaces item radiations with LCE radiations");
			enableHealthMod = builder._boolean("enableRadClassification",true);

			builder._comment("How far DFC components can reach");
			dfcComponentRange = builder._integer("dfcComponentRange",50);
		}
		builder._separator();
		builder._category("CLIENT");
		{

		}
		builder._separator();
		builder.saveConfig();
	}
}
