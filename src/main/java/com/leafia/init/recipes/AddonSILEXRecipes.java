package com.leafia.init.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.recipes.SILEXRecipes;
import com.hbm.inventory.recipes.SILEXRecipes.SILEXRecipe;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemFELCrystal.EnumWavelengths;
import com.hbm.util.WeightedRandomObject;
import com.leafia.contents.AddonItems.Resources;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.LinkedHashMap;

public class AddonSILEXRecipes {
	public static LinkedHashMap<Object, SILEXRecipe> recipes = SILEXRecipes.recipes;
	public static void register() {
		recipes.put(new ComparableStack(ModBlocks.mush_block, 1), new SILEXRecipe(100, 640, EnumWavelengths.DRX)
				.addOut(new WeightedRandomObject(new ItemStack(ModItems.biomass_compressed), 110))
				.addOut(new WeightedRandomObject(new ItemStack(ModBlocks.mush), 80))
				.addOut(new WeightedRandomObject(new ItemStack(ModItems.powder_poison), 6+3))
				//.addOut(new WeightedRandomObject(new ItemStack(ModItems.powder_radspice), 3))
				.addOut(new WeightedRandomObject(new ItemStack(ModItems.egg_balefire_shard), 1))
		);
		recipes.put(new ComparableStack(ModBlocks.ancient_scrap, 1), new SILEXRecipe(1000, 640, EnumWavelengths.DRX)
				.addOut(new WeightedRandomObject(new ItemStack(ModBlocks.block_electrical_scrap), /*720*/580)) // tf is this man
				.addOut(new WeightedRandomObject(new ItemStack(ModItems.undefined), 20))
				.addOut(new WeightedRandomObject(new ItemStack(ModItems.chlorine_pinwheel), /*2*/200)) // the only common item
				.addOut(new WeightedRandomObject(new ItemStack(ModItems.ingot_electronium), 1))
		);
		recipes.put(new ComparableStack(ModItems.egg_balefire, 1), new SILEXRecipe(1000, 1000, EnumWavelengths.DRX)
				.addOut(new ItemStack(Resources.powder_nc279), 10)
				.addOut(new ItemStack(Resources.powder_taintium), 10)
				.addOut(new ItemStack(ModItems.powder_balefire), 80)
		);
	}
}
