package com.leafia.overwrite_contents.interfaces;

import com.hbm.inventory.control_panel.SubElement;
import com.leafia.contents.machines.controlpanel.ic10.SubElementIC10Editor;
import com.leafia.contents.machines.controlpanel.ic10.SubElementIC10Examples;
import com.leafia.contents.machines.controlpanel.ic10.SubElementIC10Funcs;
import com.leafia.contents.machines.controlpanel.ic10.SubElementIC10Warning;

public interface IMixinGuiControlEdit {
	SubElementIC10Editor leafia$ic10Editor();
	SubElementIC10Funcs leafia$ic10Funcs();
	SubElementIC10Examples leafia$ic10Examples();
	SubElementIC10Warning leafia$ic10Warning();
	void leafia$popElement(); // bruh protected access
	void leafia$pushElement(SubElement e); // bruh protected access
}
