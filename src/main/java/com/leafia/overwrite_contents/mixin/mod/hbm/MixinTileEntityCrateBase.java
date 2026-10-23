// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.tileentity.machine.TileEntityLockableBase;
import com.hbm.tileentity.machine.storage.TileEntityCrateBase;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCrateBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityCrateBase.class)
public class MixinTileEntityCrateBase extends TileEntityLockableBase implements IMixinTileEntityCrateBase {
	@Unique boolean leafia$isOnRack = false;
	@Override
	public void leafia$setOnRack() {
		leafia$isOnRack = true;
	}
	@Inject(method = "isUseableByPlayer",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	void leafia$onIsUseableByPlayer(EntityPlayer player,CallbackInfoReturnable<Boolean> cir) {
		if (leafia$isOnRack) {
			cir.setReturnValue(!isInvalid());
			cir.cancel();
		}
	}
}
