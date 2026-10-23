// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.inventory.control_panel.*;
import com.leafia.overwrite_contents.interfaces.IMixinGuiControlEdit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SubElementNodeEditor.class)
public abstract class MixinSubElementNodeEditor extends SubElement {
	public MixinSubElementNodeEditor(GuiControlEdit gui) {
		super(gui);
	}
	@Redirect(method = "mouseClicked",at = @At(value = "INVOKE", target = "Lcom/hbm/inventory/control_panel/NodeSystem;getNodeElementPressed(FF)Lcom/hbm/inventory/control_panel/NodeElement;"),require = 1,remap = false)
	NodeElement leafia$onMouseClicked(NodeSystem instance,float x,float y) {
		NodeElement pressed = instance.getNodeElementPressed(x,y);
		if (pressed != null) {
			if (pressed.name.equals("Edit Code")) {
				IMixinGuiControlEdit mixin = (IMixinGuiControlEdit)gui;
				mixin.leafia$pushElement(mixin.leafia$ic10Editor());
			}
		}
		return pressed;
	}
}
