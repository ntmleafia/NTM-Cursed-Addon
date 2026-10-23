// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments;

import com.hbm.inventory.control_panel.Control;
import com.hbm.inventory.control_panel.ControlRegistry;
import com.leafia.contents.machines.controlpanel.instruments.types.drawing.Drawing;
import com.leafia.contents.machines.controlpanel.instruments.types.graph.Graph;
import com.leafia.contents.machines.controlpanel.instruments.types.lever.LeverHorizontal;
import com.leafia.contents.machines.controlpanel.instruments.types.lever.LeverVertical;
import com.leafia.contents.machines.controlpanel.instruments.types.meters.horizontal.MeterHorizontal;
import com.leafia.contents.machines.controlpanel.instruments.types.meters.vertical.MeterVertical;
import com.leafia.contents.machines.controlpanel.instruments.types.nixie.NixieDisplay;
import com.leafia.contents.machines.controlpanel.instruments.types.rbmk.RBMKDisplay;
import com.leafia.contents.machines.controlpanel.instruments.types.starbound.LargeButton;
import com.leafia.contents.machines.controlpanel.instruments.types.starbound.LargeSwitch;
import com.leafia.contents.machines.controlpanel.instruments.types.textindicator.TextIndicator;

import java.util.List;

public class AddonInstrumentRegister {
	static final List<Control> reg = ControlRegistry.addonControls;
	public static void register() {
		reg.add(new NixieDisplay("Nixie Tube","leafia_nixie",null));
		reg.add(new LargeSwitch("Large Switch","leafia_sbswitch",null));
		reg.add(new LargeButton("Large Button","leafia_sbbutton",null));
		reg.add(new TextIndicator("Text Indicator","leafia_textindicator",null));
		reg.add(new RBMKDisplay("RBMK Display","leafia_rbmk",null));
		reg.add(new Graph("Graph","leafia_graph",null));
		reg.add(new MeterHorizontal("Horizontal Meter","leafia_meter_h",null));
		reg.add(new MeterVertical("Vertical Meter","leafia_meter_v",null));
		reg.add(new Drawing("Boxed Label","leafia_drawing_box",null));
		reg.add(new LeverHorizontal("Horizontal Lever","leafia_lever_h",null));
		reg.add(new LeverVertical("Vertical Lever","leafia_lever_v",null));
	}
}
