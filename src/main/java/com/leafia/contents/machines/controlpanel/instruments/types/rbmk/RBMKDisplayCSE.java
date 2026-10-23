// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.rbmk;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public class RBMKDisplayCSE extends SubElementBaseConfig {
	private static final int[] TRANSFORM = { 132,112,88,88 };
	int width;
	int height;
	boolean offsetX;
	boolean offsetY;
	GuiSlider slider_width;
	GuiSlider slider_height;
	GuiCheckBox check_offsetX;
	GuiCheckBox check_offsetY;
	public RBMKDisplayCSE(GuiControlEdit gui,Map<String,DataValue> map) {
		super(gui,map);
		width = (int)map.get("width").getNumber();
		height = (int)map.get("height").getNumber();
		offsetX = map.get("offsetU").getBoolean();
		offsetY = map.get("offsetV").getBoolean();
	}
	@Override
	public void fillConfigs(Map<String,DataValue> configs) {
		putFloatConfig(configs,"width",width);
		putFloatConfig(configs,"height",height);
		putFloatConfig(configs,"offsetU",offsetX ? 1 : 0);
		putFloatConfig(configs,"offsetV",offsetY ? 1 : 0);
	}
	@Override
	public void initGui() {
		int cx = gui.width/2;
		slider_width = gui.addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-85,gui.getGuiTop()+70,
				80,15,"Width ","",
				1,27,width,false,true
		));
		slider_height = gui.addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-85,gui.getGuiTop()+90,
				80,15,"Height ","",
				1,27,width,false,true
		));
		check_offsetX = gui.addButton(new GuiCheckBox(
				gui.currentButtonId(),
				cx+10,gui.getGuiTop()+72,
				"Offset Tile X",offsetX
		));
		check_offsetY = gui.addButton(new GuiCheckBox(
				gui.currentButtonId(),
				cx+10,gui.getGuiTop()+92,
				"Offset Tile Y",offsetY
		));
		super.initGui();
	}
	@Override
	protected void mouseReleased(int mouseX,int mouseY,int state) {
		width = slider_width.getValueInt();
		height = slider_height.getValueInt();
		offsetX = check_offsetX.isChecked();
		offsetY = check_offsetY.isChecked();
	}
	@Override
	protected void enableButtons(boolean enable) {
		slider_width.visible = enable;
		slider_width.enabled = enable;
		slider_height.visible = enable;
		slider_height.enabled = enable;
		check_offsetX.visible = enable;
		check_offsetX.enabled = enable;
		check_offsetY.visible = enable;
		check_offsetY.enabled = enable;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public int[] getPreviewTransform() {
		return TRANSFORM;
	}
}
