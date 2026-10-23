// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.categories;

import com.hbm.inventory.control_panel.ItemList;
import com.hbm.inventory.control_panel.SubElementNodeEditor;
import com.hbm.inventory.control_panel.modular.INodeMenuCreator;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.nodes.utility.*;

public class NCLeafiaUtility implements INodeMenuCreator {
	@Override
	public Node selectItem(String s,float x,float y,SubElementNodeEditor editor) {
		return switch(s) {
			case "Cache" -> new NodeCache(x,y);
			case "Clock" -> new NodeClock(x,y);
			case "Summarizer" -> new NodeSummarizer(x,y);
			case "Text to Number" -> new NodeParseFloat(x,y);
			case "RNG" -> new NodeRNG(x,y,editor.currentSystem.parent.panel.parent.getControlWorld());
			case "Decimal RNG" -> new NodeRNGDecimal(x,y,editor.currentSystem.parent.panel.parent.getControlWorld());
			default -> null;
		};
	}
	@Override
	public void addItems(ItemList itemList,float x,float y,SubElementNodeEditor editor) {
		itemList.addItems("Cache");
		itemList.addItems("Clock");
		itemList.addItems("RNG");
		itemList.addItems("Decimal RNG");
		itemList.addItems("Summarizer");
		itemList.addItems("Text to Number");
	}
}
