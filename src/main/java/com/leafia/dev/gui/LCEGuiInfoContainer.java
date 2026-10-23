// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.gui;

import com.hbm.lib.Library;

import com.leafia.init.LeafiaSoundEvents;
import com.leafia.settings.AddonConfig;
import com.leafia.unsorted.BullshitInDavenportIowa;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

/// ntmleafia: I want to explode
public abstract class LCEGuiInfoContainer extends GuiContainerLeafia {

	ResourceLocation guiUtil =  new ResourceLocation("hbm" + ":textures/gui/gui_utility.png");
	protected void playClick(float pitch) {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK,pitch));
	}
	protected void playDenied() {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(LeafiaSoundEvents.UI_BUTTON_INVALID,1));
	}

	public LCEGuiInfoContainer(Container p_i1072_1_) {
		super(p_i1072_1_);
	}
	
	public void drawFluidInfo(String[] text, int x, int y) {
		this.drawHoveringText(Arrays.asList(text), x, y);
	}

	public void drawFluidInfo(List<String> text, int x, int y) {
		this.drawHoveringText(text, x, y);
	}

	//the mojang employee who made this private on the super can explode
	public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
    {
		return this.isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
    }
	public void drawElectricityInfo(LCEGuiInfoContainer gui,int mouseX,int mouseY,int x,int y,int width,int height,long power,long maxPower) {
		if(x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
			if (!AddonConfig.bullshitUnits)
				gui.drawFluidInfo(new String[] { Library.getShortNumber(power) + "/" + Library.getShortNumber(maxPower) + "HE" }, mouseX, mouseY);
			else
				gui.drawFluidInfo(new String[] { Library.getShortNumber((long)BullshitInDavenportIowa.HEToCn(power)) + "/" + Library.getShortNumber((long)BullshitInDavenportIowa.HEToCn(maxPower)) + "corn" }, mouseX, mouseY);
		}
	}
	
	public void drawCustomInfo(LCEGuiInfoContainer gui, int mouseX, int mouseY, int x, int y, int width, int height, String[] text) {
		if(x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY)
			this.drawHoveringText(Arrays.asList(text), mouseX, mouseY);
	}
	
	public void drawCustomInfoStat(int mouseX, int mouseY, int x, int y, int width, int height, int tPosX, int tPosY, String[] text) {
		
		if(x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY)
			this.drawHoveringText(Arrays.asList(text), tPosX, tPosY);
	}
	
	public void drawInfoPanel(int x, int y, int width, int height, int type) {

		Minecraft.getMinecraft().getTextureManager().bindTexture(guiUtil);
		
		if (type >= 0 && type < 12) {
			var column = type % 4;
			var size = column < 2 ? 8 : 16;
			var textureX = column < 2 ? 0 : 8 + (column - 2) * 16;
			var textureY = (type / 4) * 16 + (column == 1 ? 8 : 0);
			drawTexturedModalRect(x, y, textureX, textureY, size, size);
		}
	}

}
