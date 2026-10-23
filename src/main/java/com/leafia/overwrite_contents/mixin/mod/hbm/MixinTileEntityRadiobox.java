// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.blocks.machine.Radiobox;
import com.hbm.tileentity.machine.TileEntityRadiobox;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityRadiobox.class)
public class MixinTileEntityRadiobox extends TileEntity {
	@Inject(method = "update",at = @At(value = "HEAD"),require = 1,cancellable = true)
	void leafia$onUpdate(CallbackInfo ci) {
		if (!(world.getBlockState(pos).getBlock() instanceof Radiobox))
			ci.cancel();
	}
}
