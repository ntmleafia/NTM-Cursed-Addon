package com.leafia.contents;

import com.hbm.blocks.ICustomBlockItem;
import com.hbm.blocks.generic.BlockModDoor;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.ItemGunBaseNT;
import com.hbm.main.MainRegistry;
import com.hbm.util.I18nUtil;
import com.leafia.AddonBase;
import com.leafia.AddonBase.AddonLoadingStage;
import com.leafia.contents.bomb.missile.customnuke.CustomNukeMissileItem;
import com.leafia.contents.building.generic.lined_asphalt.LinedAsphaltBlock;
import com.leafia.contents.building.pinkdoor.ItemPinkDoor;
import com.leafia.contents.building.sign.SignBlock;
import com.leafia.contents.control.fuel.AddonRTGPelletItem;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.gear.advisor.AdvisorItem;
import com.leafia.contents.gear.guns.GunInit;
import com.leafia.contents.gear.ntmfbottle.ItemNTMFBottle;
import com.leafia.contents.gear.utility.fuzzy.FuzzyIdentifierItem;
import com.leafia.contents.gear.utility.radglasses.RadGlassesItem;
import com.leafia.contents.gear.wands.ItemWandLoading;
import com.leafia.contents.gear.wands.ItemWandSaving;
import com.leafia.contents.gear.wands.ItemWandV;
import com.leafia.contents.machines.elevators.car.chips.EvChipItem;
import com.leafia.contents.machines.elevators.car.styles.EvStyleItem;
import com.leafia.contents.machines.elevators.car.styles.EvStyleItem.StyleType;
import com.leafia.contents.machines.elevators.items.EvSpawnItem;
import com.leafia.contents.machines.elevators.items.WeightSpawnItem;
import com.leafia.contents.machines.elevators.items.radio.EvRadioItem;
import com.leafia.contents.machines.powercores.dfc.CrucifixItem;
import com.leafia.contents.machines.powercores.dfc.LCEItemLens;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisEntity.DebrisType;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisItem;
import com.leafia.contents.parts.depleteds.AddonDepletedFuelItem;
import com.leafia.dev.blocks.ICustomItemBlockProvider;
import com.leafia.dev.blocks.blockbase.meta.IMetaPlacable;
import com.leafia.dev.blocks.blockbase.meta.MetaPlacableItemBlock;
import com.leafia.dev.items.itembase.AddonItemHazardBaked;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.init.hazards.ItemRads;
import com.leafia.dev.items.itembase.AddonItemBaked;
import com.leafia.settings.AddonConfig;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem.ItemType.BILLET;
import static com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem.ItemType.DEPLETED;
import static com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem.Purity.*;

public class AddonItems {
	static {
		if (AddonBase.staticLoadingStage != AddonLoadingStage.ITEMS)
			throw new LeafiaDevFlaw("AddonItems loaded before it should!");
	}
	public static final List<Item> ALL_ITEMS = new ArrayList<Item>();
	public static final Item door_fuckoff = new ItemPinkDoor("door_fuckoff").setCreativeTab(null);

	public static final Item fix_tool = new CrucifixItem("fix_tool","leafia/crucifix").setMaxStackSize(1).setCreativeTab(MainRegistry.controlTab);
	public static final Item fix_survival = new CrucifixItem("fix_survival","leafia/fix_survival").setMaxStackSize(1).setCreativeTab(MainRegistry.controlTab);

	public static final Item dfcsh_cable = new AddonItemBaked("dfcsh_cable","leafia/absorber_shrapnels/framecable").setCreativeTab(MainRegistry.controlTab);
	public static final Item dfcsh_core = new AddonItemBaked("dfcsh_core","leafia/absorber_shrapnels/core").setCreativeTab(MainRegistry.controlTab);
	public static final Item dfcsh_corner = new AddonItemBaked("dfcsh_corner","leafia/absorber_shrapnels/framecorner").setCreativeTab(MainRegistry.controlTab);
	public static final Item dfcsh_front = new AddonItemBaked("dfcsh_front","leafia/absorber_shrapnels/framefront").setCreativeTab(MainRegistry.controlTab);
	public static final Item dfcsh_beam = new AddonItemBaked("dfcsh_beam","leafia/absorber_shrapnels/framebeam").setCreativeTab(MainRegistry.controlTab);

    public static final Item ams_focus_blank = new LCEItemLens(400000L, 0.5F, 1.75F, 1, "ams_focus_blank").setMaxStackSize(1).setCreativeTab(MainRegistry.controlTab);
    public static final Item ams_focus_limiter = new LCEItemLens(2500000000L, 1.5F, 1.75F, 0.8F, "ams_focus_limiter").setMaxStackSize(1).setCreativeTab(MainRegistry.controlTab);
    public static final Item ams_focus_booster = new LCEItemLens(100000000L, 0.8F, 0.5F, 1.35F, "ams_focus_booster").setMaxStackSize(1).setCreativeTab(MainRegistry.controlTab);
    public static final Item ams_focus_omega = new LCEItemLens(1000000000L, 5.0F, 10.0F, 3.5F, "ams_focus_omega").setMaxStackSize(1).setCreativeTab(MainRegistry.controlTab);
	public static final Item ams_focus_safe = new LCEItemLens(5000000000L, 8F, 4.5F, 0.1F, "ams_focus_safe").setMaxStackSize(1).setCreativeTab(MainRegistry.controlTab);

    public static final Item wand_v = new ItemWandV("wand_v","wands/wand_v").setMaxStackSize(1).setCreativeTab(MainRegistry.consumableTab);
	public static final ItemWandSaving wand_leaf = (ItemWandSaving)new ItemWandSaving("wand_leaf","wands/wand_leaf").setMaxStackSize(1).setCreativeTab(MainRegistry.consumableTab);
	public static final Item wand_l = new ItemWandLoading("wand_l","wands/wand_loading").setMaxStackSize(1).setCreativeTab(MainRegistry.consumableTab);

	public static final Item fuzzy_identifier = new FuzzyIdentifierItem("fuzzy_identifier").setMaxStackSize(1).setCreativeTab(MainRegistry.consumableTab);

	public static class LeafiaRods {
		public static final Item leafRod = new LeafiaRodItem.EmptyLeafiaRod().setCreativeTab(MainRegistry.controlTab);
		/*
				public static final Item dante = new ItemBase("dante").setCreativeTab(CreativeTabs.REDSTONE);
				public static final Item
						danteRod
						= new ItemLeafiaRod("Dante",150000000, 9000)
						.setAppearance(dante, BILLET, ISOTOPE)
						.setCreativeTab(CreativeTabs.REDSTONE);*/
		// rbmk based
		public static final Item
				leafRodU38
				= new LeafiaRodItem("U-238",4000000, 0)
				.setAppearance(ModItems.billet_u238, BILLET, ISOTOPE)
				//.setReactivity(0.3)
				.setDecayProduct("depleteduranium238")
				.addRad(ItemRads.uranium238)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodNU
				= new LeafiaRodItem("NU",4000000, 2865)
				.setAppearance(ModItems.billet_uranium, BILLET, RAW)
				.setReactivity(0.4)
				.setDecayProduct("depletednaturaluranium")
				.addRad(ItemRads.uranium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodMEU235
				= new LeafiaRodItem("MEU-235",4000000, 2865)
				.setAppearance(ModItems.billet_uranium_fuel, BILLET, FUEL)
				.setDecayProduct("depleteduranium")
				.addRad(ItemRads.uraniumFuel)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodHEU233
				= new LeafiaRodItem("HEU-233",1000000, 2865)
				.setAppearance(ModItems.billet_u233, BILLET, ISOTOPE)
				.setReactivity(2)
				.setDecayProduct("depleteduranium233")
				.addRad(ItemRads.uranium233)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodHEU235
				= new LeafiaRodItem("HEU-235",3000000, 2865)
				.setAppearance(ModItems.billet_u235, BILLET, ISOTOPE)
				.setReactivity(1.75)
				.setDecayProduct("depleteduranium235")
				.addRad(ItemRads.uranium235)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodTHMEU
				= new LeafiaRodItem("ThMEU",3500000, 3350)
				.setAppearance(ModItems.billet_thorium_fuel, BILLET, FUEL)
				.setDecayProduct("depletedthorium")
				.addRad(ItemRads.thoriumFuel)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodLEP239
				= new LeafiaRodItem("LEPu-239",5000000, 2744)
				.setAppearance(ModItems.billet_plutonium_fuel, BILLET, FUEL)
				.setReactivity(1/1.5)
				.setDecayProduct("depletedplutonium")
				.addRad(ItemRads.plutoniumFuel)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodMEP239
				= new LeafiaRodItem("MEPu-239",5000000, 2744)
				.setAppearance(ModItems.billet_pu_mix, BILLET, ISOTOPE)
				.setDecayProduct("depletedplutonium")
				.addRad(ItemRads.plutoniumRG)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodHEP239
				= new LeafiaRodItem("HEPu-239",5000000, 2744)
				.setAppearance(ModItems.billet_pu239, BILLET, ISOTOPE)
				.setReactivity(1.25)
				.setDecayProduct("pu240")
				.addRad(ItemRads.plutonium239)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodHEP241
				= new LeafiaRodItem("HEPu-241",18000000, 2744)
				.setAppearance(ModItems.billet_pu241, BILLET, ISOTOPE)
				.setReactivity(1.45)
				.setDecayProduct("depletedplutonium")
				.addRad(ItemRads.plutonium241)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodLEA242
				= new LeafiaRodItem("LEAm-242",18000000, 3986)
				.setAppearance(ModItems.billet_americium_fuel, BILLET, FUEL)
				.addRad(ItemRads.americiumFuel)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodMEA242
				= new LeafiaRodItem("MEAm-242",18000000, 3986)
				.setAppearance(ModItems.billet_am_mix, BILLET, ISOTOPE)
				.addRad(ItemRads.americiumRG)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodHEA241
				= new LeafiaRodItem("HEAm-241",18000000, 3986)
				.setAppearance(ModItems.billet_am241, BILLET, ISOTOPE)
				.preferFast()
				.addRad(ItemRads.americium241)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodHEA242
				= new LeafiaRodItem("HEAm-242",18000000, 3986)
				.setAppearance(ModItems.billet_am242, BILLET, ISOTOPE)
				.addRad(ItemRads.americium242)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodMEN237
				= new LeafiaRodItem("MENp-237",5200000, 2800)
				.setAppearance(ModItems.billet_neptunium_fuel, BILLET, FUEL)
				.preferAny()
				.addRad(ItemRads.neptuniumFuel)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodHEN237
				= new LeafiaRodItem("HENp-237",3200000, 2800)
				.setAppearance(ModItems.billet_neptunium, BILLET, RAW)
				.preferFast()
				.addRad(ItemRads.neptunium237)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodMOX
				= new LeafiaRodItem("MOX",6000000, 2815)
				.setAppearance(ModItems.billet_mox_fuel, BILLET, FUEL)
				.addRad(ItemRads.moxie)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodLES236
				= new LeafiaRodItem("LESa-326",3500000, 2500)
				.setAppearance(ModItems.billet_les, BILLET, FUEL)
				.setDecayProduct("depletedschrabidium")
				.setEmission(0.75)
				.setModerated()
				.addRad(ItemRads.schrabidiumLow)
				.addBlinding()
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodMES326
				= new LeafiaRodItem("MESa-326",3000000, 2750)
				.setAppearance(ModItems.billet_schrabidium_fuel, BILLET, FUEL)
				.setDecayProduct("depletedmsa326")
				.addRad(ItemRads.schrabidiumMedium)
				.addBlinding()
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodHES326
				= new LeafiaRodItem("HESa-326",2500000, 3000)
				.setAppearance(ModItems.billet_hes, BILLET, FUEL)
				.setDecayProduct("depletedhesa326")
				.setEmission(1.25)
				.addRad(ItemRads.schrabidiumHigh)
				.addBlinding()
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodSa326
				= new LeafiaRodItem("Sa-326",2000000, 3250)
				.setAppearance(ModItems.billet_schrabidium, BILLET, FUEL)
				.setDecayProduct("depletedsa326")
				.setEmission(2.25)
				.addRad(ItemRads.schrabidium326)
				.addBlinding()
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodLEAus
				= new LeafiaRodItem("LEAus",5500000, 7029)
				.setDecayProduct("heaus")
				.setEmission(0.3)
				.setAppearance(ModItems.billet_australium_lesser, BILLET, ISOTOPE)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodHEAus
				= new LeafiaRodItem("HEAus",5500000, 5211)
				.setEmission(1.2)
				.setAppearance(ModItems.billet_australium_greater, BILLET, ISOTOPE)
				.setCreativeTab(MainRegistry.controlTab);
		/*public static final Item
				leafRodEmitter
				= new LeafiaRodItem("Emitter",90000, 6000)
				.setAppearance(ModItems.billet_unobtainium, BILLET, SOURCE)
				.setEmission(1000).setReactivity(1/1000d)
				.setRad(ItemRads.unobtanium)
				.setCreativeTab(MainRegistry.controlTab);*/
		public static final Item
				leafRodRa226Be
				= new LeafiaRodItem("Ra-226Be",400000, 700)
				.setAppearance(ModItems.billet_ra226be, BILLET, SOURCE)
				.setModerated()
				.addRad(ItemRads.radium226be)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodPo210Be
				= new LeafiaRodItem("Po-210Be",2000000, 1287)
				.setAppearance(ModItems.billet_po210be, BILLET, SOURCE)
				.setModerated()
				.addRad(ItemRads.polonium210be)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodPu238Be
				= new LeafiaRodItem("Pu-238Be",2000000, 2744)
				.setAppearance(ModItems.billet_pu238be, BILLET, SOURCE)
				.setModerated()
				.addRad(ItemRads.plutonium238be)
				.setCreativeTab(MainRegistry.controlTab);
		/*public static final Item
				leafRodFlashgold
				= new LeafiaRodItem("Flashgold",1800000, 5000)
				.setAppearance(ModItems.billet_balefire_gold, BILLET, RAW)
				.addRad(ItemRads.gold198)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodFlashlead
				= new LeafiaRodItem("Flashlead",2200000, 5050)
				.setAppearance(ModItems.billet_flashlead, BILLET, RAW)
				.addRad(ItemRads.flashlead)
				.setCreativeTab(MainRegistry.controlTab);*/
		/*public static final Item
				leafRodBi209ZFB
				= new LeafiaRodItem("Bi-209 ZFB",180000, 2744)
				.setAppearance(ModItems.billet_zfb_bismuth, BILLET, BREEDER)
				.addRad(ItemRads.bismuth209zfb)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodPu241ZFB
				= new LeafiaRodItem("Pu-241 ZFB",180000, 2865)
				.setAppearance(ModItems.billet_zfb_pu241, BILLET, BREEDER)
				.addRad(ItemRads.plutonium241zfb)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodAm242ZFB
				= new LeafiaRodItem("Am-242 ZFB",180000, 3744)
				.setAppearance(ModItems.billet_zfb_am_mix, BILLET, BREEDER)
				.addRad(ItemRads.americium242zfb)
				.setCreativeTab(MainRegistry.controlTab);*/
		// normal rods-based
		public static final Item
				leafRodPu238
				= new LeafiaRodItem("Pu-238",3500000, 2744)
				.setAppearance(ModItems.billet_pu238, BILLET, ISOTOPE)
				.addRad(ItemRads.plutonium238)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodPo210
				= new LeafiaRodItem("Po-210",3000000, 1287)
				.setAppearance(ModItems.billet_polonium, BILLET, ISOTOPE)
				.addRad(ItemRads.polonium210)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodSa327
				= new LeafiaRodItem("Sa-327",8000000, 2250)
				.setAppearance(ModItems.billet_solinium, BILLET, RAW)
				.setDecayProduct("depletedsa327")
				.setEmission(65).setReactivity(1/45d)
				.addRad(ItemRads.solinium327)
				.addBlinding()
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodLithium
				= new LeafiaRodItem("Lithium",3000, 0)
				.setAppearance(ModItems.lithium, BILLET, RAW)
				.addAlkaline(1)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodPu240
				= new LeafiaRodItem("Pu-240",65000000, 2744)
				.setAppearance(ModItems.billet_pu240, BILLET, ISOTOPE)
				.addRad(ItemRads.plutonium240)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteUranium
				= new LeafiaRodItem("Depleted Uranium",0, 0)
				.setAppearance(ModItems.waste_uranium, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteUranium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteUraniumNatural
				= new LeafiaRodItem("Depleted Natural Uranium",0, 0)
				.setAppearance(ModItems.waste_natural_uranium, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteUranium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteUranium233
				= new LeafiaRodItem("Depleted Uranium-233",0, 0)
				.setAppearance(ModItems.waste_u233, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteUranium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteUranium235
				= new LeafiaRodItem("Depleted Uranium-235",0, 0)
				.setAppearance(ModItems.waste_u235, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteUranium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteUranium238
				= new LeafiaRodItem("Depleted Uranium-238",0, 0)
				.overrideLabel("Bred Uranium-238")
				.setAppearance(DepletedFuels.waste_u238, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteUranium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWastePlutonium
				= new LeafiaRodItem("Depleted Plutonium",0, 0)
				.setAppearance(ModItems.waste_plutonium, 1, DEPLETED, RAW)
				.addRad(ItemRads.wastePlutonium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteThorium
				= new LeafiaRodItem("Depleted Thorium",0, 0)
				.setAppearance(ModItems.waste_thorium, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteThorium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteMOX
				= new LeafiaRodItem("Depleted MOX",0, 0)
				.setAppearance(ModItems.waste_mox, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteMOX)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteSchrabidium
				= new LeafiaRodItem("Depleted Schrabidium",0, 0)
				.setAppearance(ModItems.waste_schrabidium, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteSchrabidium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteMESa326
				= new LeafiaRodItem("Depleted MESa-326",0, 0)
				.setAppearance(DepletedFuels.waste_mes, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteSchrabidium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteHESa326
				= new LeafiaRodItem("Depleted HESa-326",0, 0)
				.setAppearance(DepletedFuels.waste_hes, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteSchrabidium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteSa326
				= new LeafiaRodItem("Depleted Sa-326",0, 0)
				.setAppearance(DepletedFuels.waste_sch, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteSchrabidium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodWasteSa327
				= new LeafiaRodItem("Depleted Sa-327",0, 0)
				.setAppearance(DepletedFuels.waste_sol, 1, DEPLETED, RAW)
				.addRad(ItemRads.wasteSchrabidium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodLead
				= new LeafiaRodItem("Lead",99999999, 0)
				.setAppearance(ModItems.ingot_lead, BILLET, RAW)
				.setDecayProduct("pb209")
				.addToxic(2)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodBF
				= new LeafiaRodItem("Balefire",200, 8652)
				.setSpecialRodModel().setBaseItem(ModItems.egg_balefire)
				.setDecayProduct("blazingbalefire")
				.setEmission(8).setReactivity(1/6d)
				.setCorium(AddonBlocks.fluid_balecorium)
				.addRad(ItemRads.balefire)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodBFblazing
				= new LeafiaRodItem("Blazing Balefire",65000, 8652)
				.setSpecialRodModel()
				.setDecayProduct("balefire")
				.setEmission(12).setReactivity(1/9d)
				.setCorium(AddonBlocks.fluid_balecorium)
				.addRad(ItemRads.balefire.multiply(2))
				.setCreativeTab(MainRegistry.controlTab);
		// new stuff
		public static final Item
				leafRodNPu
				= new LeafiaRodItem("NPu",2600000, 2744)
				.setAppearance(ModItems.billet_plutonium, BILLET, RAW)
				.addRad(ItemRads.plutonium)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodTh232
				= new LeafiaRodItem("Th-232",90000, 0)
				.setDecayProduct("thmeu")
				.setAppearance(ModItems.billet_th232, BILLET, RAW)
				.addRad(ItemRads.thorium232)
				.setCreativeTab(MainRegistry.controlTab);
		/*public static final Item
				leafRodBi209
				= new LeafiaRodItem("Bi-209",0, 0)
				.setAppearance(ModItems.billet_bismuth, BILLET, RAW)
				.setCreativeTab(MainRegistry.controlTab);*/
		public static final Item
				leafRodGold198
				= new LeafiaRodItem("Au-198",900000, 3850)
				.setAppearance(ModItems.billet_au198, BILLET, ISOTOPE)
				.addRad(ItemRads.gold198)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodLead209
				= new LeafiaRodItem("Pb-209",600000, 4300)
				.setAppearance(ModItems.billet_pb209, BILLET, ISOTOPE)
				.addRad(ItemRads.lead209)
				.setCreativeTab(MainRegistry.controlTab);


		public static final Item
				leafRodDebug
				= new LeafiaRodItem("Debug",0, 0)
				.setAppearance(ModItems.wand_d, BILLET, UNSTABLE)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodDyatlov
				= new LeafiaRodItem("Potential Instant Blowout Applicator",95000000, 650000000)
				.setAppearance(ModItems.meltdown_tool, BILLET, UNSTABLE)
				.setCreativeTab(MainRegistry.controlTab);

		public static final Item
				leafRodYharonite
				= new LeafiaRodItem("Yh-XXX",6666666, 3200)
				.setAppearance(ModItems.billet_yharonite, BILLET, FUEL)
				//.setDecayProduct("depleteduranium")
				//.addRad(ItemRads.uraniumFuel)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodDGOmega
				= new LeafiaRodItem("DG-OMEGA",Double.MAX_VALUE, 1916169)
				.setSpecialRodModel().setBaseItem(ModItems.particle_digamma)
				.setReactivity(2)
				.addDigamma(0.333)
				.setCreativeTab(MainRegistry.controlTab);
		public static final Item
				leafRodKys
				= new LeafiaRodItem("Kys-3000",Double.MAX_VALUE, 22) {
					@Override
					public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
						tooltip.add(TextFormatting.ITALIC+"Inspired by Quasar!");
						tooltip.add(TextFormatting.ITALIC+"Corrstud will pay their life for this very rod.");
						super.addInformation(stack,worldIn,tooltip,flagIn);
					}
				}
				.setAppearance(Resources.billet_kys, BILLET, UNSTABLE)
				.setEmission(114)
				.setReactivity(514)
				.setCreativeTab(null);
		public static final Item
				leafRodShitfuckium
				= new LeafiaRodItem("Shitfuckium",Double.MAX_VALUE,3200)
				.setAppearance(ModItems.rbmk_pellet_drx,BILLET,RAW);
		public static final Item
				leafRodCatsmile
				= new LeafiaRodItem("Catsmilium",Double.MAX_VALUE,Double.MAX_VALUE) {
					@Override
					public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
						tooltip.add(TextFormatting.ITALIC+"Image by nootles");
						tooltip.add(TextFormatting.ITALIC+"Generates power with pure happiness");
						tooltip.add(TextFormatting.ITALIC+"Never melts down");
						super.addInformation(stack,worldIn,tooltip,flagIn);
					}
				}
				.setSpecialRodModel()
				.setCreativeTab(null);
		public static final Item
				leafRodConfusion
				= new LeafiaRodItem("Confusium",Double.MAX_VALUE,1300) {
					@Override
					public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
						tooltip.add(TextFormatting.ITALIC+"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
						super.addInformation(stack,worldIn,tooltip,flagIn);
					}
				}
				.setSpecialRodModel()
				.setCreativeTab(null);
		static {
			LeafiaRodItem.confirmDecayProducts();
			AddonConfig.FuelLives.loadFromConfig();
		}
	}

	public static Item ntmfbottle = new ItemNTMFBottle("ntmfbottle");

	public static final Item pwr_piece = new PWRDebrisItem("lwr_piece",DebrisType.CONCRETE);
	public static final Item pwr_shrapnel = new PWRDebrisItem("lwr_shrapnel",DebrisType.SHRAPNEL);
	public static final Item pwr_shard = new PWRDebrisItem("lwr_shard",DebrisType.BLANK).disableCrafting();

	public static class Resources {
		public static final Item ingot_potassium = new AddonItemBaked("ingot_potassium","leafia/ingots/ingot_potassium").setCreativeTab(MainRegistry.partsTab);
		public static final Item ingot_rubidium = new AddonItemBaked("ingot_rubidium","leafia/ingots/ingot_rubidium").setCreativeTab(MainRegistry.partsTab);
		public static final Item ingot_francium = new AddonItemBaked("ingot_francium","leafia/ingots/ingot_francium").setCreativeTab(MainRegistry.partsTab);
		public static final Item nugget_schraranium = new AddonItemHazardBaked("nugget_schraranium","resources/nuggets/schraranium")/*.addRad(ItemRads.schraranium.copy().multiply(0.1))*/.setCreativeTab(MainRegistry.partsTab);
		public static final Item ingot_xanaxium = new AddonItemBaked("ingot_xanaxium","resources/ingots/xanaxium").setCreativeTab(MainRegistry.partsTab);
		public static final Item powder_xanaxium = new AddonItemBaked("powder_xanaxium","resources/powders/xanaxium").setCreativeTab(MainRegistry.partsTab);
		public static final Item billet_kys = new AddonItemHazardBaked("billet_kys","leafia/billets/billet_kys") {
			@Override
			public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
				super.addInformation(stack,worldIn,tooltip,flagIn);
				tooltip.add("Inspired by Quasar!");
				tooltip.add("Corrstud will pay their life for this very billet.");
			}
		}.addCryogenic(5).setCreativeTab(null);
		public static final Item powder_digammitite = new AddonItemHazardBaked("powder_digammitite","resources/powders/digammitite").addDigamma(0.1).setCreativeTab(MainRegistry.partsTab);
		public static final Item powder_digammitite_tiny = new AddonItemHazardBaked("powder_digammitite_tiny","resources/powders/digammitite_tiny").addDigamma(0.01).setCreativeTab(MainRegistry.partsTab);

		// Taint world ores
		public static final Item ingot_nc279 = new AddonItemHazardBaked("ingot_nc279","resources/ingots/nc279").addRad(ItemRads.nuclium279).addFire(4).setCreativeTab(MainRegistry.partsTab);
		public static final Item powder_nc279 = new AddonItemHazardBaked("powder_nc279","resources/powders/nc279").addRad(ItemRads.nuclium279.multiply(3)).addFire(4).setCreativeTab(MainRegistry.partsTab);
		public static final Item billet_nc279 = new AddonItemHazardBaked("billet_nc279","resources/billets/nc279").addRad(ItemRads.nuclium279.multiply(0.5)).addFire(4).setCreativeTab(MainRegistry.partsTab);
		public static final Item ingot_taintium = new AddonItemBaked("ingot_taintium","resources/ingots/taintium").setCreativeTab(MainRegistry.partsTab);
		public static final Item powder_taintium = new AddonItemBaked("powder_taintium","resources/powders/taintium").setCreativeTab(MainRegistry.partsTab);

		// Mystic system resources
		public static final Item ingot_fissium = new AddonItemHazardBaked("ingot_fissium","resources/ingots/fissium").addRad(ItemRads.fissium).setCreativeTab(MainRegistry.partsTab);
		public static final Item billet_fissium = new AddonItemHazardBaked("billet_fissium","resources/billets/fissium").addRad(ItemRads.fissium.multiply(0.5)).setCreativeTab(MainRegistry.partsTab);
		public static final Item ingot_chydalium = new AddonItemBaked("ingot_chydalium","resources/ingots/chydalium").setCreativeTab(MainRegistry.partsTab);
		public static final Item powder_chydalium = new AddonItemBaked("powder_chydalium","resources/powders/chydalium").setCreativeTab(MainRegistry.partsTab);
		public static final Item billet_chydalium = new AddonItemBaked("billet_chydalium","resources/billets/chydalium").setCreativeTab(MainRegistry.partsTab);

		// Mystic system alloys
		public static final Item ingot_fissite = new AddonItemHazardBaked("ingot_fissite","resources/ingots/fissite").addRad(ItemRads.fissite).setCreativeTab(MainRegistry.partsTab);
		public static final Item plate_fissite = new AddonItemHazardBaked("plate_fissite","resources/plates/fissite").addRad(ItemRads.fissite).setCreativeTab(MainRegistry.partsTab);
		public static final Item ingot_mysticite = new AddonItemBaked("ingot_mysticite","resources/ingots/mysticite") {
			@Override
			public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
				super.addInformation(stack,worldIn,tooltip,flagIn);
				tooltip.add("The ultimate alloy.");
				tooltip.add("");
				tooltip.add("You cannot shape it due to it's");
				tooltip.add("extreme durability and heat resistance.");
				tooltip.add("");
				tooltip.add("Congratulations, you just wasted it.");
			}
		}.setCreativeTab(MainRegistry.partsTab);
		public static final Item plate_mysticite = new AddonItemBaked("plate_mysticite","resources/plates/mysticite").setCreativeTab(MainRegistry.partsTab);
		public static final Item ingot_tnalloy = new AddonItemBaked("ingot_tnalloy","resources/ingots/tnalloy").setCreativeTab(MainRegistry.partsTab);
	}

	public static final Item advisor = new AdvisorItem("advisor").setCreativeTab(MainRegistry.consumableTab);

	public static final Item missile_customnuke = new CustomNukeMissileItem("missile_customnuke").setCreativeTab(MainRegistry.missileTab);

	//public static final Item addon_battery_pack = new AddonBatteryPackItem("addon_battery_pack");

	public static final Item component_limiter = new AddonItemBaked("component_limiter").setCreativeTab(MainRegistry.partsTab);
	public static final Item component_emitter = new AddonItemBaked("component_emitter").setCreativeTab(MainRegistry.partsTab);
	public static final Item supercooler = new AddonItemBaked("supercooler").setCreativeTab(MainRegistry.partsTab);

	public static class ElevatorStyles {
		public static final List<EvStyleItem> styleItems = new ArrayList<>();
		public static final Item s6chip = new EvChipItem("ev_chip_s6","leafia/elevators/chip_s6") {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
				tooltip.addAll(Arrays.asList(I18nUtil.resolveKey("item.ev_chip_s6.desc").split("\\$")));
				super.addInformation(stack,worldIn,tooltip,flagIn);
			}
		};
		public static final Item skyliftchip = new EvChipItem("ev_chip_skylift","leafia/elevators/chip_skylift") {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack,@Nullable World worldIn,List<String> tooltip,ITooltipFlag flagIn) {
				tooltip.addAll(Arrays.asList(I18nUtil.resolveKey("item.ev_chip_skylift.desc").split("\\$")));
				super.addInformation(stack,worldIn,tooltip,flagIn);
			}
		};
		public static final Item s6floor = new EvStyleItem("ev_s6floor",StyleType.FLOOR);
		public static final Item s6ceiling = new EvStyleItem("ev_s6ceiling",StyleType.CEILING);
		public static final Item s6door = new EvStyleItem("ev_s6door",StyleType.WALL);
		public static final Item s6wall = new EvStyleItem("ev_s6wall",StyleType.WALL);
		public static final Item s6window = new EvStyleItem("ev_s6window",StyleType.WALL);
		public static final Item skyliftdoor = new EvStyleItem("ev_skyliftdoor",StyleType.WALL);
	}

	public static final Item ev_spawn = new EvSpawnItem("ev_spawn");
	public static final Item weight_spawn = new WeightSpawnItem("weight_spawn");
	public static final Item ev_radio = new EvRadioItem("ev_radio");

	public static class DepletedFuels {
		public static final Item waste_u238 = new AddonDepletedFuelItem("waste_u238","hbm","waste_uranium");
		public static final Item waste_mes = new AddonDepletedFuelItem("waste_mes","hbm","waste_schrabidium");
		public static final Item waste_hes = new AddonDepletedFuelItem("waste_hes","hbm","waste_schrabidium");
		public static final Item waste_sch = new AddonDepletedFuelItem("waste_sch","hbm","waste_schrabidium");
		public static final Item waste_sol = new AddonDepletedFuelItem("waste_sol","hbm","waste_schrabidium");
	}

	public static class DigammaRecord extends ItemRecord {
		private DigammaRecord(String recordName,SoundEvent soundIn) {
			super(recordName,soundIn);
			setRegistryName("record_"+recordName);
			AddonItems.ALL_ITEMS.add(this);
			setCreativeTab(null);
		}
		@Override
		public String getRecordNameLocal() {
			return TextFormatting.OBFUSCATED+"digma balls"+TextFormatting.RESET;
		}
	}
	public static ItemRecord digammaRecord = new DigammaRecord("digamma",LeafiaSoundEvents.digamma_record);

	public static final Item dna_felid = new AddonItemBaked("dna_felid","dna").setCreativeTab(null);
	public static final Item dna_canid = new AddonItemBaked("dna_canid","dna").setCreativeTab(null);
	public static final Item dna_eevee = new AddonItemBaked("dna_eevee","dna").setCreativeTab(null);

	public static final Item particle_dineutron = new AddonItemBaked("particle_dineutron").setCreativeTab(MainRegistry.controlTab);

	public static class Guns {
		public static ItemGunBaseNT am_rifle;
	}

	public static final Item radglasses = new RadGlassesItem(ArmorMaterial.IRON, -1, EntityEquipmentSlot.HEAD, "radglasses").setMaxStackSize(1);

	public static final Item pellet_rtg_fissium = new AddonItemHazardBaked("pellet_rtg_fissium").addRad(ItemRads.fissium.multiply(0.5)).setContainerItem(ModItems.plate_iron).setCreativeTab(MainRegistry.controlTab);
	public static final Item pellet_rtg_nc279 = new AddonRTGPelletItem(120,"pellet_rtg_nc279").setDecays(pellet_rtg_fissium,2000000L,4).setCreativeTab(MainRegistry.controlTab);

	public static final Item am_rifle_cell_fissite = new AddonItemHazardBaked("cells_fissite","resources/parts/fissite_cells").addRad(ItemRads.fissite.multiply(5)).setCreativeTab(MainRegistry.partsTab);
	public static final Item am_rifle_cell_mysticite = new AddonItemBaked("cells_mysticite","resources/parts/mysticite_cells").setCreativeTab(MainRegistry.partsTab);
	public static final Item am_rifle_cell_mysticite_filled = new AddonItemBaked("cells_mysticite_filled","resources/parts/mysticite_cells_filled").setCreativeTab(MainRegistry.partsTab);

	public static final Item particle_taint = new AddonItemBaked("particle_taint").setCreativeTab(MainRegistry.controlTab);
	public static final Item particle_cloud = new AddonItemBaked("particle_cloud").setCreativeTab(MainRegistry.controlTab);
	public static final Item grenade_cloud = new AddonItemBaked("grenade_cloud").setCreativeTab(MainRegistry.controlTab);
	public static final Item grenade_pink_cloud = new AddonItemBaked("grenade_pink_cloud").setCreativeTab(MainRegistry.controlTab);

	private static void modifyItemParams() {
		if (!AddonConfig.disableAddonPWR) {
			ModItems.pwr_fuel.setCreativeTab(null);
			ModItems.pwr_fuel_hot.setCreativeTab(null);
			ModItems.pwr_fuel_depleted.setCreativeTab(null);
		}
	}

	public static void preInit() {
		modifyItemParams();
		AddonBase._initMemberClasses(AddonItems.class);

		GunInit.init();

		for(Item item : ALL_ITEMS){
			ForgeRegistries.ITEMS.register(item);
		}

		for(Block block : AddonBlocks.ALL_BLOCKS){
			/*if(block instanceof IItemHazard){
				ForgeRegistries.ITEMS.register(new ItemBlockHazard(block).setRegistryName(block.getRegistryName()));
			} else if(block == ModBlocks.block_scrap){
				ForgeRegistries.ITEMS.register(new ItemBlockScrap(block).setRegistryName(block.getRegistryName()));
			} else */
			if(block instanceof BlockModDoor) {
			} else if (block instanceof ICustomItemBlockProvider custom) {
				ItemBlock item = custom.provideItem();
				if (item != null)
					ForgeRegistries.ITEMS.register(item.setRegistryName(block.getRegistryName()));
			} else if (block instanceof SignBlock) {
				Item item = new ItemBlock(block) {
					@Override
					public String getItemStackDisplayName(ItemStack stack) {
						return block.getLocalizedName();
					}
				};
				ForgeRegistries.ITEMS.register(item.setRegistryName(block.getRegistryName()));
			} else if (block instanceof LinedAsphaltBlock) {
			} else if (block instanceof ICustomBlockItem) {
				((ICustomBlockItem) block).registerItem();
			} else if (block instanceof IMetaPlacable) {
				ForgeRegistries.ITEMS.register(new MetaPlacableItemBlock(block).setRegistryName(block.getRegistryName()));
			} else {
				ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
			}
		}
	}
}
