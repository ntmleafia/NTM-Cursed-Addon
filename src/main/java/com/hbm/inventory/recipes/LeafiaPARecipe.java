package com.hbm.inventory.recipes;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.recipes.ParticleAcceleratorRecipes.ParticleAcceleratorRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// not again, dumbass
public class LeafiaPARecipe extends ParticleAcceleratorRecipe {
	public LeafiaPARecipe(@NotNull RecipesCommon.AStack in1,@NotNull RecipesCommon.AStack in2,int momentum,@NotNull ItemStack out1,@Nullable ItemStack out2) {
		super(in1,in2,momentum,out1,out2);
	}
}
