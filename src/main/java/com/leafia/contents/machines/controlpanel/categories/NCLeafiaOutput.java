package com.leafia.contents.machines.controlpanel.categories;

import com.hbm.inventory.control_panel.ItemList;
import com.hbm.inventory.control_panel.SubElementNodeEditor;
import com.hbm.inventory.control_panel.modular.INodeMenuCreator;
import com.hbm.inventory.control_panel.nodes.Node;
import com.hbm.inventory.control_panel.nodes.NodeCancelEvent;
import com.hbm.inventory.control_panel.nodes.NodeEventBroadcast;
import com.hbm.inventory.control_panel.nodes.NodeSetVar;
import com.leafia.contents.machines.controlpanel.nodes.NodeSounder;

public class NCLeafiaOutput implements INodeMenuCreator {
	@Override
	public Node selectItem(String s2,float x,float y,SubElementNodeEditor editor) {
		if(s2.equals("Play Sound")){
			return new NodeSounder(x,y);
		}
		return null;
	}
	@Override
	public void addItems(ItemList list,float x,float y,SubElementNodeEditor editor) {
		list.addItems("Play Sound");
	}
}
