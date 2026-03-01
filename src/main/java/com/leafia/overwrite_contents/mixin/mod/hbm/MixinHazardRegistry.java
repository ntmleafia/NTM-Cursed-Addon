package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.hazard.HazardRegistry;
import com.hbm.items.ModItems;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HazardRegistry.class)
public abstract class MixinHazardRegistry {
	@Shadow(remap = false)
	private static void addContaminatingDrop(Item item,double contaminating) {
	}

	@Redirect(method = "registerContaminatingDrops",at = @At(value = "INVOKE", target = "Lcom/hbm/hazard/HazardRegistry;addContaminatingDrop(Lnet/minecraft/item/Item;D)V"),require = 1,remap = false)
	private static void leafia$onRegisterContaminatingDrops(Item item,double contaminating) {
		if (item == ModItems.powder_balefire)
			return;
		addContaminatingDrop(item,contaminating);
	}
}
