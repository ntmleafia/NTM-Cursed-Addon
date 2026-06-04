package com.leafia.init.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockBobble.BobbleType;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.anvil.AnvilRecipes.AnvilConstructionRecipe;
import com.hbm.inventory.recipes.anvil.AnvilRecipes.AnvilOutput;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonBlocks;
import com.leafia.contents.AddonItems;
import net.minecraft.item.ItemStack;

import static com.hbm.inventory.OreDictManager.*;
import static com.hbm.inventory.OreDictManager.STEEL;
import static com.hbm.inventory.recipes.anvil.AnvilRecipes.constructionRecipes;

public class AddonAnvilRecipes {

	/*
	 *      //////  //      //  //  //////  //  //  //  //    //  //////
	 *     //      ////  ////  //    //    //  //  //  ////  //  //
	 *    //////  //  //  //  //    //    //////  //  //  ////  //  //
	 *       //  //      //  //    //    //  //  //  //    //  //  //
	 *  //////  //      //  //    //    //  //  //  //    //  //////
	 */
	public static void registerSmithingRecipes() {
		
	}

	/*
	 *      //////  //////  //    //  //////  //////  ////    //  //  //////  //////  //  //////  //    //
	 *     //      //  //  ////  //  //        //    //  //  //  //  //        //    //  //  //  ////  //
	 *    //      //  //  //  ////  //////    //    ////    //  //  //        //    //  //  //  //  ////
	 *   //      //  //  //    //      //    //    //  //  //  //  //        //    //  //  //  //    //
	 *  //////  //////  //    //  //////    //    //  //  //////  //////    //    //  //////  //    //
	 */
	public static void registerConstructionRecipes() {
		constructionRecipes.add(new AnvilConstructionRecipe(
				new AStack[] {
						new ComparableStack(ModItems.rtg_unit, 5),
						new OreDictStack(getReflector(), 8),
						new OreDictStack(CU.ingot(), 16),
						new OreDictStack(TCALLOY.ingot(), 6),
						new OreDictStack(STEEL.plate(), 8),
				}, new AnvilOutput(new ItemStack(AddonBlocks.heater_rt))).setTier(4)
		);
		constructionRecipes.add(new AnvilConstructionRecipe(
				new AStack[] {
						new OreDictStack(STEEL.plateWelded(), 6),
						new OreDictStack(CU.ingot(), 8),
						new OreDictStack(IRON.plateWelded(), 3),
						new OreDictStack(BI.nugget())
				}, new AnvilOutput(new ItemStack(AddonBlocks.hp_boiler))).setTier(3)
		);
	}
}
