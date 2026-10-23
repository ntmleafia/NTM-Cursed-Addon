// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.main.ModEventHandlerClient;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModEventHandlerClient.class)
public class MixinModEventHandlerClient {
	@Inject(method = "onOpenGUI",at = @At(value = "HEAD"),require = 1,remap = false,cancellable = true)
	void leafia$onOpenGUI(GuiOpenEvent event,CallbackInfo ci) {
		// remove wacky splashes as LCA already adds them
		if (event.getGui() instanceof GuiMainMenu)
			ci.cancel();
	}
}
