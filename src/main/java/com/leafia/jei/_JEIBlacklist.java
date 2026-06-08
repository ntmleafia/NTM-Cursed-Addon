package com.leafia.jei;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemPWRFuel.EnumPWRFuel;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonBlocks.Elevators;
import com.leafia.contents.AddonBlocks.PWR;
import com.leafia.contents.AddonItems;
import com.leafia.contents.AddonItems.LeafiaRods;
import com.leafia.settings.AddonConfig;
import com.leafia.unsorted.ateupd.Reserved6TE;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class _JEIBlacklist {
	public static void blacklistRecipes(IIngredientBlacklist blacklist) {
		for (Block block : Reserved6TE.ateupd)
			blacklist.addIngredientToBlacklist(new ItemStack(block));
		if (!AddonConfig.disableAddonPWR) {
			for (EnumPWRFuel value : EnumPWRFuel.values()) {
				int i = value.ordinal();
				blacklist.addIngredientToBlacklist(new ItemStack(ModItems.pwr_fuel,1,i));
				blacklist.addIngredientToBlacklist(new ItemStack(ModItems.pwr_fuel_hot,1,i));
				blacklist.addIngredientToBlacklist(new ItemStack(ModItems.pwr_fuel_depleted,1,i));
			}
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_block));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_casing));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_channel));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_control));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_fuelrod));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_controller));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_port));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_reflector));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_neutron_source));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_heatex));
			blacklist.addIngredientToBlacklist(new ItemStack(ModBlocks.pwr_heatsink));
		} else {
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.occs_in));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.occs_out));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.port));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.hatch));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.hatch_alt));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.terminal));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.computer));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.element));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.element_old));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.element_old_blank));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.channel));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.conductor));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.exchanger));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.control));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.reactor_control));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.hull));
			blacklist.addIngredientToBlacklist(new ItemStack(PWR.reflector));
		}
		blacklist.addIngredientToBlacklist(new ItemStack(AddonItems.billet_kys));
		blacklist.addIngredientToBlacklist(new ItemStack(LeafiaRods.leafRodKys));
		blacklist.addIngredientToBlacklist(new ItemStack(Elevators.light));
		blacklist.addIngredientToBlacklist(new ItemStack(AddonItems.digammaRecord));
		blacklist.addIngredientToBlacklist(new ItemStack(AddonItems.dna_felid));
		blacklist.addIngredientToBlacklist(new ItemStack(AddonItems.dna_canid));
		blacklist.addIngredientToBlacklist(new ItemStack(AddonItems.dna_eevee));
		blacklist.addIngredientToBlacklist(new ItemStack(LeafiaRods.leafRodCatsmile));
		blacklist.addIngredientToBlacklist(new ItemStack(LeafiaRods.leafRodConfusion));
		blacklist.addIngredientToBlacklist(new ItemStack(LeafiaRods.leafRodShitfuckium));
	}
}
