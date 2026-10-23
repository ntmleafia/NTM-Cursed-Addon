// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.categories;

import com.hbm.inventory.control_panel.ItemList;
import com.hbm.inventory.control_panel.SubElementNodeEditor;
import com.hbm.inventory.control_panel.modular.INodeMenuCreator;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.nodes.bool.NodePulse;
import com.leafia.contents.machines.controlpanel.nodes.bool.NodeSRLatch;

public class NCLeafiaBoolean implements INodeMenuCreator {
	@Override
	public Node selectItem(String s,float x,float y,SubElementNodeEditor editor) {
		return switch(s) {
			case "Pulse" -> new NodePulse(x,y);
			case "SR Latch" -> new NodeSRLatch(x,y);
			default -> null;
		};
	}
	@Override
	public void addItems(ItemList itemList,float x,float y,SubElementNodeEditor editor) {
		itemList.addItems("Pulse");
		itemList.addItems("SR Latch");
	}
}
