package com.leafia.contents.machines.controlpanel.elements;

import com.hbm.inventory.control_panel.DataValue;
import com.hbm.inventory.control_panel.DataValue.DataType;
import com.hbm.inventory.control_panel.NodeConnection;
import com.hbm.inventory.control_panel.nodes.Node;
import org.jetbrains.annotations.NotNull;

// retarded node that's just offset upwards lmao
public class NodeRetarded extends NodeConnection {
	public NodeRetarded(String name,Node p,int idx,boolean isInput,DataType type,@NotNull DataValue defaultVal) {
		super(name,p,idx,isInput,type,defaultVal);
	}

	@Override
	public void resetOffset() {
		super.resetOffset();
		offsetY -= 8;
	}
}
