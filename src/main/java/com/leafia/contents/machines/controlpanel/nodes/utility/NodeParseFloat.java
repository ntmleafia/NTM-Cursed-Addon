// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes.utility;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.AddonNodesRegister;
import net.minecraft.nbt.NBTTagCompound;

public class NodeParseFloat extends Node {
	public NodeParseFloat(float x,float y) {
		super(x,y);
		outputs.add(new NodeConnection("Output",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		outputs.add(new NodeConnection("Is Error",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		inputs.add(new NodeConnection("Input",this,inputs.size(),true,DataType.STRING,new DataValueString("0")));
		recalcSize();
		evalCache = new DataValue[2];
	}
	@Override
	public DataValue evaluate(int i) {
		if (cacheValid)
			return evalCache[i];
		DataValue in = inputs.get(0).evaluate();
		if (in == null)
			return null;
		cacheValid = true;
		float value = 0;
		float error = 0;
		try {
			value = Float.parseFloat(in.toString());
		} catch (NumberFormatException ignored) {
			error = 1;
		}
		evalCache[0] = new DataValueFloat(value);
		evalCache[1] = new DataValueFloat(error);
		return evalCache[i];
	}
	@Override
	public float[] getColor() {
		return AddonNodesRegister.colorUtility;
	}
	@Override
	public String getDisplayName() {
		return "Text to Number";
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_parse_float");
		return super.writeToNBT(tag,sys);
	}
}
