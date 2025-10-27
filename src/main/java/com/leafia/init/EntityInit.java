package com.leafia.init;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityInit {
	public static void preInit() {
		int i = 0;
		EntityRegistry.registerModEntity(new ResourceLocation("leafia", "entity_nuke_folkvangr"), EntityNukeFolkvangr.class, "entity_nuke_folkvangr", i++, MainRegistry.instance, 1000, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("leafia", "entity_cursed_torex"), EntityNukeTorex.class, "entity_cursed_torex", i++, MainRegistry.instance, 1000, 1, true);
	}
}
