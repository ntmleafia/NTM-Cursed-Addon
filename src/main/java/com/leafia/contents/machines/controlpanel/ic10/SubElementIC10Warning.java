package com.leafia.contents.machines.controlpanel.ic10;

import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.SubElement;
import com.leafia.overwrite_contents.interfaces.IMixinGuiControlEdit;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;

public class SubElementIC10Warning extends SubElement {
	public SubElementIC10Warning(GuiControlEdit gui) {
		super(gui);
	}
	public ArrayList<ArrayList<Object>> instruction;
	public GuiButton back;
	public GuiButton proceed;
	@Override
	public void initGui() {
		int cX = gui.width/2;
		int cY = gui.height/2;
		proceed = gui.addButton(new GuiButton(gui.currentButtonId(), cX-62, cY-15, 170, 20, "Proceed"));
		back = gui.addButton(new GuiButton(gui.currentButtonId(), cX-62, cY+15, 170, 20, "Wait no"));
		super.initGui();
	}
	@Override
	protected void enableButtons(boolean enable) {
		proceed.enabled = enable;
		proceed.visible = enable;
		back.enabled = enable;
		back.visible = enable;
	}
	@Override
	protected void actionPerformed(GuiButton button) {
		IMixinGuiControlEdit mixin = (IMixinGuiControlEdit)gui;
		mixin.leafia$popElement();
		mixin.leafia$popElement();
		if (button == proceed) {
			SubElementIC10Editor editor = mixin.leafia$ic10Editor();
			editor.instructions.clear();
			editor.instructions.addAll(instruction);
			instruction = null;
			editor.undoHistory.clear();
		}
	}
	@Override
	protected void drawScreen() {
		int cX = gui.width/2;
		int cY = gui.height/2;
		String text = "The current code will be lost!";
		gui.getFontRenderer().drawString(text,cX-gui.getFontRenderer().getStringWidth(text)/2F+22,cY-40,0xFF777777,false);
		text = "Are you sure want to continue?";
		gui.getFontRenderer().drawString(text,cX-gui.getFontRenderer().getStringWidth(text)/2F+22,cY-40-10,0xFF777777,false);
	}
}
