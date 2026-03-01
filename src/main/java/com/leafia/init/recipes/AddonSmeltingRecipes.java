package com.leafia.init.recipes;

import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AddonSmeltingRecipes {
	public static void register() {
		GameRegistry.addSmelting(ModItems.powder_osmiridium,new ItemStack(ModItems.ingot_osmiridium),5.0F);
		GameRegistry.addSmelting(AddonItems.powder_xanaxium,new ItemStack(AddonItems.ingot_xanaxium),5.0F);
	}
}
