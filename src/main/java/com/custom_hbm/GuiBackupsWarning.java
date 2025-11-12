package com.custom_hbm;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

// thanks community edition
@SideOnly(Side.CLIENT)
public class GuiBackupsWarning extends GuiScreen {

	public static List<String> text = new ArrayList<>();
	private final String downloadURL = "https://www.curseforge.com/minecraft/mc-mods/backups";
	private int[] lineX;
	private int[] lineY;
	public static int downloadButtonIndex = -1;

	@Override
	public void initGui() {
		// Compute line positions for centering
		lineX = new int[text.size()];
		lineY = new int[text.size()];

		int totalHeight = text.size() * fontRenderer.FONT_HEIGHT + (text.size() - 1) * 5; // 5px spacing
		int startY = (height - totalHeight) / 2;

		for (int i = 0; i < text.size(); i++) {
			lineX[i] = width / 2 - fontRenderer.getStringWidth(text.get(i)) / 2;
			lineY[i] = startY + i * (fontRenderer.FONT_HEIGHT + 5);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();

		for (int i = 0; i < text.size(); i++) {
			int color = 0xFFFFFF;
			String prefix = "";

			if (i == downloadButtonIndex) {
				int x = lineX[i];
				int y = lineY[i];
				int w = fontRenderer.getStringWidth(text.get(i));
				int h = fontRenderer.FONT_HEIGHT;
				prefix = TextFormatting.UNDERLINE.toString();

				if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
					color = 0x00FFFF;
				}
			}

			fontRenderer.drawStringWithShadow(prefix+text.get(i), lineX[i], lineY[i], color);
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (downloadButtonIndex == -1) return;
		int x = lineX[downloadButtonIndex];
		int y = lineY[downloadButtonIndex];
		int w = fontRenderer.getStringWidth(text.get(downloadButtonIndex));
		int h = fontRenderer.FONT_HEIGHT;

		if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
			try {
				java.awt.Desktop.getDesktop().browse(new java.net.URI(downloadURL));
				super.mouseClicked(mouseX, mouseY, mouseButton);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			mc.displayGuiScreen(null);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		mc.displayGuiScreen(null);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}


