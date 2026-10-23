// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.categories;

import com.hbm.inventory.control_panel.ItemList;
import com.hbm.inventory.control_panel.SubElementNodeEditor;
import com.hbm.inventory.control_panel.modular.INodeMenuCreator;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.instruments.types.graph.Graph;
import com.leafia.contents.machines.controlpanel.nodes.NodeGraphAdd;
import com.leafia.contents.machines.controlpanel.nodes.NodeSounder;
import com.leafia.contents.machines.controlpanel.nodes.ror.NodeRoRSender;

public class NCLeafiaOutput implements INodeMenuCreator {
	@Override
	public Node selectItem(String s2,float x,float y,SubElementNodeEditor editor) {
		return switch(s2) {
			case "Play Sound" -> new NodeSounder(x,y);
			case "Graph Add" -> new NodeGraphAdd(x,y,editor.currentSystem.parent);
			case "Transmit RoR" -> new NodeRoRSender(x,y,editor.currentSystem.parent.panel.parent.getControlWorld());
			default -> null;
		};
	}
	@Override
	public void addItems(ItemList list,float x,float y,SubElementNodeEditor editor) {
		list.addItems("Play Sound");
		if (editor.currentSystem.parent instanceof Graph)
			list.addItems("Graph Add");
		list.addItems("Transmit RoR");
	}
}
