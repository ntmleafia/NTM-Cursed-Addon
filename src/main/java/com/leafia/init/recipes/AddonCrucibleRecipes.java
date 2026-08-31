package com.leafia.init.recipes;

import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.CrucibleRecipe;
import com.hbm.inventory.recipes.CrucibleRecipes;
import com.hbm.items.ModItems;
import com.leafia.init.AddonMats;
import net.minecraft.item.ItemStack;

public class AddonCrucibleRecipes {
	public static CrucibleRecipes rec = CrucibleRecipes.INSTANCE;
	public static void register() {
		int n = MaterialShapes.NUGGET.q(1);
		int i = MaterialShapes.INGOT.q(1);
		rec.register(new CrucibleRecipe("crucible.leafia.tnalloy")
				.setup(9,new ItemStack(ModItems.ingot_tcalloy))
				.inputs(
						new MaterialStack(Mats.MAT_STEEL,n*26),
						new MaterialStack(AddonMats.MAT_TAINTIUM,n)
				)
				.outputs(new MaterialStack(AddonMats.MAT_TNALLOY,i*3))
		);
		rec.register(new CrucibleRecipe("crucible.leafia.fsalloy")
				.setup(9,new ItemStack(ModItems.ingot_tcalloy))
				.inputs(
						new MaterialStack(AddonMats.MAT_FISSIUM,n*8),
						new MaterialStack(AddonMats.MAT_MANA,n)
				)
				.outputs(new MaterialStack(AddonMats.MAT_FISSITE,i))
		);
	}
}
