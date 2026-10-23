// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.elevators.items.radio.container;

import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.gear.advisor.AdvisorItem;
import com.leafia.contents.gear.advisor.container.AdvisorUI.HistoryElement;
import com.leafia.contents.machines.elevators.items.radio.EvRadioItem.ElevatorMusic;
import com.leafia.contents.machines.elevators.items.radio.EvRadioItem.EvRadioSyncRequestPacket;
import com.leafia.contents.machines.elevators.items.radio.EvRadioItem.EvRadioUIPacket;
import com.leafia.dev.LeafiaUtil.ScrollUtil;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.GuiScreenLeafia;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class EvRadioUIList extends GuiScreenLeafia {
	public int upOrdinal = -1;
	public int downOrdinal = -1;
	public int idleOrdinal = -1;
	public final int target;
	public static ResourceLocation texture = new ResourceLocation("leafia","textures/gui/elevators/radio_change.png");
	public EvRadioUIList(int target,int upOrdinal,int downOrdinal,int idleOrdinal) {
		xSize = 256;
		ySize = 184;
		this.target = target;
		this.upOrdinal = upOrdinal;
		this.downOrdinal = downOrdinal;
		this.idleOrdinal = idleOrdinal;
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
	FiaUIRect scrollRect;
	int scroll = 0;
	int getMaxScroll() {
		return ElevatorMusic.values().length-9;
	}
	@Override
	public void initGui() {
		super.initGui();
		scrollRect = new FiaUIRect(this,236,18,12,160);
	}
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int i = -Math.clamp(Mouse.getEventDWheel(),-1,1);
		if (getMaxScroll() > 0) {
			scroll += i;
			scroll = MathHelper.clamp(scroll,0,getMaxScroll());
		}
	}
	boolean scrollin = false;
	protected void mouseClicked(int mouseX,int mouseY,int b) throws IOException {
		super.mouseClicked(mouseX,mouseY,b);
		if (b == 0) {
			if (scrollRect.isMouseIn(mouseX,mouseY))
				scrollin = true;
			for (int i = scroll; i <= scroll+8; i++) {
				if (i < ElevatorMusic.values().length+1) {
					int x = guiLeft+8;
					int y = guiTop+17+i*18;
					boolean isHovered = mouseX >= x && mouseY >= y && mouseX < x+223 && mouseY < y+18;
					if (isHovered) {
						if (target == 0)
							upOrdinal = i-1;
						else if (target == 1)
							downOrdinal = i-1;
						else if (target == 2)
							idleOrdinal = i-1;
						playClick(1);
						goBack();
					}
				}
			}
		}
	}
	@Override
	protected void mouseReleased(int mouseX,int mouseY,int button) {
		super.mouseReleased(mouseX,mouseY,button);
		if (button == 0)
			scrollin = false;
	}
	boolean screenSwitching = false;
	@Override
	public void onGuiClosed() {
		if (screenSwitching) return;
		goBack();
	}
	void goBack() {
		screenSwitching = true;
		Minecraft.getMinecraft().displayGuiScreen(new EvRadioUI(upOrdinal,downOrdinal,idleOrdinal));
	}
	@Override
	protected void keyTyped(char typedChar,int keyCode) throws IOException {
		if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
			goBack();
	}
	protected void drawGuiContainerForegroundLayer(int i,int j) {
		String name = I18n.format(AddonItems.ev_radio.getTranslationKey()+".name");

		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 5, 4210752);
	}
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);

		int maxScroll = Math.max(getMaxScroll(),0);
		scroll = MathHelper.clamp(scroll,0,maxScroll);
		int scrollBarPos = 0;
		if (maxScroll > 0) {
			if (scrollin)
				scroll = ScrollUtil._getScrollOffset(15,160,mouseY-18-guiTop,maxScroll);
			//scroll = MathHelper.clamp((int)((mouseY-18-7-guiTop)*maxScroll/91f+0.5f),0,maxScroll);
			scrollBarPos = ScrollUtil.getScrollBarPos(15,160,ScrollUtil._getScrollRatio(scroll,maxScroll));
			//scrollBarPos = scroll*91/maxScroll;
		}
		drawTexturedModalRect(guiLeft+236,guiTop+18+scrollBarPos,(maxScroll > 0) ? 0 : 12,220,12,15);

		LeafiaGls.color(1,1,1);
		for (int i = scroll; i <= scroll+8; i++) {
			if (i < ElevatorMusic.values().length+1) {
				int x = guiLeft+8;
				int y = guiTop+17+i*18;
				String s = "None";
				if (i >= 1) {
					ElevatorMusic element = ElevatorMusic.values()[i-1];
					s = element.artist+" - "+element.name;
				}
				boolean isHovered = mouseX >= x && mouseY >= y && mouseX < x+223 && mouseY < y+18;
				LeafiaGls.color(1,1,1);
				drawTexturedModalRect(
						x,y-scroll*18,
						0,isHovered ? 202 : 184,223,18
				);
				LeafiaGls.color(1,1,1);
				fontRenderer.drawString(
						s,
						x+5,y+5-scroll*18,
						0xFFFFFF,true
				);
				Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
			}
		}
	}
}
