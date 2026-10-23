// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel;

import com.hbm.inventory.control_panel.modular.NTMControlPanelRegistry;
import com.hbm.inventory.control_panel.modular.categories.NCStockLogic;
import com.leafia.contents.machines.controlpanel.categories.*;

import java.util.ArrayList;
import java.util.Collections;

public class AddonNodesRegister {
	public static final float[] colorString = new float[]{0.2F,0.8F,1};
	public static final float[] colorUtility = new float[]{1,0.614F,0.189F};
	public static void register() {
		if (!NTMControlPanelRegistry.addMenuCategories.contains("Text"))
			NTMControlPanelRegistry.addMenuCategories.add("Text");

		if (!NTMControlPanelRegistry.addMenuCategories.contains("Utility"))
			NTMControlPanelRegistry.addMenuCategories.add("Utility");

		NTMControlPanelRegistry.addMenuControl.putIfAbsent("Input",new ArrayList<>());
		NTMControlPanelRegistry.addMenuControl.get("Input").add(new NCLeafiaInput());

		NTMControlPanelRegistry.addMenuControl.putIfAbsent("Output",new ArrayList<>());
		NTMControlPanelRegistry.addMenuControl.get("Output").add(new NCLeafiaOutput());

		NTMControlPanelRegistry.addMenuControl.putIfAbsent("Boolean",new ArrayList<>());
		NTMControlPanelRegistry.addMenuControl.get("Boolean").add(new NCLeafiaBoolean());

		NTMControlPanelRegistry.addMenuControl.putIfAbsent("Pack",new ArrayList<>());
		NTMControlPanelRegistry.addMenuControl.get("Pack").add(new NCLeafiaPack());

		NTMControlPanelRegistry.addMenuControl.putIfAbsent("Text",new ArrayList<>());
		NTMControlPanelRegistry.addMenuControl.get("Text").add(new NCLeafiaText());

		NTMControlPanelRegistry.addMenuControl.putIfAbsent("Utility",new ArrayList<>());
		NTMControlPanelRegistry.addMenuControl.get("Utility").add(new NCLeafiaUtility());

		NTMControlPanelRegistry.nbtNodeLoaders.add(new AddonNodeLoader());
	}
}
