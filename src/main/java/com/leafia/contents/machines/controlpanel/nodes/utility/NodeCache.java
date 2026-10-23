// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes.utility;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.inventory.control_panel.NodeConnection;
import com.hbm.inventory.control_panel.NodeSystem;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.AddonNodesRegister;
import net.minecraft.nbt.NBTTagCompound;

public class NodeCache extends Node {
	public NodeCache(float x,float y) {
		super(x,y);
		this.outputs.add(new NodeConnection("Output",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		this.inputs.add(new NodeConnection("Input",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(0)));
		evalCache = new DataValue[1];
		recalcSize();
	}
	@Override
	public DataValue evaluate(int i) {
		if (inputs.get(0) == null) return null;
		if (cacheValid)
			return evalCache[0];
		DataValue value = inputs.get(0).evaluate();
		evalCache[0] = value;
		cacheValid = true;
		return value;
	}
	@Override
	public float[] getColor() {
		return AddonNodesRegister.colorUtility;
	}
	@Override
	public String getDisplayName() {
		return "Cache";
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_cache");
		return super.writeToNBT(tag,sys);
	}
}
