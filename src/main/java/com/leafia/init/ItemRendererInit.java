package com.leafia.init;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.client.NTMClientRegistry;
import com.hbm.render.item.TEISRBase;
import com.hbm.render.tileentity.IItemRendererProvider;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.Elevators;
import com.leafia.contents.AddonBlocks.LetterSigns;
import com.leafia.contents.AddonBlocks.ModularTurbines;
import com.leafia.contents.AddonItems;
import com.leafia.contents.AddonItems.ElevatorStyles;
import com.leafia.contents.bomb.chud.NukeChudRender;
import com.leafia.contents.bomb.chud.NukeChudRender.NukeChudItemRender;
import com.leafia.contents.bomb.missile.customnuke.CustomNukeMissileItemRender;
import com.leafia.contents.building.storage.broof.BroofRender.BroofItemRender;
import com.leafia.contents.building.doors.renderers.ReactorDoorRender.ReactorDoorItemRender;
import com.leafia.contents.building.light.LightRender.LightItemRender;
import com.leafia.contents.building.sign.SignRender.SignItemRender;
import com.leafia.contents.building.storage.rack.RackRender.RackItemRender;
import com.leafia.contents.gear.advisor.AdvisorRender;
import com.leafia.contents.gear.utility.FuzzyIdentifierRender;
import com.leafia.contents.machines.elevators.EvBufferRender.EvBufferItemRender;
import com.leafia.contents.machines.elevators.EvPulleyRender.EvPulleyItemRender;
import com.leafia.contents.machines.elevators.car.styles.EvStyleItem;
import com.leafia.contents.machines.elevators.car.styles.EvStyleItemRender;
import com.leafia.contents.machines.elevators.floors.EvFloorRender.EvFloorItemRender;
import com.leafia.contents.machines.elevators.items.EvSpawnItemRender;
import com.leafia.contents.machines.elevators.items.WeightSpawnItemRender;
import com.leafia.contents.machines.heat.hpboiler.HPBoilerRender.HPBoilerItemRender;
import com.leafia.contents.machines.heat.rtheater.HeaterRTGRender;
import com.leafia.contents.machines.misc.heatex.CoolantHeatexRender;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineBlockBase;
import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentRender.ModularTurbineComponentItemRender;
import com.leafia.contents.machines.misc.wind_turbines.medium.WindTurbineMediumRender;
import com.leafia.contents.machines.misc.wind_turbines.medium.WindTurbineMediumRender.WindTurbineMediumItemRender;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseRender.AMSBaseItemRender;
import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterRender.AMSEmitterItemRender;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerRender.AMSStabilizerItemRender;
import com.leafia.contents.machines.powercores.dfc.render.DFCComponentRender;
import com.leafia.contents.machines.processing.mixingvat.MixingVatRenderNeo.MixingVatItemRenderNeo;
import com.leafia.contents.machines.reactors.lftr.processing.separator.SaltSeparatorRender.SaltSeparatorItemRender;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisItemRender;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityRender.FFDuctUtilityItemRender;
import com.leafia.contents.network.fluid.FluidDuctEquipmentRender.FluidDuctEquipmentItemRender;
import com.leafia.contents.network.spk_cable.SPKCableRender.SPKCableItemRender;
import com.leafia.contents.nonmachines.storage.fluid.fftank.FFTankRender.FFTankItemRender;
import com.leafia.settings.AddonConfig;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.*;
import java.util.Map.Entry;

public class ItemRendererInit {
	public static HashMap<Item,TEISRBase> renderers = new HashMap<>();
	//public static List<Item> fixFuckingLocations = new ArrayList<>();
	public static void preInit() {
		//fix(AddonItems.door_fuckoff);
		// DFC
		if (!AddonConfig.disableAddonDFC) {
			register(true,AddonBlocks.spk_cable,new SPKCableItemRender());
			register(false,ModBlocks.dfc_emitter,new DFCComponentRender());
			register(false,ModBlocks.dfc_receiver,new DFCComponentRender());
			register(false,ModBlocks.dfc_injector,new DFCComponentRender());
			register(false,ModBlocks.dfc_stabilizer,new DFCComponentRender());
			register(true,AddonBlocks.dfc_cemitter,new DFCComponentRender());
			register(true,AddonBlocks.dfc_exchanger,new DFCComponentRender());
			register(true,AddonBlocks.dfc_reinforced,new DFCComponentRender());
			register(true,AddonBlocks.dfc_pulser,new DFCComponentRender());
		}
		SignItemRender signRenderer = new SignItemRender();
		for (Block sign : LetterSigns.signs.values())
			register(sign,signRenderer);
		FFDuctUtilityItemRender ductUtilRenderer = new FFDuctUtilityItemRender();
		register(AddonBlocks.ff_pump,ductUtilRenderer);
		register(AddonBlocks.ff_converter,ductUtilRenderer);
		register(AddonBlocks.ff_filter,ductUtilRenderer);

		register(AddonBlocks.salt_separator,new SaltSeparatorItemRender());
		register(AddonBlocks.mixingvat,new MixingVatItemRenderNeo());
		register(AddonBlocks.coolant_heatex,new CoolantHeatexRender());

		PWRDebrisItemRender lwrDebrisRender = new PWRDebrisItemRender();
		register(AddonItems.pwr_piece,lwrDebrisRender);
		register(AddonItems.pwr_shard,lwrDebrisRender);
		register(AddonItems.pwr_shrapnel,lwrDebrisRender);

		register(AddonBlocks.lightUnlit,new LightItemRender());

		FluidDuctEquipmentItemRender equipment = new FluidDuctEquipmentItemRender();
		register(AddonBlocks.fluid_duct_gauge_mdl,equipment);
		register(AddonBlocks.fluid_duct_valve_mdl,equipment);
		register(AddonBlocks.fluid_duct_valve_mdl_rs,equipment);

		register(AddonItems.advisor,new AdvisorRender());
		register(AddonItems.fuzzy_identifier, FuzzyIdentifierRender.INSTANCE);

		//register(AddonItems.addon_battery_pack,new AddonBatteryPackItemRender());

		register(AddonBlocks.ams_base,new AMSBaseItemRender());
		register(AddonBlocks.ams_limiter,new AMSStabilizerItemRender());
		register(AddonBlocks.ams_emitter,new AMSEmitterItemRender());

		register(AddonItems.missile_customnuke,new CustomNukeMissileItemRender());

		register(AddonBlocks.broof,new BroofItemRender());

		register(AddonBlocks.ff_tank,new FFTankItemRender());

		EvStyleItemRender evr = new EvStyleItemRender();
		for (EvStyleItem styleItem : ElevatorStyles.styleItems)
			register(styleItem,evr);

		register(AddonItems.ev_spawn,new EvSpawnItemRender());
		register(AddonItems.weight_spawn,new WeightSpawnItemRender());

		register(AddonBlocks.heater_rt,new HeaterRTGRender());

		//register(AddonBlocks.rbmk_rod_realersim,RBMKItemRenderers.RBMK_ROD);
		//register(AddonBlocks.rbmk_rod_realersim_mod,RBMKItemRenderers.RBMK_ROD);

		EvFloorItemRender floorRender = new EvFloorItemRender();
		register(Elevators.s6_floor,floorRender);
		register(Elevators.skylift_floor,floorRender);
		register(Elevators.buffer,new EvBufferItemRender());
		register(Elevators.pulley,new EvPulleyItemRender());

		register(AddonBlocks.hp_boiler,new HPBoilerItemRender());

		register(AddonBlocks.reactor_door,new ReactorDoorItemRender());

		register(AddonBlocks.sellacity_rack,new RackItemRender());

		register(AddonBlocks.wind_turbine_medium,new WindTurbineMediumItemRender());

		//for (ModularTurbineBlockBase component : ModularTurbines.ALL_COMPONENTS_FOR_RENDER)
		//	ModularTurbineComponentItemRender.registerItemRenderer(component);
		ModularTurbineComponentItemRender renderer = new ModularTurbineComponentItemRender();
		for (ModularTurbineBlockBase component : ModularTurbines.ALL_COMPONENTS_FOR_RENDER)
			register(component,renderer);

		register(AddonBlocks.nuke_chud,new NukeChudItemRender());

		/*fix(AddonItems.ams_focus_blank);
		fix(AddonItems.ams_focus_booster);
		fix(AddonItems.ams_focus_limiter);
		fix(AddonItems.ams_focus_omega);
		fix(AddonItems.ams_focus_safe);

		fix(AddonItems.fuzzy_identifier);

		fix(LeafiaRods.leafRod);*/
	}
	public static void register(Item item,TEISRBase renderer) { renderers.put(item,renderer); }
	public static void register(Block block,TEISRBase renderer) { renderers.put(Item.getItemFromBlock(block),renderer); }
	public static void register(Item item,IItemRendererProvider provider) { renderers.put(item,provider.getRenderer(item)); }
	public static void register(Block block,IItemRendererProvider provider) { renderers.put(Item.getItemFromBlock(block),provider.getRenderer(Item.getItemFromBlock(block))); }

	private static void register(boolean doFix,Item item,TEISRBase renderer) { register(item,renderer); }
	private static void register(boolean doFix,Block block,TEISRBase renderer) { register(block,renderer); }
	private static void register(boolean doFix,Item item,IItemRendererProvider provider) { register(item,provider); }
	private static void register(boolean doFix,Block block,IItemRendererProvider provider) { register(block,provider); }
	//private static void fix(Item item) { fixFuckingLocations.add(item); }
	//private static void fix(Block block) { fixFuckingLocations.add(Item.getItemFromBlock(block)); }
	public static void apply() {
		for (Entry<Item,TEISRBase> entry : renderers.entrySet()) {
			NTMClientRegistry.bindTeisr(entry.getKey(), entry.getValue());
		}
	}
}
