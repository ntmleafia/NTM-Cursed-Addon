package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.OreDictManager;
import com.leafia.init.AddonOreDict;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OreDictManager.class,remap = false)
public class MixinOreDictManager {
	@Inject(method = "registerOres",at = @At(value = "HEAD"),require = 1)
	private static void onRegisterOres(CallbackInfo ci) {
		AddonOreDict.registerOres();
	}
}
