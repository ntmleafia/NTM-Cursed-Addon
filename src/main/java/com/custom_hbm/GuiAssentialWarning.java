// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// thanks community edition
@SideOnly(Side.CLIENT)
public class GuiAssentialWarning extends GuiScreen {

	public static List<String> text = new ArrayList<>();
	private int[] lineX;
	private int[] lineY;
	private GuiButton dismiss;

	@Override
	public void initGui() {
		// Compute line positions for centering
		lineX = new int[text.size()];
		lineY = new int[text.size()];

		int totalHeight = text.size() * fontRenderer.FONT_HEIGHT + (text.size() - 1) * 5; // 5px spacing
		int startY = (height - totalHeight) / 2 - 10;

		for (int i = 0; i < text.size(); i++) {
			lineX[i] = width / 2 - fontRenderer.getStringWidth(text.get(i)) / 2;
			lineY[i] = startY + i * (fontRenderer.FONT_HEIGHT + 5);
		}
		dismiss = new GuiButton(0,width/2-100,height/2+60,"Fuck you I know what I'm doing");
		addButton(dismiss);
	}

	@Override
	protected void keyTyped(char typedChar,int keyCode) throws IOException {
		//super.keyTyped(typedChar,keyCode); nope!
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();

		for (int i = 0; i < text.size(); i++) {
			int color = 0xFF0000;
			if (text.get(i).startsWith("I suggest"))
				color = 0xFFFFFF;
			String prefix = "";

			fontRenderer.drawStringWithShadow(prefix+text.get(i), lineX[i], lineY[i], color);
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button == dismiss)
			mc.displayGuiScreen(null);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}


