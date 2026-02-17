package com.leafia.init;

import com.hbm.hazard.HazardData;
import static com.hbm.hazard.HazardRegistry.*;

import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.modifier.IHazardModifier;
import com.hbm.hazard.type.*;
import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.inventory.RecipesCommon;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;
import com.leafia.database.AddonOreDictHazards;
import com.leafia.dev.items.itembase.AddonItemHazardBase;
import com.leafia.init.hazards.ItemRads;
import com.leafia.init.hazards.types.HazardTypeAlkaline;
import com.leafia.init.hazards.types.HazardTypeSharpEdges;
import com.leafia.init.hazards.types.LCERad;
import com.leafia.init.hazards.types.radiation.*;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class AddonHazards {
	public static final IHazardType SHARP = new HazardTypeSharpEdges();
	public static final IHazardType ALKALINE = new HazardTypeAlkaline();
	//call after com.hbm.hazard.HazardRegistry.registerItems
	public static void register() {
		//cobalt60.register(ModItems.ingot_co60);
		//HashMap<String,HazardData> dat = HazardSystem.oreMap;
		//Map<String,Float> fuck = dictMap.get(OreDictManager.CO60);
		//System.out.println(fuck);

		ItemRads.actinium227.register(OreDictManager.AC227);
		ItemRads.americium241.register(OreDictManager.AM241);
		ItemRads.americium242.register(OreDictManager.AM242);
		ItemRads.americiumRG.register(OreDictManager.AMRG);
		ItemRads.cobalt60.register(OreDictManager.CO60);
		ItemRads.gold198.register(OreDictManager.AU198);
		ItemRads.lead209.register(OreDictManager.PB209);
		ItemRads.neptunium237.register(OreDictManager.NP237);
		ItemRads.plutonium.register(OreDictManager.PU);
		ItemRads.plutoniumRG.register(OreDictManager.PURG);
		ItemRads.plutonium238.register(OreDictManager.PU238);
		ItemRads.plutonium239.register(OreDictManager.PU239);
		ItemRads.plutonium240.register(OreDictManager.PU240);
		ItemRads.plutonium241.register(OreDictManager.PU241);
		ItemRads.polonium210.register(OreDictManager.PO210);
		ItemRads.radium226.register(OreDictManager.RA226);
		ItemRads.schrabidium326.register(OreDictManager.SA326);
		ItemRads.solinium327.register(OreDictManager.SA327);
		ItemRads.schrabidate.register(OreDictManager.SBD);
		ItemRads.schraranium.register(OreDictManager.SRN);
		ItemRads.technetium99.register(OreDictManager.TC99);
		ItemRads.thorium232.register(OreDictManager.TH232);
		ItemRads.uranium.register(OreDictManager.U);
		ItemRads.uranium233.register(OreDictManager.U233);
		ItemRads.uranium235.register(OreDictManager.U235);
		ItemRads.uranium238.register(OreDictManager.U238);
		ItemRads.waste.copy().multiply(3).register(ModItems.nuclear_waste);
		ItemRads.waste_v.copy().multiply(3).register(ModItems.nuclear_waste_vitrified);
		ItemRads.waste.copy().multiply(0.3).register(ModItems.nuclear_waste_tiny);
		ItemRads.waste_v.copy().multiply(0.3).register(ModItems.nuclear_waste_vitrified_tiny);
		ItemRads.waste.register(ModItems.billet_nuclear_waste);

		HazardSystem.register(AddonItems.dfcsh_cable,makeData(SHARP,5).addEntry(DIGAMMA,0.003));
		HazardSystem.register(AddonItems.dfcsh_core,makeData(HOT,10));
		HazardSystem.register(AddonItems.dfcsh_corner,makeData(SHARP,5).addEntry(DIGAMMA,0.005));
		HazardSystem.register(AddonItems.dfcsh_front,makeData(DIGAMMA,0.004F));
		HazardSystem.register(AddonItems.dfcsh_beam,makeData(SHARP,25).addEntry(DIGAMMA,0.002));

		registerHazardRegular(OreDictManager.LI,new HazardEntry(ALKALINE,1));
		registerHazardRegular(OreDictManager.NA,new HazardEntry(ALKALINE,2));
		registerHazardRegular(AddonOreDict.K,new HazardEntry(ALKALINE,3));
		registerHazardRegular(AddonOreDict.RB,new HazardEntry(ALKALINE,4));
		registerHazardRegular(OreDictManager.CS,new HazardEntry(ALKALINE,5));
		registerHazardRegular(OreDictManager.CS137,new HazardEntry(ALKALINE,5));
		registerHazardRegular(AddonOreDict.FR,new HazardEntry(ALKALINE,6));
		registerHazardRegular(OreDictManager.SR,new HazardEntry(ALKALINE,1.5));
		registerHazardRegular(OreDictManager.SR90,new HazardEntry(ALKALINE,1.5));

		registerHazardRegular(OreDictManager.PB,new HazardEntry(TOXIC,1.75));
		registerHazardRegular(OreDictManager.BE,new HazardEntry(TOXIC,1.75));

		HazardSystem.register(ModItems.blades_desh,makeData(SHARP,40));
		HazardSystem.register(ModItems.blades_steel,makeData(SHARP,40));
		HazardSystem.register(ModItems.blades_titanium,makeData(SHARP,40));
		HazardSystem.register(ModItems.blades_advanced_alloy,makeData(SHARP,40));

		HazardSystem.register(ModItems.nugget_zirconium,makeData(SHARP,10));
		HazardSystem.register(ModItems.debris_shrapnel,makeData(SHARP,1));

		HazardSystem.register(AddonItems.pwr_shrapnel,makeData(SHARP,35));
		HazardSystem.register(AddonItems.pwr_shard,makeData(SHARP,85));

		registerHazard(OreDictManager.OSMIRIDIUM,new HazardEntry(DIGAMMA,0.004));
		HazardSystem.register(AddonBlocks.block_welded_osmiridium,makeData(DIGAMMA,0.04f));

		for (AddonItemHazardBase hazardItem : AddonItemHazardBase.ALL_HAZARD_ITEMS) {
			HazardEntry entry_contamination = null;
			HazardEntry entry_alpha = null;
			HazardEntry entry_beta = null;
			HazardEntry entry_gamma = null;
			HazardEntry entry_x = null;
			HazardEntry entry_neutrons = null;
			HazardEntry entry_radon = null;
			if (hazardItem.radContainer != null) {
				entry_contamination = generateHazEntry(RADIATION,hazardItem.radContainer.activation,hazardItem.mods);
				entry_alpha = generateHazEntry(Alpha.INSTANCE,hazardItem.radContainer.alpha,hazardItem.mods);
				entry_beta = generateHazEntry(Beta.INSTANCE,hazardItem.radContainer.beta,hazardItem.mods);
				entry_gamma = generateHazEntry(Gamma.INSTANCE,hazardItem.radContainer.gamma,hazardItem.mods);
				entry_x = generateHazEntry(XRay.INSTANCE,hazardItem.radContainer.x,hazardItem.mods);
				entry_neutrons = generateHazEntry(Neutrons.INSTANCE,hazardItem.radContainer.neutrons,hazardItem.mods);
				entry_radon = generateHazEntry(Radon.INSTANCE,hazardItem.radContainer.radon,hazardItem.mods);
			}
			HazardEntry entry_digamma = generateHazEntry(DIGAMMA,hazardItem.digamma,hazardItem.mods);
			HazardEntry entry_fire = generateHazEntry(HOT,hazardItem.fire,hazardItem.mods);
			HazardEntry entry_cryogenic = generateHazEntry(COLD,hazardItem.cryogenic,hazardItem.mods);
			HazardEntry entry_toxic = generateHazEntry(TOXIC,hazardItem.toxic,hazardItem.mods);
			HazardEntry entry_blinding = generateHazEntry(BLINDING,hazardItem.blinding ? 110 : 0,hazardItem.mods);
			HazardEntry entry_asbestos = generateHazEntry(ASBESTOS,hazardItem.asbestos,hazardItem.mods);
			HazardEntry entry_coal = generateHazEntry(COAL,hazardItem.coal,hazardItem.mods);
			HazardEntry entry_alkaline = generateHazEntry(ALKALINE,hazardItem.alkaline,hazardItem.mods);
			HazardEntry entry_explosive = generateHazEntry(EXPLOSIVE,hazardItem.explosive,hazardItem.mods);
			HazardEntry entry_sharp = generateHazEntry(SHARP,hazardItem.sharp,hazardItem.mods);
			HazardData data = new HazardData();
			if (entry_alpha != null) data.addEntry(entry_alpha);
			if (entry_beta != null) data.addEntry(entry_beta);
			if (entry_gamma != null) data.addEntry(entry_gamma);
			if (entry_x != null) data.addEntry(entry_x);
			if (entry_neutrons != null) data.addEntry(entry_neutrons);
			if (entry_contamination != null) data.addEntry(entry_contamination);
			if (entry_radon != null) data.addEntry(entry_radon);
			if (entry_digamma != null) data.addEntry(entry_digamma);
			if (entry_fire != null) data.addEntry(entry_fire);
			if (entry_cryogenic != null) data.addEntry(entry_cryogenic);
			if (entry_toxic != null) data.addEntry(entry_toxic);
			if (entry_blinding != null) data.addEntry(entry_blinding);
			if (entry_asbestos != null) data.addEntry(entry_asbestos);
			if (entry_coal != null) data.addEntry(entry_coal);
			if (entry_alkaline != null) data.addEntry(entry_alkaline);
			if (entry_explosive != null) data.addEntry(entry_explosive);
			if (entry_sharp != null) data.addEntry(entry_sharp);
			if (!data.entries.isEmpty())
				HazardSystem.register(hazardItem,data);
		}

		compute((object,data)->{
			// do not fucking modify this array, modify data.entries
			List<HazardEntry> ____________ = new ArrayList<>(data.entries);
			List<HazardEntry> entries = data.entries;
			for (HazardEntry entry : ____________) {
				if (entry.type instanceof HazardTypeHydroactive)
					entries.remove(entry);
			}
		});
	}
	public static @Nullable HazardEntry generateHazEntry(IHazardType template,double value,@Nullable Map<Class<? extends IHazardType>,IHazardModifier> mods) {
		if (value == 0) return null;
		HazardEntry entry = new HazardEntry(template,value);
		if (mods != null) {
			if (mods.containsKey(template.getClass()))
				entry.addMod(mods.get(template.getClass()));
		}
		return entry;
	}
	public static void registerHazard(DictFrame frame,HazardEntry... entries) {
		registerHazard(frame,(s)->true,entries);
	}
	public static void registerHazardRegular(DictFrame frame,HazardEntry... entries) {
		registerHazard(
				frame,
				(s)->{
					if (s.startsWith("ingot")) return true;
					if (s.startsWith("dust")) return true;
					if (s.startsWith("block")) return true;
					if (s.startsWith("plate")) return true;
					if (s.startsWith("bedrock")) return true;
					if (s.startsWith("billet")) return true;
					if (s.startsWith("crystal")) return true;
					return false;
				},
				entries
		);
	}
	public static void registerHazard(DictFrame frame,Function<String,Boolean> processor,HazardEntry... entries) {
		Map<String,Float> map = AddonOreDictHazards.dictMap.get(frame);
		if (map == null) {
			System.out.println("\uD83C\uDF3FCAUTION: dictMap for "+frame.ingot()+" could not be captured");
			return;
		}
		for (Entry<String,Float> entry : map.entrySet()) {
			if (processor.apply(entry.getKey())) {
				HazardData data = HazardSystem.oreMap.computeIfAbsent(entry.getKey(),k->new HazardData());
				for (HazardEntry hazard : entries) {
					double level = hazard.baseLevel;
					if (hazard.type instanceof HazardTypeRadiation || hazard.type instanceof LCERad || hazard.type instanceof HazardTypeDigamma || hazard.type instanceof HazardTypeCoal || hazard.type instanceof HazardTypeAsbestos)
						level *= entry.getValue();
					if (hazard.type instanceof HazardTypeToxic)
						level = level+(level*entry.getValue()-level)*0.25;
					HazardEntry haz = new HazardEntry(hazard.type,level);
					data.addEntry(haz);
				}
			}
		}
	}
	public static void computeOreMap(Map<String,HazardData> map,BiConsumer<Object,HazardData> processor) {
		for (Entry<String,HazardData> entry : map.entrySet())
			processor.accept(entry.getKey(),entry.getValue());
	}
	public static void computeItemMap(Map<Item,HazardData> map,BiConsumer<Object,HazardData> processor) {
		for (Entry<Item,HazardData> entry : map.entrySet())
			processor.accept(entry.getKey(),entry.getValue());
	}
	public static void computeStackMap(Map<RecipesCommon.ComparableStack,HazardData> map,BiConsumer<Object,HazardData> processor) {
		for (Entry<RecipesCommon.ComparableStack,HazardData> entry : map.entrySet())
			processor.accept(entry.getKey(),entry.getValue());
	}
	public static void compute(BiConsumer<Object,HazardData> processor) {
		computeOreMap(HazardSystem.oreMap,processor);
		computeItemMap(HazardSystem.itemMap,processor);
		computeStackMap(HazardSystem.stackMap,processor);
	}
	// why'd you had to make these private
	public static HazardData makeData(IHazardType hazard) { return (new HazardData()).addEntry(hazard); }
	public static HazardData makeData(IHazardType hazard, float level) { return (new HazardData()).addEntry(hazard, (double)level); }
	public static HazardData makeData(IHazardType hazard, float level, boolean override) { return (new HazardData()).addEntry(hazard, (double)level, override); }
}
