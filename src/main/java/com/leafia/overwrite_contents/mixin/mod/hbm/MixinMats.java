package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.material.Mats;
import com.leafia.init.AddonMats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.leafia.AddonBase._initClass;

@Mixin(value = Mats.class)
public class MixinMats {
	@Inject(method = "<clinit>",at = @At(value = "TAIL"),require = 1,remap = false)
	private static void leafia$onClinit(CallbackInfo ci) {
		_initClass(AddonMats.class);
	}
}
