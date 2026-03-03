package com.leafia.settings;

import com.hbm.config.GeneralConfig;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.dev.LeafiaDebug;
import com.leafia.settings._ConfigBuilder.LeafiaConfigError;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AddonConfig {
	public static boolean useLeafiaTorex = true;
	public static boolean enableHealthMod = true;
    public static int dfcComponentRange = 50;
	public static boolean enableFirestorm = LeafiaDebug.isDevEnv;
	public static boolean enableWackySplashes = true;
	public static boolean enableAcidRainRender = true;
	public static int meteorDiverterMinAliveTime = 30*60;
	public static int meteorDiverterProtectionRadius = 3;
	public static boolean enableMeteorCraters = true;
	public static class ConfigOverrides {
		public static boolean blockReplacement = true;
		public static void applyGeneralConfig() {
			GeneralConfig.enableBlockReplcement = blockReplacement;
		}
	}
	public static void loadFromConfig(){
		_ConfigBuilder builder = new _ConfigBuilder("leafia");
		builder._separator();
		builder._category("ASM");
		{
			enableWackySplashes = builder._boolean("enableWackySplashes",true);
			enableAcidRainRender = builder._boolean("enableAcidRainRender",true);
		}
		builder._separator();
		builder._category("OVERRIDE");
		{
			// I do not care about performance. This addon is aimed for newer playerbase.
			ConfigOverrides.blockReplacement = builder._boolean("ovr_enableBlockReplacement",true);
		}
		builder._separator();
		builder._category("GENERAL");
		{
			builder._comment("Replaces item radiations with LCE radiations");
			enableHealthMod = builder._boolean("enableRadClassification",true);

			builder._comment("How far DFC components can reach");
			dfcComponentRange = builder._integer("dfcComponentRange",50);

			builder._comment("How long the placer of Meteor Protection Beacon has to be alive (in seconds)");
			meteorDiverterMinAliveTime = builder._integer("meteorDiverterMinAliveTime",30*60);

			builder._comment("How far the Meteor Protection Beacon should protect (in chunks)");
			meteorDiverterProtectionRadius = builder._integer("meteorDiverterProtectionRadius",3);
			if (meteorDiverterProtectionRadius < 0)
				throw new LeafiaConfigError("meteorDiverterProtectionRadius should be positive!");

			builder._comment("Whether meteors should create custom craters or not");
			enableMeteorCraters = builder._boolean("enableMeteorCraters",true);
		}
		builder._separator();
		builder._category("CLIENT");
		{

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
			_ConfigBuilder builder = new _ConfigBuilder("generic_fuels");
			builder._category("Remove underscore in the file to apply");
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
			builder.changePath("_generic_fuels");
			builder.saveConfig();
		}
	}
	static {
		loadFromConfig();
	}
}
