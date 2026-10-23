// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.miscellanous.regex_filter.pneumatic.container;

import com.hbm.util.I18nUtil;
import com.leafia.contents.miscellanous.regex_filter.pneumatic.RegexFilterTE;
import com.leafia.contents.miscellanous.regex_filter.pneumatic.RegexFilterTE.FilterType;
import com.leafia.contents.miscellanous.regex_filter.pneumatic.RegexFilterTE.RegexFilter;
import com.leafia.dev.LeafiaUtil;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.GuiScreenLeafia;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegexFilterGUIFilter extends GuiScreenLeafia implements IRegexFilterGUI {

	private static final ResourceLocation texture = new ResourceLocation("leafia:textures/gui/regex_filter.png");

	private final RegexFilterTE entity;
	final Container container;
	public RegexFilter filter;
	FilterType filterType = FilterType.RESOURCE_ID;
	GuiTextField field;
	public RegexFilterGUIFilter(Container container,RegexFilterTE entity,RegexFilter filter) {
		entity.gui = this;
		this.entity = entity;
		this.filter = filter;
		if (filter != null)
			filterType = filter.type;
		this.container = container;
		this.xSize = 176;
		this.ySize = 41;
	}

	FiaUIRect modeRect;
	FiaUIRect saveRect;

	@Override
	public void initGui() {
		super.initGui();
		modeRect = new FiaUIRect(this,132,16,18,18);
		saveRect = new FiaUIRect(this,151,16,18,18);
		field = new GuiTextField(0,this.fontRenderer,guiLeft+8+4,guiTop+18+4,117-8,10);
		field.setTextColor(0x5BBC00);
		field.setDisabledTextColour(0x499500);
		field.setEnableBackgroundDrawing(false);
		if (filter != null)
			field.setText(filter.regex);
		field.setMaxStringLength(50);
	}

	@Override
	public void onSyncFilters() {
		// if we are creating new filter, do nothing
		if (filter == null) return;
		// if we are editing filter and the filter was made gone by other players, forcibly close the GUI
		for (RegexFilter other : entity.filters) {
			if (other.equals(filter)) {
				filter = other;
				return;
			}
		}
		exitConfig();
	}

	void exitConfig() {
		screenSwitching = true;
		mc.displayGuiScreen(new RegexFilterGUI(container,entity));
	}

	boolean screenSwitching = false;
	@Override
	public void onGuiClosed() {
		if (!screenSwitching && this.mc != null)
			container.onContainerClosed(mc.player);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		this.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);
		super.drawScreen(mouseX,mouseY,f);
		if (modeRect.isMouseIn(mouseX,mouseY)) {
			drawHoveringText(
					I18nUtil.resolveKey("tile.regex_filter.gui.config.filter",
							I18nUtil.resolveKey("tile.regex_filter.gui.filter."+filterType.name())
					),
					mouseX,mouseY
			);
		} else if (saveRect.isMouseIn(mouseX,mouseY)) {
			List<String> texts = new ArrayList<>();
			if (isDelete())
				texts.add(I18nUtil.resolveKey("tile.regex_filter.gui.config.delete"));
			else {
				texts.add(I18nUtil.resolveKey("tile.regex_filter.gui.config.save"));
				if (filter != null)
					texts.add(I18nUtil.resolveKey("tile.regex_filter.gui.config.shift"));
			}
			drawHoveringText(texts,mouseX,mouseY);
		}
		LeafiaGls.color(1,1,1);
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(guiLeft,guiTop,0);
		this.drawGuiContainerForegroundLayer(mouseX, mouseY);
		LeafiaGls.popMatrix();
	}

	protected void mouseClicked(int x,int y,int i) throws IOException {
		super.mouseClicked(x,y,i);
		field.mouseClicked(x,y,i);
		if (i == 0) {
			if (modeRect.isMouseIn(x,y)) {
				playClick(1);
				filterType = FilterType.values()[(filterType.ordinal()+1)%FilterType.values().length];
			} else if (saveRect.isMouseIn(x,y)) {
				if (isDelete()) {
					playClick(1);
					entity.filters.remove(filter);
				} else {
                    if (!LeafiaUtil.isRegexValid(field.getText())) {
						playDenied();
	                    return;
                    }
					playClick(1);
					RegexFilter f = filter;
					if (f == null)
						f = new RegexFilter();
					f.type = filterType;
					f.regex = field.getText();
					if (filter == null)
						entity.filters.add(f);
					filter = null;
				}
				entity.generateSyncPacket().__sendToServer();
				exitConfig();
			}
		}
    }

	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = I18n.format(this.entity.getBlockType().getTranslationKey()+".name");

		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 5, 4210752);
	}

	boolean isDelete() {
		if (filter == null) return false;
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft,guiTop,0,203,xSize,ySize);
        boolean valid = LeafiaUtil.isRegexValid(field.getText());

		if (modeRect.isMouseIn(mouseX,mouseY))
			LeafiaGls.color(0.9f,0.9f,0.9f);
		drawTexturedModalRect(guiLeft+132,guiTop+16,200,filterType.ordinal()*18,18,18);
		LeafiaGls.color(1,1,1);

		if (saveRect.isMouseIn(mouseX,mouseY))
			LeafiaGls.color(0.9f,0.9f,0.9f);
		drawTexturedModalRect(guiLeft+151,guiTop+16,isDelete() ? 194 : 176,219,18,18);
		LeafiaGls.color(1,1,1);

		if (field.isFocused())
			drawTexturedModalRect(guiLeft+8,guiTop+18,136,185,117,14);
		field.setTextColor(valid ? 0x5BBC00 : 0xD84747);
		field.setDisabledTextColour(valid ? 0x499500 : 0xA33A3A);
		field.drawTextBox();
	}

	@Override
	protected void keyTyped(char typedChar,int keyCode) throws IOException {
		if ((keyCode == 1 || keyCode == 13) && field.isFocused()) {
			field.setFocused(false);
			return;
		}
		if (!field.textboxKeyTyped(typedChar,keyCode)) {
			if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
				exitConfig();
		}
	}
}
