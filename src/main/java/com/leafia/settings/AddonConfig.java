package com.leafia.settings;

import com.hbm.config.GeneralConfig;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.dev.LeafiaDebug;
import com.leafia.settings._ConfigBuilder.LeafiaConfigError;
import com.leafia.unsorted.StructuralIntegrityHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AddonConfig {
	public static boolean disableLCAShaders = false;
	public static boolean useLeafiaTorex = true;
	public static boolean enableHealthMod = true;
    public static int dfcComponentRange = 50;
	public static boolean enableFirestorm = LeafiaDebug.isDevEnv;
	public static boolean enableWackySplashes = true;
	public static boolean enableAcidRainRender = true;
	public static int meteorDiverterMinAliveTime = 30*60;
	public static int meteorDiverterProtectionRadius = 3;
	public static boolean enableMeteorCraters = true;
	public static boolean enableSellacity = LeafiaDebug.isDevEnv;
	public static boolean enableBarrelSidePorts = true;
	public static boolean enableGovernedRPS = true;
	public static double governedRPS = 60;
	public static int maxOptimalTurbineLength = 5;
	public static double surgeTurbulenceMultiplier = 1;
	public static int ic10maxstack = 512;
	public static int ic10maxregisters = 64;
	public static boolean schizoMode = false;
	public static boolean disableAddonDFC = false;
	public static boolean disableAddonPWR = false;
	public static boolean disableAddonZIRNOX = false;
	public static class ConfigOverrides {
		public static boolean blockReplacement = true;
		public static void applyGeneralConfig() {
			GeneralConfig.enableBlockReplcement = blockReplacement;
		}
	}
	public static void loadFromConfig(){
		_ConfigBuilder builder = new _ConfigBuilder("leafia2");
		builder._separator();
		builder._category("IMPORTANT: The configs will not apply by default! Remove ? on start of each configs to apply.");
		builder._pushLine();
		builder._category("MIXINS");
		{
			enableWackySplashes = builder._boolean("enableWackySplashes",true);
			//enableAcidRainRender = builder._boolean("enableAcidRainRender",true); yeah no point
		}
		builder._separator();
		builder._category("OVERRIDE");
		{
			// I do not care about performance. This addon is aimed for newer playerbase.
			ConfigOverrides.blockReplacement = builder._boolean("ovr_enableBlockReplacement",true);
		}
		builder._separator();
		builder._category("OVERWRITES AND REPLACEMENTS (GAMEPLAY)");
		{
			builder._comment("Enable this if you would like the DFC to have the NTMain behavior");
			disableAddonDFC = builder._boolean("disableAddonDFC",false);
			builder._comment("Enable this if you would like the PWR to have the NTMain behavior");
			disableAddonPWR = builder._boolean("disableAddonPWR",false);
			builder._comment("Enable this if you would like the ZIRNOX to have the NTMain behavior");
			disableAddonZIRNOX = builder._boolean("disableAddonZIRNOX",false);
		}
		builder._separator();
		builder._category("GENERAL");
		{
			builder._comment("How far DFC components can reach");
			dfcComponentRange = builder._integer("dfcComponentRange",50);

			builder._comment("Whether the barrels should have side ports or not");
			enableBarrelSidePorts = builder._boolean("enableBarrelSidePorts",true);

			builder._comment("Replaces item radiations with LCE radiations");
			enableHealthMod = builder._boolean("enableRadClassification",true);

			builder._comment("How long the placer of Meteor Protection Beacon has to be alive (in seconds)");
			meteorDiverterMinAliveTime = builder._integer("meteorDiverterMinAliveTime",30*60);

			builder._comment("How far the Meteor Protection Beacon should protect (in chunks)");
			meteorDiverterProtectionRadius = builder._integer("meteorDiverterProtectionRadius",3);
			if (meteorDiverterProtectionRadius < 0)
				throw new LeafiaConfigError("meteorDiverterProtectionRadius should be positive!");

			builder._comment("Whether meteors should create custom craters or not");
			enableMeteorCraters = builder._boolean("enableMeteorCraters",true);

			builder._comment("Whether the modular turbine RPS should be capped or not");
			enableGovernedRPS = builder._boolean("enableGovernedRPS",true); builder._popLine();
			governedRPS = builder._double("minimumGovernedRPS",60);
			builder._comment("How many blades there can be per side until turbulence skyrockets");
			maxOptimalTurbineLength = builder._integer("maxOptimalTurbineLength",5);
			builder._comment("Multiplier of steam input surge turbulence for modular turbines");
			surgeTurbulenceMultiplier = builder._double("surgeTurbulenceMultiplier",1);

			builder._comment("Every biome acts like the digamma crater biome");
			schizoMode = builder._boolean("enableSchizoMode",false);

			builder._comment("Whether floating blocks and weirdly built buildings should collapse or not (WARNING: VERY LAGGY & CHALLENGING!)");
			StructuralIntegrityHandler.AUTOMATIC = builder._boolean("enableStructuralIntegrity",false);

			builder._comment("IC10 nodes will throw StackOverflow when stack count exceeds this number");
			ic10maxstack = builder._integer("ic10maxstack",512);

			builder._comment("IC10 nodes will throw OutOfRegisterBounds when register index exceeds this number");
			ic10maxregisters = builder._integer("ic10maxregisters",64);
		}
		builder._separator();
		builder._category("CLIENT");
		{
			builder._comment("Disables shaders used by this addon. This may make it compatible with Vivecraft");
			disableLCAShaders = builder._boolean("disableLCAShaders",false);
		}
		builder._separator();
		builder.saveConfig();
	}
	public static class FuelLives {
		public static class RodInfo {
			public final double life;
			public RodInfo(double life) {
				this.life = life;
			}
		}
		public static Map<String,RodInfo> map = new HashMap<>();
		public static void loadFromConfig() {
			_ConfigBuilder builder = new _ConfigBuilder("generic_fuels2");
			builder._category("IMPORTANT: The configs will not apply by default! Remove ? on start of each lines to apply.");
			builder._separator();
			builder._autoLineBreak = false;
			for (Entry<String,LeafiaRodItem> entry : LeafiaRodItem.fromResourceMap.entrySet()) {
				String s = entry.getKey().substring("leafia_rod_".length());
				LeafiaRodItem item = entry.getValue();
				if (item.life > 0) {
					item.life = builder._double(s+"-life",item.life);
					item.emission = builder._double(s+"-emission",item.emission);
					item.reactivity = builder._double(s+"-reactivity",item.reactivity);
					builder._separator();
				}
			}
			builder.saveConfig();
		}
	}
	static {
		loadFromConfig();
	}
}
