package com.leafia.init;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class AddonAdvancements {
	public static Advancement openpwr;
	public static Advancement nukepwr;
	public static Advancement nukebwr;
	public static Advancement genericfuel;
	public static Advancement loseadvisor;
	public static void init(MinecraftServer serv) {
		AdvancementManager adv = serv.getAdvancementManager();
		openpwr = get(adv,"openpwr");
		nukepwr = get(adv,"nukepwr");
		nukebwr = get(adv,"nukebwr");
		genericfuel = get(adv,"genericfuel");
		loseadvisor = get(adv,"loseadvisor");
	}
	static Advancement get(AdvancementManager manager,String path) {
		return manager.getAdvancement(new ResourceLocation("leafia",path));
	}
}
