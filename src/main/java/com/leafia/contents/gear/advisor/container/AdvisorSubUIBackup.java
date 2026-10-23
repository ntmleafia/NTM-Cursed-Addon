// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.gear.advisor.container;

import com.leafia.contents.AddonItems;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorPacket;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorSignalType;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.gui.GuiScreenLeafia;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

import static com.leafia.AddonBase.getIntegrated;

public class AdvisorSubUIBackup extends GuiScreenLeafia implements IAdvisorUI {
	public static ResourceLocation texture = getIntegrated("items/advisor/ui/main.png");
	final int scr;
	public AdvisorSubUIBackup(int scroll) {
		AdvisorUI.instance = this;
		xSize = 195;
		ySize = 145;
		scr = scroll;
	}
	boolean screenSwitching = false;
	@Override
	public void onGuiClosed() {
		if (screenSwitching) return;
		AdvisorUI.instance = null;
		LeafiaCustomPacket.__start(new AdvisorPacket(AdvisorSignalType.CLOSE.id)).__sendToServer();
		super.onGuiClosed();
	}
	@Override
	protected void keyTyped(char typedChar,int keyCode) throws IOException {
		if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
			screenSwitching = true;
			Minecraft.getMinecraft().displayGuiScreen(new AdvisorUI(scr));
		}
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		this.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
		super.drawScreen(mouseX,mouseY,f);
		LeafiaGls.color(1,1,1);
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(guiLeft,guiTop,0);
		this.drawGuiContainerForegroundLayer(mouseX, mouseY);
		LeafiaGls.popMatrix();
	}

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(AddonItems.advisor.getTranslationKey()+".name");

		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 5, 4210752);
	}
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
	}
}
