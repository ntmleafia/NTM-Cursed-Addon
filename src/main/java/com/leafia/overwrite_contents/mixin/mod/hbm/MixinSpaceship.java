// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.world.Spaceship;
import com.leafia.contents.AddonBlocks.PWR;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Spaceship.class)
public class MixinSpaceship {
	@Shadow(remap = false) Block Block12;
	@Inject(method = "<init>",at = @At(value = "TAIL"),require = 1,remap = false)
	void leafia$onInit(CallbackInfo ci) {
		Block12 = PWR.element;
	}
}
