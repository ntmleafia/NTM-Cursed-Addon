package com.leafia.overwrite_contents.mixin;

import com.hbm.handler.radiation.RadiationSystemNT;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RadiationSystemNT.class)
public class MixinRadiationSystemNT {
	@Mutable @Shadow(remap = false) @Final static double RAD_MAX;

	@Inject(method = "<clinit>",at = @At("TAIL"),require = 1,remap = false)
	private static void leafia$onClinit(CallbackInfo ci) {
		RAD_MAX = 25000;
	}
}
