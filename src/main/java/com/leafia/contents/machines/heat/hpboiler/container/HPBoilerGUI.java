// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.heat.hpboiler.container;

import com.leafia.contents.machines.heat.hpboiler.HPBoilerTE;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

import static com.leafia.AddonBase.getIntegrated;

public class HPBoilerGUI extends LCEGuiInfoContainer {

	private static ResourceLocation texture = getIntegrated("fluids/ntmf/ngf_hpboiler/gui.png");

	private HPBoilerTE entity;
	GuiTextField field;
	FiaUIRect button;
	int saveButtonCooldown = 0;
	public HPBoilerGUI(InventoryPlayer invPlayer,HPBoilerTE entity) {
		super(new HPBoilerContainer(invPlayer,entity));
		this.entity = entity;
		this.xSize = 176;
		this.ySize = 185;
	}

	@Override
	public void initGui() {
		super.initGui();
		button = new FiaUIRect(this,88,52,18,18);
		field = new GuiTextField(0,this.fontRenderer,guiLeft+72+9,guiTop+36+4,24,10);
		field.setTextColor(0x5BBC00);
		field.setDisabledTextColour(0x499500);
		field.setEnableBackgroundDrawing(false);
		field.setText(Integer.toString(entity.compression));
		field.setMaxStringLength(2);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX,mouseY,f);
		LeafiaClientUtil.renderTankInfo(entity.input,this,mouseX,mouseY,guiLeft+44,guiTop+17,16,52);
		LeafiaClientUtil.renderTankInfo(entity.output,this,mouseX,mouseY,guiLeft+116,guiTop+17,16,52);
		drawCustomInfoStat(mouseX, mouseY, guiLeft + 84, guiTop + 16, 8, 18, mouseX, mouseY, new String[] { entity.heat + " TU" });
		super.renderHoveredToolTip(mouseX,mouseY);
	}

	protected void mouseClicked(int x,int y,int i) throws IOException {
		super.mouseClicked(x,y,i);
		field.mouseClicked(x,y,i);
		if (i == 0 && button.isMouseIn(x,y) && saveButtonCooldown <= 0) {
			try {
				int value = Integer.parseInt(field.getText());
				if (value <= 0) {
					playDenied();
					return;
				}
				LeafiaPacket._start(entity).__write(0,value).__sendToServer();
				playClick(1);
				saveButtonCooldown = 20;
			} catch (NumberFormatException ignored) {
				playDenied();
			}
		}
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.entity.hasCustomName() ? this.entity.getName() : I18n.format(this.entity.getDefaultName());

		this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawDefaultBackground();
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		int heatpx = Math.min(entity.heat*16/entity.heatReq,16);
		drawTexturedModalRect(guiLeft+85,guiTop+17+16-heatpx,194,14+16-heatpx,6,heatpx);
		if (saveButtonCooldown > 0) {
			drawTexturedModalRect(guiLeft+88,guiTop+52,176,14,18,18);
			saveButtonCooldown--;
		}
		if (field.isFocused())
			drawTexturedModalRect(guiLeft+72,guiTop+36,176,0,32,14);
		field.drawTextBox();
		entity.input.renderTank(guiLeft+44,guiTop+17+52,zLevel,16,52);
		entity.output.renderTank(guiLeft+116,guiTop+17+52,zLevel,16,52);
	}

	@Override
	protected void keyTyped(char typedChar,int keyCode) throws IOException {
		super.keyTyped(typedChar,keyCode);
		field.textboxKeyTyped(typedChar,keyCode);
	}
}
