package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.hazard.HazardRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HazardRegistry.class)
public class MxiinHazardRegistry {
	@Redirect(method = "<clinit>",at = @At())
	static void onClInit() {

	}
}
