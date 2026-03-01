package com.leafia.init.recipes;

import com.hbm.inventory.recipes.DFCRecipes;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static com.hbm.inventory.OreDictManager.*;
import static com.hbm.inventory.OreDictManager.MAGTUNG;
import static com.hbm.inventory.OreDictManager.PU;
import static com.hbm.inventory.OreDictManager.SBD;
import static com.hbm.inventory.OreDictManager.U;
import static com.hbm.inventory.OreDictManager.U238;

public class AddonDFCRecipes {
	public static void register() {
		DFCRecipes.setRecipe(10000L, ModItems.marshmallow, new ItemStack(ModItems.marshmallow_roasted));

		DFCRecipes.setRecipe(200000L, REDSTONE.dust(), new ItemStack(ModItems.ingot_mercury));
		DFCRecipes.setRecipe(200000L, REDSTONE.block(), new ItemStack(ModItems.bottle_mercury));

		DFCRecipes.setRecipe(1000000L, W.dust(), new ItemStack(ModItems.powder_magnetized_tungsten));
		DFCRecipes.setRecipe(1000000L, W.ingot(), new ItemStack(ModItems.ingot_magnetized_tungsten));

		DFCRecipes.setRecipe(6000000L, MAGTUNG.dust(), new ItemStack(ModItems.powder_chlorophyte));
		DFCRecipes.setRecipe(6000000L, MAGTUNG.ingot(), new ItemStack(ModItems.powder_chlorophyte));

		DFCRecipes.setRecipe(20000000L, ModItems.powder_chlorophyte, new ItemStack(ModItems.powder_balefire));

		DFCRecipes.setRecipe(60000000L, ModItems.powder_balefire, new ItemStack(ModItems.egg_balefire_shard));

		DFCRecipes.setRecipe(80000000L, ModItems.billet_thorium_fuel, new ItemStack(ModItems.billet_zfb_bismuth));

		DFCRecipes.setRecipe(120000000L, Items.STICK, new ItemStack(Blocks.LOG));
		DFCRecipes.setRecipe(120000000L, Blocks.STONE, new ItemStack(Blocks.IRON_ORE));
		DFCRecipes.setRecipe(120000000L, Blocks.GRAVEL, new ItemStack(Blocks.COAL_ORE));
		DFCRecipes.setRecipe(120000000L, Blocks.NETHERRACK, new ItemStack(Blocks.QUARTZ_ORE));

		DFCRecipes.setRecipe(150000000L, ModItems.nugget_unobtainium_lesser, new ItemStack(ModItems.nugget_unobtainium_greater));

		DFCRecipes.setRecipe(200000000L, U.nugget(), new ItemStack(ModItems.nugget_schrabidium));
		DFCRecipes.setRecipe(200000000L, U.ingot(), new ItemStack(ModItems.ingot_schrabidium));
		DFCRecipes.setRecipe(200000000L, U.dust(), new ItemStack(ModItems.powder_schrabidium));

		DFCRecipes.setRecipe(250000000L, ModItems.powder_nitan_mix, new ItemStack(ModItems.powder_spark_mix));
		DFCRecipes.setRecipe(500000000L, ModItems.particle_hydrogen, new ItemStack(ModItems.particle_amat));

		DFCRecipes.setRecipe(2000000000L, PU.nugget(), new ItemStack(ModItems.nugget_euphemium));
		DFCRecipes.setRecipe(2000000000L, PU.ingot(), new ItemStack(ModItems.ingot_euphemium));
		DFCRecipes.setRecipe(2000000000L, PU.dust(), new ItemStack(ModItems.powder_euphemium));

		DFCRecipes.setRecipe(3000000000L, ModItems.particle_amat, new ItemStack(ModItems.particle_aschrab));

		DFCRecipes.setRecipe(20000000000L,AddonBlocks.block_welded_osmiridium, new ItemStack(AddonBlocks.block_xanaxium));

		DFCRecipes.setRecipe(20000000000L, SBD.dust(), new ItemStack(ModItems.powder_dineutronium));
		DFCRecipes.setRecipe(20000000000L, SBD.ingot(), new ItemStack(ModItems.ingot_dineutronium));

		DFCRecipes.setRecipe(40000000000L, U238.ingot(), new ItemStack(ModItems.ingot_u238m2));
		DFCRecipes.setRecipe(4200000000L, U238.nugget(), new ItemStack(ModItems.nugget_u238m2));

		DFCRecipes.setRecipe(80000000000L, ModItems.undefined, new ItemStack(ModItems.glitch));
	}
}
