package com.leafia.init.recipes;

import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.CrucibleRecipe;
import com.hbm.inventory.recipes.CrucibleRecipes;
import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems.Resources;
import com.leafia.init.AddonMats;
import net.minecraft.item.ItemStack;

public class AddonCrucibleRecipes {
	public static CrucibleRecipes rec = CrucibleRecipes.INSTANCE;
	public static void register() {
		int q = MaterialShapes.QUANTUM.q(1);
		int n = MaterialShapes.NUGGET.q(1);
		int i = MaterialShapes.INGOT.q(1);
		rec.register(new CrucibleRecipe("crucible.leafia.tnalloy")
				.setup(9,new ItemStack(Resources.ingot_tnalloy))
				.inputs(
						new MaterialStack(Mats.MAT_STEEL,i*6-q),
						new MaterialStack(AddonMats.MAT_TAINTIUM,q)
				)
				.outputs(new MaterialStack(AddonMats.MAT_TNALLOY,i*6))
		);
		rec.register(new CrucibleRecipe("crucible.leafia.fsalloy")
				.setup(9,new ItemStack(Resources.ingot_fissite))
				.inputs(
						new MaterialStack(AddonMats.MAT_FISSIUM,n*8),
						new MaterialStack(AddonMats.MAT_MANA,n)
				)
				.outputs(new MaterialStack(AddonMats.MAT_FISSITE,i))
		);
	}
}
