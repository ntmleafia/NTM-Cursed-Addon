// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.render.tileentity.RenderRBMKControlRod;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKControl;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityRBMKBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderRBMKControlRod.class)
public class MixinRenderRBMKControlRod {
	@Inject(method = "render(Lcom/hbm/tileentity/machine/rbmk/TileEntityRBMKControl;DDDFIF)V",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	void leafia$onRender(TileEntityRBMKControl te,double x,double y,double z,float partialTicks,int destroyStage,float alpha,CallbackInfo ci) {
		// do not render if it's jumping and if it's not a jumping renderer (identified by destroyStage being -14)
		if (((IMixinTileEntityRBMKBase)te).leafia$jumpHeight() > 0 && destroyStage > -10)
			ci.cancel();
	}
}
