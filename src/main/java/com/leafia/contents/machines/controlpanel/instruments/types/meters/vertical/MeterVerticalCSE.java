// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.meters.vertical;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.leafia.contents.machines.controlpanel.instruments.types.graph.Graph.GraphSkin;
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

public class MeterVerticalCSE extends SubElementBaseConfig {
	private static final int[] TRANSFORM = { 40,112,88,88 };
	@Override
	protected void drawScreen() {
		GlStateManager.disableLighting();
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

	int needleOffset = 0;
	float max = 1;
	float min = -1;
	int divisions = 20;
	int subdivisions = 3;
	int decimals = 1;
	boolean clamp = false;

	GuiSlider slider_needleOffset;
	GuiTextField field_max;
	GuiTextField field_min;
	GuiSlider slider_div;
	GuiSlider slider_subd;
	GuiSlider slider_decimals;
	GuiCheckBox check_clamp;

	@Override
	protected void mouseReleased(int mouseX,int mouseY,int state) {
		divisions = slider_div.getValueInt();
		subdivisions = slider_subd.getValueInt();
		decimals = slider_decimals.getValueInt();
		clamp = check_clamp.isChecked();
	}

	@Override
	protected void mouseClicked(int mouseX,int mouseY,int button) {
		field_max.mouseClicked(mouseX,mouseY,button);
		field_min.mouseClicked(mouseX,mouseY,button);
		super.mouseClicked(mouseX,mouseY,button);
	}

	@Override
	protected void update() {
		needleOffset = slider_needleOffset.getValueInt();
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
		putFloatConfig(configs,"needleOffset",needleOffset);
		putFloatConfig(configs,"minValue",min);
		putFloatConfig(configs,"maxValue",max);
		putFloatConfig(configs,"decimals",decimals);
		putFloatConfig(configs,"divisions",divisions);
		putFloatConfig(configs,"subdivisions",subdivisions);
		putFloatConfig(configs,"clamp",clamp ? 1 : 0);
	}

	public MeterVerticalCSE(GuiControlEdit gui,Map<String,DataValue> map) {
		super(gui,map);
		needleOffset = (int)map.get("needleOffset").getNumber();
		min = map.get("minValue").getNumber();
		max = map.get("maxValue").getNumber();
		decimals = (int)map.get("decimals").getNumber();
		divisions = (int)map.get("divisions").getNumber();
		subdivisions = (int)map.get("subdivisions").getNumber();
		clamp = map.get("clamp").getBoolean();
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
		field_max = new GuiTextField(
				1000,gui.getFontRenderer(),
				cx-startXOffset,gui.getGuiTop()+70,
				80,15
		);
		field_min = new GuiTextField(
				1000,gui.getFontRenderer(),
				cx-startXOffset,gui.getGuiTop()+70+20,
				80,15
		);
		field_max.setText(Float.toString(max));
		field_min.setText(Float.toString(min));
		slider_needleOffset = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70,
				80,15,"Offset ","",
				-3,3,needleOffset,false,true
		));
		slider_decimals = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+20,
				80,15,"Decimals ","",
				0,3,decimals,false,true
		));
		slider_div = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+40,
				80,15,"Division ","",
				5,30,divisions,false,true
		));
		slider_subd = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+60,
				80,15,"Subdivision ","",
				0,10,subdivisions,false,true
		));
		check_clamp = addButton(new GuiCheckBox(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+80+2,
				"Clamp",clamp
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
