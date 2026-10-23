// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.turret.TileEntityTurretBaseNT;
import com.hbm.tileentity.turret.TileEntityTurretChekhov;
import com.hbm.tileentity.turret.TileEntityTurretJeremy;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityTurretBaseNT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityTurretJeremy.class)
public abstract class MixinTileEntityTurretJeremy extends TileEntityTurretBaseNT {
	@Inject(method = "updateFiringTick",at = @At(value = "INVOKE",target = "Lcom/hbm/tileentity/turret/TileEntityTurretJeremy;getFirstConfigLoaded()Lcom/hbm/items/weapon/sedna/BulletConfig;",shift = Shift.AFTER),remap = false,require = 1,cancellable = true)
	void leafia$onUpdateFiringTick(CallbackInfo ci) {
		if (target != null) {
			if (((IMixinTileEntityTurretBaseNT)this).leafia$detectFriendlyFire(target))
				ci.cancel();
		}
	}
}
