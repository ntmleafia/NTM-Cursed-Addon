// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.controls.ControlType;
import com.leafia.AddonBase;
import com.leafia.contents.machines.controlpanel.instruments.AddonControlType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ControlType.class)
public class MixinControlType {
	@Inject(method = "<clinit>",at = @At(value = "TAIL"),require = 1,remap = false)
	private static void leafia$onClinit(CallbackInfo ci) {
		AddonBase._initClass(AddonControlType.class);
	}
}
