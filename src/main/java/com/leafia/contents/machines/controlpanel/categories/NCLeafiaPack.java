// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.categories;

import com.hbm.inventory.control_panel.ItemList;
import com.hbm.inventory.control_panel.SubElementNodeEditor;
import com.hbm.inventory.control_panel.modular.INodeMenuCreator;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.nodes.pack.NodeIC10;

public class NCLeafiaPack implements INodeMenuCreator {
	@Override
	public Node selectItem(String s2,float x,float y,SubElementNodeEditor editor) {
		return switch(s2) {
			case "IC10 Sequence" -> new NodeIC10(x,y,editor.currentSystem.parent);
			default -> null;
		};
	}
	@Override
	public void addItems(ItemList list,float x,float y,SubElementNodeEditor editor) {
		list.addItems("IC10 Sequence");
	}
}
