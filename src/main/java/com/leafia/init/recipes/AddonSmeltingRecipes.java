package com.leafia.init.recipes;

import com.hbm.items.ModItems;
import com.leafia.contents.AddonItems.Resources;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AddonSmeltingRecipes {
	public static void register() {
		GameRegistry.addSmelting(ModItems.powder_osmiridium,new ItemStack(ModItems.ingot_osmiridium),5.0F);
		GameRegistry.addSmelting(Resources.powder_xanaxium,new ItemStack(Resources.ingot_xanaxium),5.0F);
		GameRegistry.addSmelting(Resources.powder_nc279,new ItemStack(Resources.ingot_nc279),5.0F);
		GameRegistry.addSmelting(Resources.powder_taintium,new ItemStack(Resources.ingot_taintium),5.0F);
		GameRegistry.addSmelting(Resources.powder_chydalium,new ItemStack(Resources.ingot_chydalium),5.0F);
	}
}
