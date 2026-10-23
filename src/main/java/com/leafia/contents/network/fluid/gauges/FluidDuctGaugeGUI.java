// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.network.fluid.gauges;

import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.gui.GuiScreenLeafia;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/// mostly stolen from GUIPump lmao
public class FluidDuctGaugeGUI extends GuiScreenLeafia {
	GuiTextField field;
	final FluidDuctGaugeTE te;

	public FluidDuctGaugeGUI(FluidDuctGaugeTE te) {
		this.te = te;
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		field = new GuiTextField(0,this.fontRenderer,this.width/2-50,100,90,20);
		field.setText(String.valueOf(te.maximum));
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		try {
			LeafiaPacket._start(te).__write(0,Long.parseLong(field.getText())).__sendToServer();
			playClick(1);
		} catch (NumberFormatException ignored) {
			playDenied();
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		field.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (field.textboxKeyTyped(typedChar, keyCode)) {
			return;
		}

		if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
			this.mc.player.closeScreen();
			return;
		}

		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		field.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		this.fontRenderer.drawString("Gauge Max:",this.width/2-50,80,0xA0A0A0);
		field.drawTextBox();

		super.drawScreen(mouseX,mouseY,partialTicks);
	}
}
