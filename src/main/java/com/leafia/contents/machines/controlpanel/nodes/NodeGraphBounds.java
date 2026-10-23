// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.modular.StockNodesRegister;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.instruments.types.graph.Graph;
import net.minecraft.nbt.NBTTagCompound;

public class NodeGraphBounds extends Node {
	public Control ctrl;
	public NodeGraphBounds(float x,float y,Control ctrl) {
		super(x,y);
		this.ctrl = ctrl;
		outputs.add(new NodeConnection("Highest",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(1)));
		outputs.add(new NodeConnection("Lowest",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		recalcSize();
	}
	@Override
	public DataValue evaluate(int i) {
		if (ctrl instanceof Graph graph) {
			Double max = null;
			Double min = null;
			for (double value : graph.values) {
				if (max == null || value > max)
					max = value;
				if (min == null || value < min)
					min = value;
			}
			if (max == null) max = 0d;
			if (min == null) min = 0d;
			if (i == 0)
				return new DataValueFloat((float)(double)max);
			else if (i == 1)
				return new DataValueFloat((float)(double)min);
		}
		return new DataValueFloat(0);
	}
	@Override
	public float[] getColor() {
		return StockNodesRegister.colorInput;
	}
	@Override
	public String getDisplayName() {
		return "Graph Bounds";
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_graph_bounds");
		return super.writeToNBT(tag,sys);
	}
}
