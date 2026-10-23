// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.meters.horizontal;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.render.NTMRenderHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeterHorizontalCSE extends SubElementBaseConfig {
	private static final int[] TRANSFORM = { 50,112+22,66,66 };
	@Override
	protected void drawScreen() {
		GlStateManager.disableLighting();
		NTMRenderHelper.drawGuiRectColor(gui.getGuiLeft()+20,gui.getGuiTop()+70,0,0,15,15,1,1,r/255f,g/255f,b/255f,1F);
		field_min.drawTextBox();
		field_max.drawTextBox();
		GlStateManager.enableLighting();
	}
	@Override
	protected void keyTyped(char typedChar,int code) {
		field_max.textboxKeyTyped(typedChar,code);
		field_min.textboxKeyTyped(typedChar,code);
		super.keyTyped(typedChar,code);
	}

	static final int spacing = 95;
	static final int startXOffset = 85;

	int length = 5;
	int r = 255;
	int g = 153;
	int b = 0;
	float min = 0;
	float max = 1;
	int divisions = 5;
	int subdivisions = 3;
	int decimals = 0;

	GuiSlider slider_length;
	GuiSlider slider_r;
	GuiSlider slider_g;
	GuiSlider slider_b;
	GuiTextField field_max;
	GuiTextField field_min;
	GuiSlider slider_div;
	GuiSlider slider_subd;
	GuiSlider slider_decimals;

	@Override
	protected void mouseReleased(int mouseX,int mouseY,int state) {
		length = slider_length.getValueInt();
		r = slider_r.getValueInt();
		g = slider_g.getValueInt();
		b = slider_b.getValueInt();
		divisions = slider_div.getValueInt();
		subdivisions = slider_subd.getValueInt();
		decimals = slider_decimals.getValueInt();
	}

	@Override
	protected void mouseClicked(int mouseX,int mouseY,int button) {
		field_max.mouseClicked(mouseX,mouseY,button);
		field_min.mouseClicked(mouseX,mouseY,button);
		super.mouseClicked(mouseX,mouseY,button);
	}

	@Override
	protected void update() {
		field_max.setTextColor(0xFFFFFF);
		field_min.setTextColor(0xFFFFFF);
		try {
			max = Float.parseFloat(field_max.getText());
		} catch (NumberFormatException ignored) {
			field_max.setTextColor(0xFF0000);
		}
		try {
			min = Float.parseFloat(field_min.getText());
		} catch (NumberFormatException ignored) {
			field_min.setTextColor(0xFF0000);
		}
		super.update();
	}

	@Override
	public void fillConfigs(Map<String,DataValue> configs) {
		putFloatConfig(configs,"length",length);
		putFloatConfig(configs,"r",r);
		putFloatConfig(configs,"g",g);
		putFloatConfig(configs,"b",b);
		putFloatConfig(configs,"minValue",min);
		putFloatConfig(configs,"maxValue",max);
		putFloatConfig(configs,"decimals",decimals);
		putFloatConfig(configs,"divisions",divisions);
		putFloatConfig(configs,"subdivisions",subdivisions);
	}

	public MeterHorizontalCSE(GuiControlEdit gui,Map<String,DataValue> map) {
		super(gui,map);
		length = (int)map.get("length").getNumber();
		r = (int)map.get("r").getNumber();
		g = (int)map.get("g").getNumber();
		b = (int)map.get("b").getNumber();
		min = map.get("minValue").getNumber();
		max = map.get("maxValue").getNumber();
		decimals = (int)map.get("decimals").getNumber();
		divisions = (int)map.get("divisions").getNumber();
		subdivisions = (int)map.get("subdivisions").getNumber();
	}

	List<GuiButton> buttons = new ArrayList<>();
	<T extends GuiButton> T addButton(T buttonIn) {
		gui.addButton(buttonIn);
		buttons.add(buttonIn);
		return buttonIn;
	}

	@Override
	public void initGui() {
		int cx = gui.width/2;
		slider_r = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70,
				80,15,TextFormatting.RED+"R ","",
				0,255,r,false,true
		));
		slider_g = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70+20,
				80,15,TextFormatting.GREEN+"G ","",
				0,255,g,false,true
		));
		slider_b = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70+40,
				80,15,TextFormatting.BLUE+"B ","",
				0,255,b,false,true
		));
		field_max = new GuiTextField(
				1000,gui.getFontRenderer(),
				cx-startXOffset+spacing,gui.getGuiTop()+70,
				80,15
		);
		field_min = new GuiTextField(
				1000,gui.getFontRenderer(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+20,
				80,15
		);
		field_max.setText(Float.toString(max));
		field_min.setText(Float.toString(min));
		slider_length = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+40,
				80,15,"Length ","",
				3,10,length,false,true
		));
		slider_decimals = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+60,
				80,15,"Decimals ","",
				0,3,decimals,false,true
		));
		slider_div = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+80,
				80,15,"Division ","",
				2,20,divisions,false,true
		));
		slider_subd = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+100,
				80,15,"Subdivision ","",
				0,10,subdivisions,false,true
		));
		super.initGui();
	}

	@Override
	protected void enableButtons(boolean enable) {
		for (GuiButton button : buttons) {
			button.visible = enable;
			button.enabled = enable;
		}
		field_max.setVisible(enable);
		field_max.setEnabled(enable);
		field_min.setVisible(enable);
		field_min.setEnabled(enable);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int[] getPreviewTransform() {
		return TRANSFORM;
	}
}
