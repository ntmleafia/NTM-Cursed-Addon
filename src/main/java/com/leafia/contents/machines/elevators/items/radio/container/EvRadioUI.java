// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.elevators.items.radio.container;

import com.hbm.util.EnumUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.machines.elevators.items.radio.EvRadioItem.ElevatorMusic;
import com.leafia.contents.machines.elevators.items.radio.EvRadioItem.EvRadioSyncRequestPacket;
import com.leafia.contents.machines.elevators.items.radio.EvRadioItem.EvRadioUIPacket;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.GuiScreenLeafia;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class EvRadioUI extends GuiScreenLeafia {
	public int upOrdinal = -1;
	public int downOrdinal = -1;
	public int idleOrdinal = -1;
	public boolean loading = true;
	public static EvRadioUI instance;
	public static ResourceLocation texture = new ResourceLocation("leafia","textures/gui/elevators/radio_main.png");
	public EvRadioUI() {
		xSize = 256;
		ySize = 184;
		instance = this;
		LeafiaCustomPacket.__start(new EvRadioSyncRequestPacket()).__sendToServer();
	}
	public EvRadioUI(int upOrdinal,int downOrdinal,int idleOrdinal) {
		xSize = 256;
		ySize = 184;
		instance = this;
		this.upOrdinal = upOrdinal;
		this.downOrdinal = downOrdinal;
		this.idleOrdinal = idleOrdinal;
		loading = false;
	}
	FiaUIRect changeUp;
	FiaUIRect changeDown;
	FiaUIRect changeIdle;
	@Override
	public void initGui() {
		super.initGui();
		changeUp = new FiaUIRect(this,208,19,39,12);
		changeDown = new FiaUIRect(this,208,73,39,12);
		changeIdle = new FiaUIRect(this,208,127,39,12);
	}
	void selector(int target) {
		screenSwitching = true;
		playClick(1);
		Minecraft.getMinecraft().displayGuiScreen(new EvRadioUIList(target,upOrdinal,downOrdinal,idleOrdinal));
	}
	@Override
	protected void mouseClicked(int mouseX,int mouseY,int mouseButton) throws IOException {
		super.mouseClicked(mouseX,mouseY,mouseButton);
		if (loading) return;
		if (mouseButton == 0) {
			if (changeUp.isMouseIn(mouseX,mouseY))
				selector(0);
			if (changeDown.isMouseIn(mouseX,mouseY))
				selector(1);
			if (changeIdle.isMouseIn(mouseX,mouseY))
				selector(2);
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
	boolean screenSwitching = false;
	@Override
	public void onGuiClosed() {
		instance = null;
		if (screenSwitching) return;
		EvRadioUIPacket packet = new EvRadioUIPacket();
		packet.upOrdinal = upOrdinal;
		packet.downOrdinal = downOrdinal;
		packet.idleOrdinal = idleOrdinal;
		LeafiaCustomPacket.__start(packet).__sendToServer();;
		super.onGuiClosed();
	}
	void drawMusicInfo(int y,int ordinal) {
		if (ordinal == -1) return;
		ElevatorMusic music = EnumUtil.grabEnumSafely(ElevatorMusic.values(),ordinal);
		fontRenderer.drawString(music.name,10,y+1,4210752);
		fontRenderer.drawString("( "+music.artist+" )",10,y+1+12,4210752);
	}
	protected void drawGuiContainerForegroundLayer(int i,int j) {
		String name = I18n.format(AddonItems.ev_radio.getTranslationKey()+".name");
		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 5, 4210752);
		drawMusicInfo(31,upOrdinal);
		drawMusicInfo(85,downOrdinal);
		drawMusicInfo(139,idleOrdinal);
	}
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		if (loading) LeafiaGls.color(0.9f,0.9f,0.9f);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		if (loading) {
			fontRenderer.drawString(
					"Loading...",guiLeft+xSize/2f-0.5f-fontRenderer.getStringWidth("Loading...")/2f,guiTop+ySize/2f-6.5f,0xFFFFFF,true
			);
			LeafiaGls.color(1,1,1);
		}
		if (loading) return;
		if (changeUp.isMouseIn(mouseX,mouseY))
			drawTexturedModalByFiaRect(changeUp,0,184);
		if (changeDown.isMouseIn(mouseX,mouseY))
			drawTexturedModalByFiaRect(changeDown,0,184);
		if (changeIdle.isMouseIn(mouseX,mouseY))
			drawTexturedModalByFiaRect(changeIdle,0,184);
	}
}
