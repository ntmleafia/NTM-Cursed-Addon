// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes.bool;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.inventory.control_panel.NodeConnection;
import com.hbm.inventory.control_panel.NodeSystem;
import com.hbm.inventory.control_panel.modular.StockNodesRegister;
import com.hbm.inventory.control_panel.nodes.Node;
import net.minecraft.nbt.NBTTagCompound;

public class NodeSRLatch extends Node {
	boolean state = false;
	public NodeSRLatch(float x,float y) {
		super(x,y);
		this.outputs.add(new NodeConnection("Output",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		this.inputs.add(new NodeConnection("Set",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(0)));
		this.inputs.add(new NodeConnection("Reset",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(0)));
		recalcSize();
	}
	@Override
	public DataValue evaluate(int i) {
		DataValue set = inputs.get(0).evaluate();
		DataValue reset = inputs.get(1).evaluate();
		if (set != null && set.getBoolean())
			state = true;
		if (reset != null && reset.getBoolean())
			state = false;
		return new DataValueFloat(state ? 1 : 0);
	}
	@Override
	public float[] getColor() {
		return StockNodesRegister.colorBoolean;
	}
	@Override
	public String getDisplayName() {
		return "SR Latch";
	}
	@Override
	public void readFromNBT(NBTTagCompound tag,NodeSystem sys) {
		super.readFromNBT(tag,sys);
		state = tag.getBoolean("state");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_sr_latch");
		tag.setBoolean("state",state);
		return super.writeToNBT(tag,sys);
	}
}
