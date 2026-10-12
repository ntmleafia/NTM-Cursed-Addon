package com.leafia.overwrite_contents.interfaces;

import com.hbm.inventory.control_panel.SubElement;
import com.leafia.contents.machines.controlpanel.ic10.SubElementIC10Editor;
import com.leafia.contents.machines.controlpanel.ic10.SubElementIC10Funcs;

public interface IMixinGuiControlEdit {
	SubElementIC10Editor leafia$ic10Editor();
	SubElementIC10Funcs leafia$ic10Funcs();
	void leafia$popElement(); // bruh protected access
	void leafia$pushElement(SubElement e); // bruh protected access
}
