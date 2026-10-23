// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.SubElement;
import com.hbm.inventory.control_panel.SubElementItemConfig;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.lib.internal.MethodHandleHelper;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.client.gui.GuiButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

@Mixin(value = SubElementItemConfig.class)
public class MixinSubElementItemConfig {
	@Shadow(remap = false)
	private SubElementBaseConfig config_gui;

	private static final MethodHandle actionPerformed;
	static {
		try {
			Method reflected = SubElement.class.getDeclaredMethod("actionPerformed", GuiButton.class);
			reflected.setAccessible(true);
			actionPerformed = MethodHandles.lookup().unreflect(reflected);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new LeafiaDevFlaw(e);
		}
	}

	@Inject(method = "actionPerformed",at=@At(value = "TAIL"),require = 1,remap = false)
	void leafia$onActionPerformed(GuiButton button,CallbackInfo ci) {
		try {
			actionPerformed.invokeExact((SubElement)config_gui,button);
		} catch (Throwable e) {
			throw new LeafiaDevFlaw(e);
		}
	}
}
