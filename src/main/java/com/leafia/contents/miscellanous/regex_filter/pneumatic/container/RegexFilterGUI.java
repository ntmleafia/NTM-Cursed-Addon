// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.miscellanous.regex_filter.pneumatic.container;

import com.hbm.util.I18nUtil;
import com.leafia.contents.miscellanous.regex_filter.pneumatic.RegexFilterTE;
import com.leafia.contents.miscellanous.regex_filter.pneumatic.RegexFilterTE.RegexFilter;
import com.leafia.dev.LeafiaUtil.ScrollUtil;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegexFilterGUI extends LCEGuiInfoContainer implements IRegexFilterGUI {

	private static final ResourceLocation texture = new ResourceLocation("leafia:textures/gui/regex_filter.png");

	private final RegexFilterTE entity;
	public RegexFilterGUI(InventoryPlayer invPlayer,RegexFilterTE entity) {
		super(new RegexFilterContainer(invPlayer,entity));
		entity.gui = this;
		this.entity = entity;
		this.xSize = 176;
		this.ySize = 185;
	}
	public RegexFilterGUI(Container container,RegexFilterTE entity) {
		super(container);
		entity.gui = this;
		this.entity = entity;
		this.xSize = 176;
		this.ySize = 185;
	}

	int truncatorLength = 0;
	FiaUIRect scrollRect;
	@Override
	public void initGui() {
		super.initGui();
		truncatorLength = fontRenderer.getStringWidth("...");
		scrollRect = new FiaUIRect(this,156,17,12,52);
		generateRegexEntires();
	}

	@Override
	public void onSyncFilters() {
		generateRegexEntires();
	}

	boolean screenSwitching = false;
	@Override
	public void onGuiClosed() {
		if (!screenSwitching)
			super.onGuiClosed();
	}

	int scroll = 0;
	int getMaxScroll() {
		return entity.filters.size()-2;
	}
	public static class RegexEntry {
		FiaUIRect rect;
		FiaUIRect blacklistRect;
		RegexFilter filter;
	}
	List<RegexEntry> list = new ArrayList<>();
	void generateRegexEntires() {
		list.clear();
		for (int i = 0; i < entity.filters.size()+1; i++) {
			RegexEntry entry = new RegexEntry();
			if (i < entity.filters.size()) {
				entry.filter = entity.filters.get(i);
				entry.rect = new FiaUIRect(this,133,18+i*18,14,14);
				entry.blacklistRect = new FiaUIRect(this,122,19+i*18,10,12);
			} else
				entry.rect = new FiaUIRect(this,86,18+i*18,14,14);
			list.add(entry);
		}
		int maxScroll = Math.max(getMaxScroll(),0);
		scroll = MathHelper.clamp(scroll,0,maxScroll);
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

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX,mouseY,f);
		super.renderHoveredToolTip(mouseX,mouseY);
		for (int i = scroll; i <= scroll+2; i++) {
			if (i < list.size()) {
				RegexEntry entry = list.get(i);
				if (entry.rect.isMouseIn(mouseX,mouseY+scroll*18)) {
					if (entry.filter == null)
						drawHoveringText(I18nUtil.resolveKey("tile.regex_filter.gui.main.add"),mouseX,mouseY);
					else
						drawHoveringText(I18nUtil.resolveKey("tile.regex_filter.gui.main.edit"),mouseX,mouseY);
					break;
				}
				if (entry.filter != null) {
					if (entry.blacklistRect.isMouseIn(mouseX,mouseY+scroll*18)) {
						drawHoveringText(I18nUtil.resolveKey("tile.regex_filter.gui.main."+
								(entry.filter.blacklist ? "blacklist" : "whitelist")),mouseX,mouseY);
					}
				}
			} else break;
		}
	}

	boolean scrollin = false;

	protected void mouseClicked(int x,int y,int b) throws IOException {
		super.mouseClicked(x,y,b);
		if (b == 0) {
			if (scrollRect.isMouseIn(x,y))
				scrollin = true;
			for (int i = scroll; i <= scroll+2; i++) {
				if (i < list.size()) {
					RegexEntry entry = list.get(i);
					if (entry.filter != null) {
						if (entry.blacklistRect.isMouseIn(x,y+scroll*18)) {
							playClick(1);
							entry.filter.blacklist = !entry.filter.blacklist;
							entity.generateSyncPacket().__sendToServer();
						}
					}
					if (entry.rect.isMouseIn(x,y+scroll*18)) {
						playClick(1);
						screenSwitching = true;
						mc.displayGuiScreen(new RegexFilterGUIFilter(inventorySlots,entity,entry.filter));
						break;
					}
				} else break;
			}
		}
    }

	@Override
	protected void mouseReleased(int mouseX,int mouseY,int button) {
		super.mouseReleased(mouseX,mouseY,button);
		if (button == 0)
			scrollin = false;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.entity.getBlockType().getTranslationKey()+".name");

		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 5, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		int maxScroll = Math.max(getMaxScroll(),0);
		int scrollBarPos = 0;
		if (maxScroll > 0) {
			if (scrollin)
				scroll = ScrollUtil._getScrollOffset(15,52,mouseY-17-guiTop,maxScroll);
			//scroll = MathHelper.clamp((int)((mouseY-17-7-guiTop)*maxScroll/37f+0.5f),0,maxScroll);
			scrollBarPos = ScrollUtil.getScrollBarPos(15,52,ScrollUtil._getScrollRatio(scroll,maxScroll));
			//scrollBarPos = scroll*37/maxScroll;
		}
		drawTexturedModalRect(guiLeft+156,guiTop+17+scrollBarPos,(maxScroll > 0) ? 176 : 188,0,12,15);
		int pos = 0;
		for (int i = scroll; i <= scroll+2; i++) {
			if (i < list.size()) {
				RegexEntry entry = list.get(i);
				if (entry.filter != null) {
					drawTexturedModalRect(guiLeft+37,guiTop+16+pos*18,0,185,112,18);
					String text = entry.filter.regex;
					if (fontRenderer.getStringWidth(text) > 88) {
						text = "";
						for (char c : entry.filter.regex.toCharArray()) {
							if (fontRenderer.getStringWidth(text+c)+truncatorLength <= 77)
								text = text+c;
							else {
								text = text+"...";
								break;
							}
						}
					}
					fontRenderer.drawString(text,guiLeft+37+5,guiTop+16+pos*18+5,0xa6a6a6,true);
					Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

					float color = entry.filter.blacklist ? 0 : 1;
					if (entry.blacklistRect.isMouseIn(mouseX,mouseY+scroll*18))
						color = entry.filter.blacklist ? 0.1f : 0.9f;
					LeafiaGls.color(color,color,color);
					drawTexturedModalRect(
							entry.blacklistRect.guiLeft+entry.blacklistRect.x,
							entry.blacklistRect.guiTop+entry.blacklistRect.y-scroll*18,
							112,185,10,12
					);
					LeafiaGls.color(1,1,1);
				}
				if (entry.rect.isMouseIn(mouseX,mouseY+scroll*18))
					LeafiaGls.color(0.9f,0.9f,0.9f);
				drawTexturedModalRect(
						entry.rect.guiLeft+entry.rect.x,entry.rect.guiTop+entry.rect.y-scroll*18,
						entry.filter != null ? 225 : 239,171,
						14,14
				);
				LeafiaGls.color(1,1,1);
			} else break;
			pos++;
		}
	}
}
