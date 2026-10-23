// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.processing.mixingvat.container;

import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.processing.mixingvat.MixingVatTE;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class MixingVatNclrGUI extends LCEGuiInfoContainer {

	public static ResourceLocation texture = new ResourceLocation("leafia","textures/gui/processing/mixingvat_nuclear.png");
	private MixingVatTE vat;
	
	public MixingVatNclrGUI(InventoryPlayer invPlayer,MixingVatTE vat) {
		super(new MixingVatContainer(invPlayer, vat));
		this.vat = vat;

		this.xSize = 210;
		this.ySize = 203;
	}

	FiaUIRect clear;
	@Override
	public void initGui() {
		super.initGui();
		clear = new FiaUIRect(this,156,39,8,8);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);

		LeafiaClientUtil.renderTankInfo(this, mouseX, mouseY, guiLeft + 45 - 18, guiTop + 69 - 52, 16, 52, vat.tankNc1, vat.inputTypeNc.getFF());
		LeafiaClientUtil.renderTankInfo(this, mouseX, mouseY, guiLeft + 45 - 18 + 108, guiTop + 69 - 52, 16, 52, vat.tankNc1, vat.inputTypeNc.getFF());

		this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 186, guiTop + 18, 16, 35, vat.power, MixingVatTE.maxPower);

		if (clear.isMouseIn(mouseX,mouseY))
			drawHoveringText(I18nUtil.resolveKey("gui.desc.voidcontents"),mouseX,mouseY);

		super.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void mouseClicked(int mouseX,int mouseY,int mouseButton) throws IOException {
		super.mouseClicked(mouseX,mouseY,mouseButton);
		if (clear.isMouseIn(mouseX,mouseY) && mouseButton == 0) {
			playClick(1);
			LeafiaPacket._start(vat).__write(0,true).__sendToServer();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.vat.hasCustomName() ? this.vat.getName() : I18n.format(this.vat.getDefaultName());
		
		this.fontRenderer.drawString(name, 176 / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		int i = (int) vat.getPowerRemainingScaled(35);
		drawTexturedModalRect(guiLeft + 186, guiTop + 53 - i, 210, 35 - i, 16, i);
		if (vat.power >= 1000/20)
			drawTexturedModalRect(guiLeft+190,guiTop+4,210,35,9,12);

		int j = (int) vat.getProgressScaled(54);
		drawTexturedModalRect(guiLeft + 70, guiTop + 54, 0, 203, j, 16);

		LeafiaClientUtil.drawLiquid(vat.tankNc0, guiLeft, guiTop, zLevel, 16, 52, 44-18, 97);
		LeafiaClientUtil.drawLiquid(vat.tankNc1, guiLeft, guiTop, zLevel, 16, 52, 44-18+108, 97);
	}
}
