package com.leafia.init.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.OreDictManager.DictFrame;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.material.Mats;
import com.hbm.items.ItemEnums.EnumCircuitType;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemBatteryPack.EnumBatteryPack;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.LetterSigns;
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.AddonItems;
import com.leafia.contents.AddonItems.LeafiaRods;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodCrafting;
import com.leafia.contents.control.fuel.nuclearfuel.LeafiaRodItem;
import com.leafia.contents.machines.reactors.pwr.debris.PWRDebrisCrafting;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.Objects;

import static com.hbm.inventory.OreDictManager.*;
import static com.hbm.inventory.OreDictManager.ZR;
import static com.hbm.main.CraftingManager.*;

public class AddonCraftingRecipes {
	public static void craftingRegister() {
		ForgeRegistry<IRecipe> reg = (ForgeRegistry<IRecipe>)hack.getRegistry();

		addRecipeAuto(new ItemStack(AddonBlocks.spk_cable, 16), " W ", "RRR", " W ", 'W', ModItems.plate_dineutronium, 'R',OreDictManager.MAGTUNG.wireFine());
		addShapelessAuto(new ItemStack(ModBlocks.dfc_receiver, 1), AddonItems.dfcsh_beam, AddonItems.dfcsh_cable, AddonItems.dfcsh_corner, AddonItems.dfcsh_core, OreDictManager.STEEL.heavyBarrel(), AddonItems.dfcsh_front, AddonItems.dfcsh_corner, AddonItems.dfcsh_beam, AddonItems.dfcsh_beam);
		addRecipeAuto(new ItemStack(AddonBlocks.dfc_reinforced, 1), "SDS", "TXL", "SDS", 'S', AddonBlocks.block_welded_osmiridium, 'D', ModItems.plate_dineutronium, 'T', AddonItems.supercooler, 'L', ModBlocks.dfc_receiver, 'X', ModBlocks.block_dineutronium);
		addRecipeAuto(new ItemStack(AddonBlocks.dfc_exchanger, 1), "SCS", "HMP", "SCS", 'S', OSMIRIDIUM.plateWelded(), 'C', ModItems.plate_combine_steel, 'H', ModBlocks.heater_heatex, 'M', ModItems.motor, 'P', ModItems.pipes_steel);

		addRecipeAuto(new ItemStack(AddonItems.fuzzy_identifier, 1), "=  ", "@CS", "@MP", '@', OreDictManager.GOLD.wireFine(), 'P', ANY_PLASTIC.ingot(), '=', DictFrame.fromOne(ModItems.circuit, EnumCircuitType.BASIC), 'M', ModItems.motor_desh, 'C', ModItems.coil_gold, 'S', ModItems.screwdriver_desh);

		addRecipeAuto(new ItemStack(LeafiaRods.leafRod, 4), "O", "I", "O", 'O', ZR.billet(), 'I', ZR.nugget());
		for (LeafiaRodItem rod : LeafiaRodItem.fromResourceMap.values()) {
			if (rod.baseItem != null) {
				addShapelessAuto(new ItemStack(rod,1),LeafiaRods.leafRod,new ItemStack(rod.baseItem,1,rod.baseMeta));
				//addShapelessAuto(new ItemStack(rod.baseItem,1,rod.baseMeta),rod);
			}
		}
		hack.getRegistry().register(new LeafiaRodCrafting().setRegistryName(new ResourceLocation("leafia", "generic_fuel_handler")));
		// A NOTE TO SELF: Ingredient keys are supposed to be chars, not strings.
		// If they're strings, they things its part of the shape and probably shits your bed off.

		// CraftingManager can suck my ass god fucking dammit
		addRecipeAuto(new ItemStack(AddonBlocks.amat_duct,3),"CCC","DDD","CCC",'C',ALLOY.wireFine(),'D',new ItemStack(ModBlocks.fluid_duct_neo,1,0));
		addRecipeAuto(new ItemStack(AddonBlocks.amat_charger)," D ","DED"," D ",'D',AddonBlocks.amat_duct,'E',new ItemStack(ModItems.battery_pack,1,EnumBatteryPack.CAPACITOR_NIOBIUM.ordinal()));

		for (int meta = 0; meta < 3; meta++) {
			addRecipeAuto(new ItemStack(AddonBlocks.ff_duct,3,meta),"DDD",'D',new ItemStack(ModBlocks.fluid_duct_neo,1,meta));
			addShapelessAuto(new ItemStack(ModBlocks.fluid_duct_neo,1,meta),new ItemStack(AddonBlocks.ff_duct,1,meta));
		}
		addShapelessAuto(new ItemStack(AddonBlocks.ff_pump),new ItemStack(AddonBlocks.ff_duct,1,2),new ItemStack(ModItems.motor));
		addShapelessAuto(new ItemStack(AddonBlocks.ff_converter),new ItemStack(AddonBlocks.ff_duct,1,2),AL.plate());

		removeRecipesForItem(reg,ModItems.ams_lens);

		addShapelessAuto(new ItemStack(PWR.element_old_blank),new ItemStack(PWR.element_old));
		addShapelessAuto(new ItemStack(PWR.element_old),new ItemStack(PWR.element_old_blank));
		addShapelessAuto(new ItemStack(PWR.hatch_alt),new ItemStack(PWR.hatch));
		addShapelessAuto(new ItemStack(PWR.hatch),new ItemStack(PWR.hatch_alt));

		addRecipeAuto(new ItemStack(AddonItems.ams_focus_blank),"PTP","GSG","PJP",'P',ModItems.plate_dineutronium,'T',ModItems.rune_thurisaz,'G',ModBlocks.reinforced_glass,'S',AL.shell(),'J',ModItems.rune_jera);
		addRecipeAuto(new ItemStack(ModItems.ams_lens, 1), "PFP", "GEG", "PFP", 'P', ModItems.rune_dagaz, 'G', AddonItems.ams_focus_blank, 'E', ModItems.upgrade_overdrive_3, 'F', new ItemStack(ModItems.plate_welded,1,Mats.MAT_TUNGSTEN.id));
		addRecipeAuto(new ItemStack(AddonItems.ams_focus_omega, 1), "PFP", "REG", "PFP", 'P', ModBlocks.dfc_stabilizer, 'R', AddonItems.ams_focus_limiter, 'G', AddonItems.ams_focus_booster, 'E', ModItems.laser_crystal_digamma, 'F', ModBlocks.block_euphemium_cluster);
		addRecipeAuto(new ItemStack(AddonItems.ams_focus_booster, 1), "PFP", "GEG", "PFP", 'P', ModItems.rune_hagalaz, 'G', ModItems.ams_lens, 'E', ModItems.upgrade_screm, 'F', ModItems.plate_desh);
		addRecipeAuto(new ItemStack(AddonItems.ams_focus_limiter, 1), "PFP", "GEG", "PFP", 'P', ModItems.rune_isa, 'G', AddonItems.ams_focus_blank, 'E', ModItems.upgrade_power_3, 'F', ModItems.inf_water_mk2);
		addRecipeAuto(new ItemStack(AddonItems.ams_focus_safe, 1), "PFP", "GEG", "PFP", 'P', ModItems.rune_isa, 'G', AddonItems.ams_focus_limiter, 'E', ModItems.upgrade_effect_3, 'F', AddonItems.supercooler);
		addRecipeAuto(new ItemStack(ModItems.ams_catalyst_blank, 1), "TET", "ETE", "TET", 'T', TS.dust(), 'E', EUPH.ingot());

		addRecipeAuto(new ItemStack(AddonBlocks.ff_duct_solid_shielded, 8), "SAS", "ADA", "SAS", 'S', ModBlocks.brick_compound, 'A', AddonBlocks.ff_duct, 'D', ModItems.ducttape);

		addShapelessAuto(new ItemStack(AddonBlocks.fluid_duct_valve_mdl),new ItemStack(ModBlocks.fluid_duct_neo,1,2),AL.plate());
		addShapelessAuto(new ItemStack(AddonBlocks.fluid_duct_valve_mdl_rs),new ItemStack(ModBlocks.fluid_duct_neo,1,2),new ItemStack(ModItems.motor));

		addShapelessAuto(new ItemStack(AddonBlocks.block_welded_osmiridium),new ItemStack(ModItems.plate_welded,1,Mats.MAT_OSMIRIDIUM.id),new ItemStack(ModItems.plate_welded,1,Mats.MAT_OSMIRIDIUM.id),new ItemStack(ModItems.plate_welded,1,Mats.MAT_OSMIRIDIUM.id),new ItemStack(ModItems.plate_welded,1,Mats.MAT_OSMIRIDIUM.id),new ItemStack(ModItems.plate_welded,1,Mats.MAT_OSMIRIDIUM.id),new ItemStack(ModItems.plate_welded,1,Mats.MAT_OSMIRIDIUM.id),new ItemStack(ModItems.plate_welded,1,Mats.MAT_OSMIRIDIUM.id),new ItemStack(ModItems.plate_welded,1,Mats.MAT_OSMIRIDIUM.id),new ItemStack(ModItems.plate_welded,1,Mats.MAT_OSMIRIDIUM.id));
		addShapelessAuto(new ItemStack(ModItems.plate_welded,9,Mats.MAT_OSMIRIDIUM.id),new ItemStack(AddonBlocks.block_welded_osmiridium));

		// make laser detonator cheap af
		removeRecipesForItem(reg,ModItems.detonator_laser);
		addShapelessAuto(new ItemStack(ModItems.detonator_laser, 1), ModItems.rangefinder, ANY_RUBBER.ingot(), GOLD.wireFine(), new ItemStack(ModItems.circuit, 1, EnumCircuitType.BASIC.ordinal()), new ItemStack(ModItems.circuit, 1, EnumCircuitType.BASIC.ordinal()) );
		removeRecipesForItem(reg,ModItems.detonator_multi);
		addShapelessAuto(new ItemStack(ModItems.detonator_multi, 1), ModItems.detonator, new ItemStack(ModItems.circuit, 1, EnumCircuitType.BASIC.ordinal()),new ItemStack(ModItems.circuit, 1, EnumCircuitType.BASIC.ordinal()) );

		if (AddonBlocks.oc_cable != null) {
			addRecipeAuto(new ItemStack(AddonBlocks.oc_cable, 4), "ICI", "CRC", "ICI", 'I', ModItems.plate_polymer, 'R', REDSTONE.dust(), 'C', ForgeRegistries.ITEMS.getValue(new ResourceLocation("opencomputers", "cable")));
			addShapelessAuto(new ItemStack(AddonBlocks.oc_cable_rad),AddonBlocks.oc_cable,ModBlocks.brick_compound);
		}
		if (AddonBlocks.audio_cable != null) {
			addRecipeAuto(new ItemStack(AddonBlocks.audio_cable, 4), "SCS", "CIC", "SCS", 'S', ModBlocks.concrete_smooth, 'I', IRON.ingot(), 'C', ForgeRegistries.ITEMS.getValue(new ResourceLocation("computronics", "audio_cable")));
			addShapelessAuto(new ItemStack(AddonBlocks.audio_cable_rad),AddonBlocks.audio_cable,ModBlocks.brick_compound);
		}
		for (Block sign : LetterSigns.signs.values())
			addShapelessAuto(new ItemStack(ModItems.plate_iron),new ItemStack(sign));

		addRecipeAuto(new ItemStack(AddonBlocks.broof),"CCC","P P",'C',new ItemStack(Blocks.CARPET,1,EnumDyeColor.GREEN.getDyeDamage()),'P',ModBlocks.steel_beam);

		addRecipeAuto(new ItemStack(AddonItems.fix_survival, 1), " O ", "OTO", " O ", 'O', ModItems.ingot_osmiridium, 'T', ModItems.crystal_trixite);

		hack.getRegistry().register(new PWRDebrisCrafting().setRegistryName(new ResourceLocation("leafia", "lwr_debris_crafting_handler")));
	}
	static void removeRecipesForItem(ForgeRegistry<IRecipe> reg,Item item) {
		ResourceLocation loc = new ResourceLocation("hbm", Objects.requireNonNull(item.getRegistryName()).getPath());
		int i = 0;
		ResourceLocation r_loc = loc;
		while(net.minecraft.item.crafting.CraftingManager.REGISTRY.containsKey(r_loc)) {
			i++;
			reg.remove(r_loc);
			r_loc = new ResourceLocation("hbm", loc.getPath() + "_" + i);
		}
	}
}
