// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.gear.advisor.container;

import com.hbm.util.I18nUtil;
import com.leafia.contents.AddonItems;
import com.leafia.contents.gear.advisor.AdvisorItem;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorPacket;
import com.leafia.contents.gear.advisor.AdvisorItem.AdvisorSignalType;
import com.leafia.dev.LeafiaUtil.ScrollUtil;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.GuiScreenLeafia;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.leafia.AddonBase.getIntegrated;

public class AdvisorUI extends GuiScreenLeafia implements IAdvisorUI {
	public static IAdvisorUI instance = null;
	public static ResourceLocation texture = getIntegrated("items/advisor/ui/main.png");
	public boolean loading = true;
	public AdvisorUI() {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(LeafiaSoundEvents.advisor_activate,1));
		instance = this;
		xSize = 195;
		ySize = 145;
		LeafiaCustomPacket.__start(new AdvisorPacket(AdvisorSignalType.SYNC_REQUEST.id)).__sendToServer();
	}
	public AdvisorUI(int scroll) {
		this.scroll = scroll;
		instance = this;
		xSize = 195;
		ySize = 145;
		LeafiaCustomPacket.__start(new AdvisorPacket(AdvisorSignalType.SYNC_REQUEST.id)).__sendToServer();
	}
	boolean screenSwitching = false;
	@Override
	public void onGuiClosed() {
		if (screenSwitching) return;
		instance = null;
		LeafiaCustomPacket.__start(new AdvisorPacket(AdvisorSignalType.CLOSE.id)).__sendToServer();
		super.onGuiClosed();
	}
	public void updateRects() {
		for (HistoryElement warning : warnings) {
			warning.rect.updateGuiPosition(this);
			warning.dirty = false;
		}
	}
	FiaUIRect scrollRect;
	FiaUIRect dismissRect;
	FiaUIRect readRect;
	int dismissCooldown = 0;
	void drawButton(String s,FiaUIRect rect) {
		int width = fontRenderer.getStringWidth(s);
		fontRenderer.drawString(s,rect.guiLeft+rect.x+rect.w/2-width/2,rect.guiTop+rect.y+3,4210752);
	}
	@Override
	public void initGui() {
		super.initGui();
		scrollRect = new FiaUIRect(this,175,18,12,106);
		dismissRect = new FiaUIRect(this,90,127,48,13);
		readRect = new FiaUIRect(this,140,127,48,13);
		updateRects();
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
	protected void mouseClicked(int x,int y,int b) throws IOException {
		super.mouseClicked(x,y,b);
		if (b == 0) {
			if (scrollRect.isMouseIn(x,y))
				scrollin = true;
			else if (dismissRect.isMouseIn(x,y)) {
				if (selection != -1) {
					playClick(1);
					AdvisorPacket packet = new AdvisorPacket(AdvisorSignalType.DISMISS.id);
					packet.dismissIndex = warnings.size()-1-selection;
					LeafiaCustomPacket.__start(packet).__sendToServer();
					selection = -1;
				} else
					playDenied();
			} else if (readRect.isMouseIn(x,y)) {
				if (selection != -1) {
					playClick(1);
					HistoryElement element = warnings.get(selection);
					screenSwitching = true;
					Minecraft.getMinecraft().displayGuiScreen(
							new AdvisorUIRead(I18nUtil.resolveKey(AdvisorItem.msgRoot+element.key+".message"),scroll)
					);
				} else
					playDenied();
			} else
				selection = -1;
			for (int i = scroll; i <= scroll+5; i++) {
				if (i < warnings.size()) {
					HistoryElement element = warnings.get(i);
					if (element.rect.isMouseIn(x,y+18*scroll)) {
						selection = i;
						playClick(1);
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
	public static class HistoryElement {
		final String key;
		final FiaUIRect rect;
		final int index;
		boolean dirty = true;
		public HistoryElement(String key,int index) {
			this.key = key;
			this.index = index;
			rect = new FiaUIRect(8,17+18*index,162,18);
		}
	}
	public List<HistoryElement> warnings = new ArrayList<>();
	int scroll = 0;
	int selection = -1;
	int getMaxScroll() {
		return warnings.size()-6;
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
		if (loading) LeafiaGls.color(0.9f,0.9f,0.9f);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		if (loading) {
			fontRenderer.drawString(
					"Loading...",guiLeft+xSize/2f-0.5f-fontRenderer.getStringWidth("Loading...")/2f,guiTop+ySize/2f-6.5f,0xFFFFFF,true
			);
			LeafiaGls.color(1,1,1);
		}
		if (loading) return;
		int maxScroll = Math.max(getMaxScroll(),0);
		scroll = MathHelper.clamp(scroll,0,maxScroll);
		int scrollBarPos = 0;
		if (maxScroll > 0) {
			if (scrollin)
				scroll = ScrollUtil._getScrollOffset(15,106,mouseY-18-guiTop,maxScroll);
			//scroll = MathHelper.clamp((int)((mouseY-18-7-guiTop)*maxScroll/91f+0.5f),0,maxScroll);
			scrollBarPos = ScrollUtil.getScrollBarPos(15,106,ScrollUtil._getScrollRatio(scroll,maxScroll));
			//scrollBarPos = scroll*91/maxScroll;
		}
		drawTexturedModalRect(guiLeft+175,guiTop+18+scrollBarPos,(maxScroll > 0) ? 195 : 207,0,12,15);

		if (dismissRect.isMouseIn(mouseX,mouseY))
			LeafiaGls.color(0.9f,0.9f,0.9f);
		if (dismissCooldown > 0) {
			dismissCooldown--;
			drawTexturedModalByFiaRect(dismissRect,0,181);
		} else if (dismissRect.isMouseIn(mouseX,mouseY))
			drawTexturedModalByFiaRect(dismissRect,90,127);

		LeafiaGls.color(1,1,1);
		if (readRect.isMouseIn(mouseX,mouseY)) {
			LeafiaGls.color(0.9f,0.9f,0.9f);
			drawTexturedModalByFiaRect(readRect,140,127);
		}

		LeafiaGls.color(1,1,1);
		for (int i = scroll; i <= scroll+5; i++) {
			if (i < warnings.size()) {
				HistoryElement element = warnings.get(i);
				if (element.rect.isMouseIn(mouseX,mouseY+18*scroll))
					LeafiaGls.color(0.9f,0.9f,0.9f);
				drawTexturedModalRect(
						element.rect.guiLeft+element.rect.x,element.rect.guiTop+element.rect.y-scroll*18,
						0,(selection == i) ? 163 : 145,162,18
				);
				LeafiaGls.color(1,1,1);
				fontRenderer.drawString(
						I18nUtil.resolveKey(AdvisorItem.msgRoot+element.key+".name"),
						element.rect.guiLeft+element.rect.x+5,element.rect.guiTop+element.rect.y+5-scroll*18,
						0xa6a6a6,true
				);
				Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
				LeafiaGls.color(1,1,1);
			}
		}
		drawButton(I18nUtil.resolveKey("item.advisor.gui.main.dismiss"),dismissRect);
		drawButton(I18nUtil.resolveKey("item.advisor.gui.main.read"),readRect);
	}
}
