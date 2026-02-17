package com.leafia.init;

import com.hbm.entity.effect.EntityNukeTorex;
import com.leafia.AddonBase;
import com.leafia.contents.bomb.missile.customnuke.entity.CustomNukeMissileEntity;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr;
import com.leafia.contents.machines.elevators.car.ElevatorEntity;
import com.leafia.contents.machines.elevators.weight.EvWeightEntity;
import com.leafia.contents.machines.elevators.weight.EvWeightRender;
import com.leafia.contents.machines.powercores.dfc.debris.AbsorberShrapnelEntity;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityInit {
	public static void preInit() {
		int i = 0;
		EntityRegistry.registerModEntity(new ResourceLocation("leafia", "entity_nuke_folkvangr"), EntityNukeFolkvangr.class, "entity_nuke_folkvangr", i++, AddonBase.instance, 1000, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("leafia", "entity_cursed_torex"), EntityNukeTorex.class, "entity_cursed_torex", i++, AddonBase.instance, 1000, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("leafia", "entity_dfc_absorber_shrapnel"), AbsorberShrapnelEntity.class, "entity_dfc_absorber_shrapnel", i++, AddonBase.instance, 1000, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("leafia", "entity_lwr_debris"), PWRDebrisEntity.class, "entity_lwr_debris", i++, AddonBase.instance, 1000, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("leafia", "entity_missile_customnuke"), CustomNukeMissileEntity.class, "entity_missile_customnuke", i++, AddonBase.instance, 1000, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation("leafia","entity_elevator"),ElevatorEntity.class,"entity_elevator",i++,AddonBase.instance,1000,1,true);
		EntityRegistry.registerModEntity(new ResourceLocation("leafia","entity_counterweight"),EvWeightEntity.class,"entity_counterweight",i++,AddonBase.instance,1000,1,true);
	}
}
