package com.leafia.contents.machines.controlpanel;

import com.hbm.inventory.control_panel.modular.NTMControlPanelRegistry;
import com.hbm.inventory.control_panel.modular.categories.NCStockLogic;
import com.leafia.contents.machines.controlpanel.categories.NCLeafiaOutput;
import com.leafia.contents.machines.controlpanel.categories.NCLeafiaText;

import java.util.ArrayList;
import java.util.Collections;

public class AddonNodesRegister {
	public static final float[] colorString = new float[]{0.2F,0.8F,1};
	public static void register() {
		if (!NTMControlPanelRegistry.addMenuCategories.contains("Text"))
			NTMControlPanelRegistry.addMenuCategories.add("Text");
		NTMControlPanelRegistry.addMenuControl.putIfAbsent("Output",new ArrayList<>());
		NTMControlPanelRegistry.addMenuControl.get("Output").add(new NCLeafiaOutput());
		NTMControlPanelRegistry.addMenuControl.putIfAbsent("Text",new ArrayList<>());
		NTMControlPanelRegistry.addMenuControl.get("Text").add(new NCLeafiaText());
		NTMControlPanelRegistry.nbtNodeLoaders.add(new AddonNodeLoader());
	}
}
