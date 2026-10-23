// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.heatex.container;

import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.toserver.NBTControlPacket;
import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.misc.heatex.CoolantHeatexTE;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Arrays;

public class CoolantHeatexGUI extends LCEGuiInfoContainer {
	private final CoolantHeatexTE heatex;
	static final ResourceLocation tex = new ResourceLocation("leafia","textures/gui/lftr/gui_heatex.png");
	private GuiTextField fieldCycles;
	private GuiTextField fieldDelay;
	public CoolantHeatexGUI(InventoryPlayer invPlayer,CoolantHeatexTE heatex) {
		super(new CoolantHeatexContainer(invPlayer,heatex));
		this.heatex = heatex;
		this.xSize = 176;
		this.ySize = 204;
	}
	FiaUIRect ntmfSwitch;
	FiaUIRect coolantMode;
	@Override
	public void initGui() {
		super.initGui();
		ntmfSwitch = new FiaUIRect(this,50,72,51,16);
		coolantMode = new FiaUIRect(this,107,78,14,18);
		Keyboard.enableRepeatEvents(true);
		this.fieldCycles = new GuiTextField(0, this.fontRenderer, guiLeft + 73-6, guiTop + 31, 30, 10);
		initText(this.fieldCycles);
		this.fieldCycles.setText(String.valueOf(heatex.amountToCool));

		this.fieldDelay = new GuiTextField(1, this.fontRenderer, guiLeft + 73-6, guiTop + 49, 30, 10);
		initText(this.fieldDelay);
		this.fieldDelay.setText(String.valueOf(heatex.tickDelay));
	}

	protected void initText(GuiTextField field) {
		field.setTextColor(0x00ff00);
		field.setDisabledTextColour(0x00ff00);
		field.setEnableBackgroundDrawing(false);
		field.setMaxStringLength(5);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);

		//compressor.tanks[0].renderTankInfo(this, mouseX, mouseY, guiLeft + 17, guiTop + 18, 16, 52);
		//compressor.tanks[1].renderTankInfo(this, mouseX, mouseY, guiLeft + 107, guiTop + 18, 16, 52);
		this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 152, guiTop + 18, 16, 52, heatex.power,CoolantHeatexTE.maxPower);

		if (ntmfSwitch.isMouseIn(mouseX,mouseY))
			drawHoveringText(Arrays.asList(
					TextFormatting.LIGHT_PURPLE+I18nUtil.resolveKey("gui.coolant_heatex.ntmfswitch.title"),
					TextFormatting.LIGHT_PURPLE+" "+I18nUtil.resolveKey("gui.coolant_heatex.ntmfswitch.desc.ff"),
					TextFormatting.LIGHT_PURPLE+" "+I18nUtil.resolveKey("gui.coolant_heatex.ntmfswitch.desc.ntmf"),
					" "+I18nUtil.resolveKey("gui.coolant_heatex.ntmfswitch",heatex.ntmfMode ? "NTMF" : "FF")
			),mouseX,mouseY);

		if (coolantMode.isMouseIn(mouseX,mouseY))
			drawHoveringText(Arrays.asList(I18nUtil.resolveKey("gui.coolant_heatex.coolant",CoolantHeatexTE.coolants[heatex.coolantMode].getLocalizedName()).split("\\$")),mouseX,mouseY);

		if (guiLeft + 70-6 <= mouseX && guiLeft + 70-6 + 36 > mouseX && guiTop + 26 < mouseY && guiTop + 26 + 18 >= mouseY) {
			drawHoveringText(Arrays.asList(I18nUtil.resolveKey("gui.heatex.amount")), mouseX, mouseY);
		}
		if (guiLeft + 70-6 <= mouseX && guiLeft + 70-6 + 36 > mouseX && guiTop + 44 < mouseY && guiTop + 44 + 18 >= mouseY) {
			drawHoveringText(Arrays.asList(I18nUtil.resolveKey("gui.heatex.cycle")), mouseX, mouseY);
		}

		LeafiaClientUtil.renderTankInfo(heatex.ntmf_inputB,this,mouseX,mouseY,guiLeft+107,guiTop+18,16,52);
		LeafiaClientUtil.renderTankInfo(heatex.ntmf_outputB,this,mouseX,mouseY,guiLeft+124,guiTop+79,10,16);
		if (heatex.ntmfMode) {
			LeafiaClientUtil.renderTankInfo(heatex.ntmf_inputA,this,mouseX,mouseY,guiLeft+17,guiTop+18,16,52);
			LeafiaClientUtil.renderTankInfo(heatex.ntmf_outputA,this,mouseX,mouseY,guiLeft+41,guiTop+18,16,52);
		} else {
			LeafiaClientUtil.renderTankInfo(this,mouseX,mouseY,guiLeft+17,guiTop+18,16,52,heatex.ff_inputA,heatex.inputFilter.getFF());
			LeafiaClientUtil.renderTankInfo(this,mouseX,mouseY,guiLeft+41,guiTop+18,16,52,heatex.ff_outputA,heatex.outputFilter.getFF());
		}

		renderHoveredToolTip(mouseX, mouseY);
	}
	@Override
	protected void mouseClicked(int mouseX,int mouseY,int mouseButton) throws IOException {
		super.mouseClicked(mouseX,mouseY,mouseButton);
		if (mouseButton == 0) {
			if (ntmfSwitch.isMouseIn(mouseX,mouseY)) {
				playClick(1);
				LeafiaPacket._start(heatex).__write(0,false).__sendToServer();
			}
			if (coolantMode.isMouseIn(mouseX,mouseY)) {
				playClick(1);
				LeafiaPacket._start(heatex).__write(0,true).__sendToServer();
			}
		}
		this.fieldCycles.mouseClicked(mouseX, mouseY, mouseButton);
		this.fieldDelay.mouseClicked(mouseX, mouseY, mouseButton);
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {

		String name = this.heatex.hasCustomName() ? this.heatex.getName() : I18n.format(this.heatex.getName());

		this.fontRenderer.drawString(name, /*70*/94 - this.fontRenderer.getStringWidth(name) / 2, 6, 0xC7C1A3);
		this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if(heatex.power >= CoolantHeatexTE.powerConsumption) {
			drawTexturedModalRect(guiLeft + 156, guiTop + 4, 176, 52, 9, 12);
		}

		int j = (int) (heatex.power * 52 / CoolantHeatexTE.maxPower);
		drawTexturedModalRect(guiLeft + 152, guiTop + 70 - j, 176, 52 - j, 16, j);

		// ff/ntmf switch
		drawTexturedModalRect(guiLeft + (heatex.ntmfMode ? 71 : 60),guiTop+72,192,0,10,16);

		// coolant switch
		drawTexturedModalRect(guiLeft+107,guiTop+78,202+14*heatex.coolantMode,0,14,18);

		heatex.ntmf_inputB.renderTank(guiLeft+107,guiTop+18+52,zLevel,16,52);
		heatex.ntmf_outputB.renderTank(guiLeft+124,guiTop+79+16,zLevel,10,16);
		if (heatex.ntmfMode) {
			heatex.ntmf_inputA.renderTank(guiLeft+17,guiTop+18+52,zLevel,16,52);
			heatex.ntmf_outputA.renderTank(guiLeft+41,guiTop+18+52,zLevel,16,52);
		} else {
			LeafiaClientUtil.drawLiquid(heatex.ff_inputA,guiLeft+17,guiTop+18+52,zLevel,16,52,0,28);
			LeafiaClientUtil.drawLiquid(heatex.ff_outputA,guiLeft+41,guiTop+18+52,zLevel,16,52,0,28);
		}
		this.fieldCycles.drawTextBox();
		this.fieldDelay.drawTextBox();
	}

	@Override
	protected void keyTyped(char c, int i) throws IOException {
		if (this.fieldCycles.textboxKeyTyped(c, i)) {
			int cyc = Math.max(NumberUtils.toInt(this.fieldCycles.getText()), 1);
			LeafiaPacket._start(heatex).__write(1,cyc).__sendToServer();
			return;
		}
		if (this.fieldDelay.textboxKeyTyped(c, i)) {
			int delay = Math.max(NumberUtils.toInt(this.fieldDelay.getText()), 1);
			LeafiaPacket._start(heatex).__write(2,delay).__sendToServer();
			return;
		}

		super.keyTyped(c, i);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
}
