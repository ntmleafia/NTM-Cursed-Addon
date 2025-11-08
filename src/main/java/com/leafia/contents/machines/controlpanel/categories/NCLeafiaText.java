package com.leafia.contents.machines.controlpanel.categories;

import com.hbm.inventory.control_panel.ItemList;
import com.hbm.inventory.control_panel.SubElementNodeEditor;
import com.hbm.inventory.control_panel.modular.INodeMenuCreator;
import com.hbm.inventory.control_panel.nodes.Node;
import com.hbm.inventory.control_panel.nodes.NodeCancelEvent;
import com.hbm.inventory.control_panel.nodes.NodeEventBroadcast;
import com.hbm.inventory.control_panel.nodes.NodeSetVar;
import com.leafia.contents.machines.controlpanel.nodes.NodeAddString;
import com.leafia.contents.machines.controlpanel.nodes.NodeFormat;
import com.leafia.contents.machines.controlpanel.nodes.NodeSIPfx;
import com.leafia.contents.machines.controlpanel.nodes.NodeSubString;

public class NCLeafiaText implements INodeMenuCreator {
	@Override
	public Node selectItem(String s2,float x,float y,SubElementNodeEditor editor) {
		if(s2.equals("Add String")){
			return new NodeAddString(x, y);
		} else if(s2.equals("Substring")){
			return new NodeSubString(x, y);
		} else if(s2.equals("SIPfx")){
			return new NodeSIPfx(x, y);
		} else if(s2.equals("Format")){
			return new NodeFormat(x, y);
		}
		return null;
	}
	@Override
	public void addItems(ItemList list,float x,float y,SubElementNodeEditor editor) {
		list.addItems("Add String");
		list.addItems("Substring");
		list.addItems("SIPfx");
		list.addItems("Format");
	}
}
