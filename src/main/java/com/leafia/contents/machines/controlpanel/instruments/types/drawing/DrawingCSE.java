// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.drawing;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.leafia.contents.machines.controlpanel.instruments.types.graph.Graph.GraphSkin;
import net.minecraft.client.Minecraft;
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

public class DrawingCSE extends SubElementBaseConfig {
	private static final int[] TRANSFORM = { 132,112,88,88 };
	double width = 0.2;
	double height = 0.2;
	int red = 100;
	int green = 100;
	int blue = 100;
	int scale = 25;
	boolean whiteText = false;
	String text = "";
	@Override
	protected void drawScreen() {
		GlStateManager.disableLighting();
		gui.mc.getTextureManager().bindTexture(ResourceManager.white);
		NTMRenderHelper.drawGuiRectColor(gui.getGuiLeft()+20,gui.getGuiTop()+70,0,0,15,15,1,1,red/100f,green/100f,blue/100f,1F);
		GlStateManager.enableLighting();
		textField.drawTextBox();
	}

	static final int spacing = 95;
	static final int startXOffset = 85;

	GuiSlider slider_red;
	GuiSlider slider_green;
	GuiSlider slider_blue;
	GuiSlider slider_width;
	GuiSlider slider_height;
	GuiSlider slider_scale;
	GuiCheckBox check_white;
	GuiTextField textField;

	@Override
	protected void mouseReleased(int mouseX,int mouseY,int state) {
		red = slider_red.getValueInt();
		green = slider_green.getValueInt();
		blue = slider_blue.getValueInt();
		width = Math.round((float)slider_width.getValue()*10)/10d;
		height = Math.round((float)slider_height.getValue()*10)/10d;
		scale = slider_scale.getValueInt();
		whiteText = check_white.isChecked();
	}

	@Override
	protected void update() {
		super.update();
		textField.updateCursorCounter();
		text = textField.getText();
	}

	@Override
	public void fillConfigs(Map<String,DataValue> configs) {
		putFloatConfig(configs,"red",red);
		putFloatConfig(configs,"green",green);
		putFloatConfig(configs,"blue",blue);
		putFloatConfig(configs,"width",(float)width);
		putFloatConfig(configs,"height",(float)height);
		putFloatConfig(configs,"white",whiteText ? 1 : 0);
		putFloatConfig(configs,"scale",scale);
		putStringConfig(configs,"text",text);
	}

	public DrawingCSE(GuiControlEdit gui,Map<String,DataValue> map) {
		super(gui,map);
		red = (int)map.get("red").getNumber();
		green = (int)map.get("green").getNumber();
		blue = (int)map.get("blue").getNumber();
		width = Math.round(map.get("width").getNumber()*10)/10d;
		height = Math.round(map.get("height").getNumber()*10)/10d;
		scale = (int)map.get("scale").getNumber();
		whiteText = map.get("white").getBoolean();
		text = map.get("text").toString();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		this.textField.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		super.keyTyped(typedChar, keyCode);
		this.textField.textboxKeyTyped(typedChar, keyCode);
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
		slider_red = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70,
				80,15,TextFormatting.RED+"R ","",
				0,100,red,false,true
		));
		slider_green = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70+20,
				80,15,TextFormatting.GREEN+"G ","",
				0,100,green,false,true
		));
		slider_blue = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70+40,
				80,15,TextFormatting.BLUE+"B ","",
				0,100,blue,false,true
		));
		slider_width = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70,
				80,15,"Width ","",
				0.1,10,width,true,true
		));
		slider_height = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+20,
				80,15,"Height ","",
				0.1,10,height,true,true
		));
		slider_scale = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+140,
				80,15,"Text Scale ","",
				10,100,scale,false,true
		));
		check_white = addButton(new GuiCheckBox(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+140+20+35+2,
				"White Text",whiteText
		));
		textField = new GuiTextField(gui.currentButtonId(), Minecraft.getMinecraft().fontRenderer, cx-startXOffset-20, gui.getGuiTop()+140+20, 100, 30);
		textField.setText(text);
		//////////////////////////////////////////////////////////////////////
		super.initGui();
	}

	@Override
	protected void enableButtons(boolean enable) {
		for (GuiButton button : buttons) {
			button.visible = enable;
			button.enabled = enable;
		}
		textField.setEnabled(enable);
		textField.setVisible(enable);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int[] getPreviewTransform() {
		return TRANSFORM;
	}
}
