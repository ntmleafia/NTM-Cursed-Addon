// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.WeightedRandomChestContentFrom1710;
import com.leafia.contents.AddonBlocks.PWR;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WeightedRandomChestContentFrom1710.class)
public class MixinWeightedRandomChestContentFrom1710 {
	@Shadow(remap = false) public ItemStack theItemId;
	@Inject(method = "<init>(Lnet/minecraft/item/ItemStack;III)V",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onInit(ItemStack stack,int minChance,int maxChance,int weight,CallbackInfo ci) {
		if (stack.getItem() instanceof ItemBlock ib) {
			if (ib.getBlock() == ModBlocks.pwr_fuelrod)
				theItemId = new ItemStack(PWR.element,stack.getCount(),0);
		}
	}
	@Inject(method = "<init>(Lnet/minecraft/item/Item;IIII)V",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onInit(Item item,int meta,int minChance,int maxChance,int weight,CallbackInfo ci) {
		if (item instanceof ItemBlock ib) {
			if (ib.getBlock() == ModBlocks.pwr_fuelrod)
				theItemId = new ItemStack(PWR.element,1,0);
		}
	}
}
