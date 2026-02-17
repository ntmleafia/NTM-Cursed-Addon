package com.leafia.init;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.render.item.ItemRenderBase;
import com.hbm.render.item.ItemRenderBatteryPack;
import com.hbm.render.item.TEISRBase;
import com.hbm.render.tileentity.IItemRendererProvider;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.LetterSigns;
import com.leafia.contents.AddonItems;
import com.leafia.contents.AddonItems.ElevatorStyles;
import com.leafia.contents.AddonItems.LeafiaRods;
import com.leafia.contents.bomb.missile.customnuke.CustomNukeMissileItemRender;
import com.leafia.contents.building.broof.BroofRender;
import com.leafia.contents.building.broof.BroofRender.BroofItemRender;
import com.leafia.contents.building.light.LightRender;
import com.leafia.contents.building.light.LightRender.LightItemRender;
import com.leafia.contents.building.sign.SignRender;
import com.leafia.contents.building.sign.SignRender.SignItemRender;
import com.leafia.contents.control.battery.AddonBatteryPackItemRender;
import com.leafia.contents.gear.advisor.AdvisorRender;
import com.leafia.contents.machines.elevators.car.styles.EvStyleItem;
import com.leafia.contents.machines.elevators.car.styles.EvStyleItemRender;
import com.leafia.contents.machines.elevators.items.EvSpawnItemRender;
import com.leafia.contents.machines.elevators.items.WeightSpawnItemRender;
import com.leafia.contents.machines.misc.heatex.CoolantHeatexRender;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseRender;
import com.leafia.contents.machines.powercores.ams.base.AMSBaseRender.AMSBaseItemRender;
import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterRender;
import com.leafia.contents.machines.powercores.ams.emitter.AMSEmitterRender.AMSEmitterItemRender;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerRender;
import com.leafia.contents.machines.powercores.ams.stabilizer.AMSStabilizerRender.AMSStabilizerItemRender;
import com.leafia.contents.machines.powercores.dfc.render.DFCComponentRender;
import com.leafia.contents.machines.processing.mixingvat.MixingVatRender;
import com.leafia.contents.machines.processing.mixingvat.MixingVatRender.MixingVatItemRender;
import com.leafia.contents.machines.processing.mixingvat.MixingVatRenderNeo;
import com.leafia.contents.machines.processing.mixingvat.MixingVatRenderNeo.MixingVatItemRenderNeo;
import com.leafia.contents.machines.reactors.lftr.processing.separator.SaltSeparatorRender;
import com.leafia.contents.machines.reactors.lftr.processing.separator.SaltSeparatorRender.SaltSeparatorItemRender;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisItemRender;
import com.leafia.contents.network.ff_duct.utility.FFDuctUtilityRender.FFDuctUtilityItemRender;
import com.leafia.contents.network.fluid.FluidDuctEquipmentRender.FluidDuctEquipmentItemRender;
import com.leafia.contents.network.spk_cable.SPKCableRender.SPKCableItemRender;
import com.leafia.contents.nonmachines.fftank.FFTankRender;
import com.leafia.contents.nonmachines.fftank.FFTankRender.FFTankItemRender;
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
		register(true,AddonBlocks.spk_cable,new SPKCableItemRender());
		register(false,ModBlocks.dfc_emitter,new DFCComponentRender());
		register(false,ModBlocks.dfc_receiver,new DFCComponentRender());
		register(false,ModBlocks.dfc_injector,new DFCComponentRender());
		register(false,ModBlocks.dfc_stabilizer,new DFCComponentRender());
		register(true,AddonBlocks.dfc_cemitter,new DFCComponentRender());
		register(true,AddonBlocks.dfc_exchanger,new DFCComponentRender());
		register(true,AddonBlocks.dfc_reinforced,new DFCComponentRender());
		SignItemRender signRenderer = new SignItemRender();
		for (Block sign : LetterSigns.signs.values())
			register(sign,signRenderer);
		FFDuctUtilityItemRender ductUtilRenderer = new FFDuctUtilityItemRender();
		register(AddonBlocks.ff_pump,ductUtilRenderer);
		register(AddonBlocks.ff_converter,ductUtilRenderer);

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

		/*fix(AddonItems.ams_focus_blank);
		fix(AddonItems.ams_focus_booster);
		fix(AddonItems.ams_focus_limiter);
		fix(AddonItems.ams_focus_omega);
		fix(AddonItems.ams_focus_safe);

		fix(AddonItems.fuzzy_identifier);

		fix(LeafiaRods.leafRod);*/
	}
	private static void register(Item item,TEISRBase renderer) { renderers.put(item,renderer); }
	private static void register(Block block,TEISRBase renderer) { renderers.put(Item.getItemFromBlock(block),renderer); }
	private static void register(Item item,IItemRendererProvider provider) { renderers.put(item,provider.getRenderer(item)); }
	private static void register(Block block,IItemRendererProvider provider) { renderers.put(Item.getItemFromBlock(block),provider.getRenderer(Item.getItemFromBlock(block))); }

	private static void register(boolean doFix,Item item,TEISRBase renderer) { register(item,renderer); }
	private static void register(boolean doFix,Block block,TEISRBase renderer) { register(block,renderer); }
	private static void register(boolean doFix,Item item,IItemRendererProvider provider) { register(item,provider); }
	private static void register(boolean doFix,Block block,IItemRendererProvider provider) { register(block,provider); }
	//private static void fix(Item item) { fixFuckingLocations.add(item); }
	//private static void fix(Block block) { fixFuckingLocations.add(Item.getItemFromBlock(block)); }
	public static void apply() {
		for (Entry<Item,TEISRBase> entry : renderers.entrySet()) {
			entry.getKey().setTileEntityItemStackRenderer(entry.getValue());
		}
	}
}
