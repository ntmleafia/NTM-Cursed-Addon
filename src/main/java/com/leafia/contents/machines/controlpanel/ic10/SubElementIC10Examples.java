package com.leafia.contents.machines.controlpanel.ic10;

import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.SubElement;
import com.leafia.overwrite_contents.interfaces.IMixinGuiControlEdit;
import net.minecraft.client.gui.GuiButton;

import java.util.*;
import java.util.Map.Entry;

public class SubElementIC10Examples extends SubElement {
	public static final Map<String,ArrayList<ArrayList<Object>>> examples = new HashMap<>();
	static <T> ArrayList<T> asArrayList(T ...a) {
		return new ArrayList<>(Arrays.asList(a));
	}
	static {
		examples.put("3 Second Clock",asArrayList(
				asArrayList("alias",1,"timer",1,"r0"),
				asArrayList("alias",1,"on",1,"r1"),
				asArrayList(),
				asArrayList("#"," reset variables"),
				asArrayList("move",1,"timer",1,"0"),
				asArrayList("move",1,"on",1,"0"),
				asArrayList(),
				asArrayList("Loop:"),
				asArrayList(2,"#"," check if timer equals 3"),
				asArrayList(2,"seq",1,"on",1,"timer",1,"3"),
				asArrayList(2,"s",1,"\"signal\"",1,"on"),
				asArrayList(2),
				asArrayList(2,"#"," adds timer 1 and modulo by 4"),
				asArrayList(2,"#"," (1mod4=1, 2mod4=2, 3mod4=3, 4mod4=0, 5mod4=1, 6mod4=2...)"),
				asArrayList(2,"add",1,"timer",1,"timer",1,"1"),
				asArrayList(2,"mod",1,"timer",1,"timer",1,"4"),
				asArrayList(2),
				asArrayList(2,"#"," 1 second = 2 stationeers ticks"),
				asArrayList(2,"yield"),
				asArrayList(2,"yield"),
				asArrayList(2,"j",1,"Loop")
		));
		examples.put("3 Second Capacitor",asArrayList(
				asArrayList("alias",1,"timer",1,"r0"),
				asArrayList("alias",1,"input",1,"r1"),
				asArrayList("alias",1,"output",1,"r2"),
				asArrayList("define",1,"duration",1,"3",1,"#"," 3 seconds"),
				asArrayList(),
				asArrayList("#"," reset variables"),
				asArrayList("move",1,"timer",1,"0"),
				asArrayList("move",1,"input",1,"0"),
				asArrayList("move",1,"output",1,"0"),
				asArrayList(),
				asArrayList("Loop:"),
				asArrayList(2,"ln",1,"input",1,"\"signal\"",1,"#"," read port \"signal\""),
				asArrayList(2,"max",1,"output",1,"input",1,"output"),
				asArrayList(2,"beqz",1,"output",1,"Idle",1,"#"," if output is off"),
				asArrayList(2,"beqz",1,"input",1,"IfOff",1,"#"," if input is off but output is on"),
				asArrayList("Idle:",1,"#"," if is on, or is off but 3 seconds had already passed"),
				asArrayList(2,"move",1,"timer",1,"0"),
				asArrayList(2,"move",1,"output",1,"input",1,"#"," turn off output if input is off"),
				asArrayList(2,"s",1,"\"signal\"",1,"output",1,"#"," apply to port \"signal\""),
				asArrayList(2,"sleep",1,"1",1,"#"," wait 1 minecraft tick"),
				asArrayList(2,"j",1,"Loop"),
				asArrayList("IfOff:",1,"#"," during the 3 seconds after being turned off"),
				asArrayList(2,"bge",1,"timer",1,"duration",1,"Idle",1,"#"," if timer has reached 3 seconds"),
				asArrayList(2),
				asArrayList(2,"add",1,"timer",1,"timer",1,"0.5"),
				asArrayList(2,"s",1,"\"signal\"",1,"output",1,"#"," apply to port \"signal\""),
				asArrayList(2,"yield",1,"#"," wait 1 stationeers tick (= 0.5 seconds)"),
				asArrayList(2,"j",1,"Loop")
		));
		examples.put("Simple NOT Gate",asArrayList(
				asArrayList("Loop:"),
				asArrayList(2,"ln",1,"r0",1,"\"signal\"",1,"#"," read port \"signal\" into register0"),
				asArrayList(2),
				asArrayList(2,"#"," do the NOT operation"),
				asArrayList(2,"seq",1,"r0",1,"r0",1,"0",1,"#"," set register0 = 1 if 0,1 otherwise"),
				asArrayList(2),
				asArrayList(2,"s",1,"\"signal\"",1,"r0",1,"#"," apply register0 to port \"signal\""),
				asArrayList(2,"sleep",1,"1",1,"#"," wait 1 minecraft tick"),
				asArrayList(2,"j",1,"Loop",1,"#"," jump to start")
		));
		examples.put("Simple RoR NOT Gate",asArrayList(
				asArrayList("Loop:"),
				asArrayList(2,"lnr",1,"r0",1,"\"meow\"",1,"#"," read RoR signal \"meow\" into register0"),
				asArrayList(2),
				asArrayList(2,"#"," do the NOT operation"),
				asArrayList(2,"seq",1,"r0",1,"r0",1,"0",1,"#"," set register0 = 1 if 0,1 otherwise"),
				asArrayList(2),
				asArrayList(2,"sr",1,"\"mrow\"",1,"r0",1,"#"," apply register0 to RoR freq \"mrow\""),
				asArrayList(2,"sleep",1,"1",1,"#"," wait 1 minecraft tick"),
				asArrayList(2,"j",1,"Loop",1,"#"," jump to start")
		));
	}
	public SubElementIC10Examples(GuiControlEdit gui) {
		super(gui);
	}
	public GuiButton back;
	public List<GuiButton> buttons = new ArrayList<>();
	@Override
	public void initGui() {
		int cX = gui.width/2;
		int cY = gui.height/2;
		back = gui.addButton(new GuiButton(gui.currentButtonId(), gui.getGuiLeft()+7, gui.getGuiTop()+13, 30, 20, "Back"));
		buttons.clear();
		int i = 0;
		for (Entry<String,ArrayList<ArrayList<Object>>> entry : examples.entrySet()) {
			int offset = i*25;
			buttons.add(gui.addButton(new GuiButton(i+1000,cX-62,cY-100+offset,170,20,entry.getKey())));
			i++;
		}
		super.initGui();
	}
	@Override
	protected void enableButtons(boolean enable) {
		back.enabled = enable;
		back.visible = enable;
		for (GuiButton button : buttons) {
			button.enabled = enable;
			button.visible = enable;
		}
	}
	public ArrayList<ArrayList<Object>> deepCopy(ArrayList<ArrayList<Object>> insn) {
		ArrayList<ArrayList<Object>> out = new ArrayList<>();
		for (ArrayList<Object> insns : insn)
			out.add(new ArrayList<>(insns));
		return out;
	}
	@Override
	protected void actionPerformed(GuiButton button) {
		IMixinGuiControlEdit mixin = (IMixinGuiControlEdit)gui;
		if(button == back)
			mixin.leafia$popElement();
		else {
			ArrayList<ArrayList<Object>> insn = examples.get(button.displayString);
			if (insn != null) {
				mixin.leafia$ic10Warning().instruction = deepCopy(insn);
				mixin.leafia$pushElement(mixin.leafia$ic10Warning());
			}
		}
	}
	@Override
	protected void drawScreen() {
		int cX = gui.width/2;
		int cY = gui.height/2;
		String text = "Example Codes";
		gui.getFontRenderer().drawString(text,cX-gui.getFontRenderer().getStringWidth(text)/2F+22,cY-112,0xFF777777,false);
	}
}
