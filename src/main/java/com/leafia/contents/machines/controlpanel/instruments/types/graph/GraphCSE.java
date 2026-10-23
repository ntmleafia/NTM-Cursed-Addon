// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.graph;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.leafia.contents.machines.controlpanel.instruments.types.graph.Graph.GraphSkin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class GraphCSE extends SubElementBaseConfig {
	private static final int[] TRANSFORM = { 60,172-5,44,44 };
	double width = 6;
	double height = 4;
	int verticalLineSpan = 1;
	int horizontalLineDivision = 10;
	int segments = 40;
	int decimalLength = 2;
	double textOffset = 0.5f;
	float red = 0;
	float green = 255;
	float blue = 0;
	String skin = "Console";
	@Override
	protected void drawScreen() {
		GlStateManager.disableLighting();
		gui.mc.getTextureManager().bindTexture(ResourceManager.white);
		NTMRenderHelper.drawGuiRectColor(gui.getGuiLeft()+20,gui.getGuiTop()+70,0,0,15,15,1,1,red/255,green/255,blue/255,1F);
		GlStateManager.enableLighting();
	}

	static final int spacing = 95;
	static final int startXOffset = 85;

	GuiSlider slider_red;
	GuiSlider slider_green;
	GuiSlider slider_blue;
	GuiSlider slider_width;
	GuiSlider slider_height;
	GuiSlider slider_segments;
	GuiSlider slider_vspan;
	GuiSlider slider_hdiv;
	GuiSlider slider_decimals;
	GuiSlider slider_textOffset;
	GuiButton button_skin;

	@Override
	protected void mouseReleased(int mouseX,int mouseY,int state) {
		red = slider_red.getValueInt();
		green = slider_green.getValueInt();
		blue = slider_blue.getValueInt();
		width = Math.round((float)slider_width.getValue()*10)/10f;
		height = Math.round((float)slider_height.getValue()*10)/10f;
		segments = slider_segments.getValueInt();
		verticalLineSpan = slider_vspan.getValueInt();
		horizontalLineDivision = slider_hdiv.getValueInt();
		decimalLength = slider_decimals.getValueInt();
		textOffset = Math.round((float)slider_textOffset.getValue()*10)/10f;
	}

	@Override
	public void fillConfigs(Map<String,DataValue> configs) {
		putFloatConfig(configs,"red",red);
		putFloatConfig(configs,"green",green);
		putFloatConfig(configs,"blue",blue);
		putStringConfig(configs,"skin",skin);
		putFloatConfig(configs,"segments",segments);
		putFloatConfig(configs,"verticalLineSpan",verticalLineSpan);
		putFloatConfig(configs,"horizontalLineDivision",horizontalLineDivision);
		putFloatConfig(configs,"decimalLength",decimalLength);
		putFloatConfig(configs,"textOffset",(float)textOffset);
		putFloatConfig(configs,"width",(float)width);
		putFloatConfig(configs,"height",(float)height);
	}

	public GraphCSE(GuiControlEdit gui,Map<String,DataValue> map) {
		super(gui,map);
		red = map.get("red").getNumber();
		green = map.get("green").getNumber();
		blue = map.get("blue").getNumber();
		skin = map.get("skin").toString();
		segments = (int)map.get("segments").getNumber();
		verticalLineSpan = (int)map.get("verticalLineSpan").getNumber();
		horizontalLineDivision = (int)map.get("horizontalLineDivision").getNumber();
		decimalLength = (int)map.get("decimalLength").getNumber();
		textOffset = Math.round(map.get("textOffset").getNumber()*10)/10d;
		width = Math.round(map.get("width").getNumber()*10)/10d;
		height = Math.round(map.get("height").getNumber()*10)/10d;
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
				0,255,red,false,true
		));
		slider_green = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70+20,
				80,15,TextFormatting.GREEN+"G ","",
				0,255,green,false,true
		));
		slider_blue = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70+40,
				80,15,TextFormatting.BLUE+"B ","",
				0,255,blue,false,true
		));
		slider_width = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70+60,
				80,15,"Width ","",
				1,10,width,true,true
		));
		slider_height = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset,gui.getGuiTop()+70+80,
				80,15,"Height ","",
				1,10,height,true,true
		));
		//////////////////////////////////////////////////////////////////////
		slider_segments = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70,
				80,15,"Segments ","",
				1,200,segments,false,true
		));
		slider_vspan = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+20,
				80,15,"X Line Span ","",
				1,20,verticalLineSpan,false,true
		));
		slider_hdiv = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+40,
				80,15,"Y Line Div ","",
				0,20,horizontalLineDivision,false,true
		));
		slider_decimals = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+60,
				80,15,"Decimals ","",
				0,5,decimalLength,false,true
		));
		slider_textOffset = addButton(new GuiSlider(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+80,
				80,15,"Txt Offs. ","",
				0,1,textOffset,true,true
		));
		button_skin = addButton(new GuiButton(
				gui.currentButtonId(),
				cx-startXOffset+spacing,gui.getGuiTop()+70+100,
				80,20,
				"Theme: "+skin
		));
		//////////////////////////////////////////////////////////////////////
		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button == button_skin) {
			int index = -1;
			int i = 0;
			for (GraphSkin s : GraphSkin.skins) {
				if (s.name.equals(skin)) {
					index = i;
					break;
				}
				i++;
			}
			index++;
			if (index >= GraphSkin.skins.size()) index = 0;
			skin = GraphSkin.skins.get(index).name;
			button_skin.displayString = "Theme: "+skin;
		}
	}

	@Override
	protected void enableButtons(boolean enable) {
		for (GuiButton button : buttons) {
			button.visible = enable;
			button.enabled = enable;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int[] getPreviewTransform() {
		return TRANSFORM;
	}
}
