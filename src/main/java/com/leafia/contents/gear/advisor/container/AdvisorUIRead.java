// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.gear.advisor.container;

import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorPacket;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorSignalType;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.GuiScreenLeafia;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.leafia.AddonBase.getIntegrated;

public class AdvisorUIRead extends GuiScreenLeafia implements IAdvisorUI {
	public static ResourceLocation texture = getIntegrated("items/advisor/ui/message.png");
	final String str;
	final int scr;
	public AdvisorUIRead(String message,int scroll) {
		AdvisorUI.instance = this;
		xSize = 195;
		ySize = 145;
		str = message;
		scr = scroll;
	}
	FiaUIRect closeRect;
	@Override
	public void initGui() {
		super.initGui();
		closeRect = new FiaUIRect(this,8,123,48,17);
	}
	void drawButton(String s,FiaUIRect rect) {
		int width = fontRenderer.getStringWidth(s);
		fontRenderer.drawString(s,rect.guiLeft+rect.x+rect.w/2-width/2,rect.guiTop+rect.y+5,4210752);
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
	protected void mouseClicked(int mouseX,int mouseY,int mouseButton) throws IOException {
		super.mouseClicked(mouseX,mouseY,mouseButton);
		if (mouseButton == 0 && closeRect.isMouseIn(mouseX,mouseY)) {
			playClick(1);
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
		if (closeRect.isMouseIn(mouseX,mouseY)) {
			LeafiaGls.color(0.9f,0.9f,0.9f);
			drawTexturedModalByFiaRect(closeRect,8,123);
			LeafiaGls.color(1,1,1);
		}
		List<String> text = new ArrayList<>();
		for (String s : I18nUtil.autoBreak(fontRenderer,str,162-10))
			text.addAll(Arrays.asList(s.split("\\$")));
		int i = 0;
		for (String s : text)
			fontRenderer.drawString(s,guiLeft+8+5,guiTop+17+5+11*(i++),0xa0a0a0,true);
		drawButton(I18nUtil.resolveKey("item.advisor.gui.message.close"),closeRect);
	}
}
