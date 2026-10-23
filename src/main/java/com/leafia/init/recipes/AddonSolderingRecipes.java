// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.init.recipes;

import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.inventory.RecipesCommon.OreDictStack;
import com.hbm.inventory.recipes.SolderingRecipes;
import com.hbm.inventory.recipes.SolderingRecipes.SolderingRecipe;
import com.hbm.items.ItemEnums.EnumCircuitType;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems.ElevatorStyles;
import net.minecraft.item.ItemStack;

import java.util.List;

import static com.hbm.inventory.OreDictManager.*;

public class AddonSolderingRecipes {
	public static List<SolderingRecipe> recipes = SolderingRecipes.recipes;
	public static void register() {
		recipes.add(new SolderingRecipe(
				new ItemStack(ElevatorStyles.s6chip),
				60,
				250,
				new AStack[]{
						new ComparableStack(ModItems.circuit,2,EnumCircuitType.CHIP),
						new ComparableStack(ModItems.circuit,1,EnumCircuitType.CAPACITOR),
						new OreDictStack(REDSTONE.dust())
				},
				new AStack[]{new ComparableStack(ModItems.circuit,4,EnumCircuitType.PCB)},
				new AStack[]{new OreDictStack(PB.wireFine(),4)}
		));
		recipes.add(new SolderingRecipe(
				new ItemStack(ElevatorStyles.skyliftchip),
				60,
				250,
				new AStack[]{
						new ComparableStack(ModItems.circuit,2,EnumCircuitType.CHIP),
						new ComparableStack(ModItems.circuit,1,EnumCircuitType.CAPACITOR),
						new OreDictStack(DIAMOND.dust())
				},
				new AStack[]{new ComparableStack(ModItems.circuit,4,EnumCircuitType.PCB)},
				new AStack[]{new OreDictStack(PB.wireFine(),4)}
		));
	}
}
