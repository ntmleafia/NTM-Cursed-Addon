// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.miscellanous.slop.container;

import com.hbm.inventory.gui.GuiInfoContainer;
import com.leafia.contents.miscellanous.slop.SlopTE;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class SlopGUI extends LCEGuiInfoContainer {

	private static ResourceLocation texture = new ResourceLocation("leafia:textures/gui/reactors/slop.png");

	private SlopTE entity;
	public SlopGUI(InventoryPlayer invPlayer,SlopTE entity) {
		super(new SlopContainer(invPlayer, entity));
		this.entity = entity;
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX,mouseY,f);
		drawElectricityInfo(this,mouseX,mouseY,guiLeft+152,guiTop+17,16,52,entity.power,entity.getMaxPower());
		super.renderHoveredToolTip(mouseX,mouseY);
	}

	protected void mouseClicked(int x,int y,int i) throws IOException {
		if (i == 0 && x >= guiLeft+7 && y >= guiTop+52 && x < guiLeft+7+18 && y < guiTop+52+18) {
			LeafiaPacket._start(entity).__write(0,!entity.on).__sendToServer();
			playClick(1);
		}
		super.mouseClicked(x,y,i);
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.entity.hasCustomName() ? this.entity.getName() : I18n.format(this.entity.getDefaultName());

		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	void drawVerticalBar(int x,int y,int tx,int ty,int level) {
		drawTexturedModalRect(guiLeft+x,guiTop+y+52-level,tx,ty+52-level,16,level);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		if (entity.on)
			drawTexturedModalRect(guiLeft+7,guiTop+52,208,0,18,18);
		drawVerticalBar(44,17,192,0,entity.fuel*52/27);
		drawVerticalBar(152,17,176,0,(int)(entity.power*52/entity.getMaxPower()));
		if (entity.dynaMaxPartialConsumption > 0)
			drawTexturedModalRect(guiLeft+80,guiTop+17,176,52,52*entity.partialConsumption/entity.dynaMaxPartialConsumption,16);
		drawTexturedModalRect(guiLeft+80+18,guiTop+53,176,68,34*entity.damage/SlopTE.maxDamage,16);
	}
}
