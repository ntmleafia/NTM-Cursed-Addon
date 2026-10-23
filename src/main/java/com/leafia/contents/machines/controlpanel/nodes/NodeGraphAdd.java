// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.modular.StockNodesRegister;
import com.hbm.inventory.control_panel.nodes.NodeOutput;
import com.leafia.contents.machines.controlpanel.instruments.types.graph.Graph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public class NodeGraphAdd extends NodeOutput {
	public Control ctrl;
	public NodeGraphAdd(float x,float y,Control ctrl) {
		super(x,y);
		this.ctrl = ctrl;
		inputs.add(new NodeConnection("Value",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(0)));
		recalcSize();
	}
	@Override
	public boolean doOutput(IControllable iControllable,Map<String,NodeSystem> map,Map<String,BlockPos> list) {
		if (inputs.get(0) == null) return false;
		if (ctrl instanceof Graph graph)
			graph.pushValue(inputs.get(0).evaluate().getNumber());
		return false;
	}
	@Override
	public float[] getColor() {
		return StockNodesRegister.colorOutput;
	}
	@Override
	public String getDisplayName() {
		return "Graph Add";
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_graph");
		return super.writeToNBT(tag,sys);
	}
}
