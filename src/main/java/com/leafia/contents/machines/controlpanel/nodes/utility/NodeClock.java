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
import com.leafia.passive.LeafiaPassiveServer;
import net.minecraft.nbt.NBTTagCompound;

public class NodeClock extends Node {
	int internalCounter = 0;
	public NodeClock(float x,float y) {
		super(x,y);
		this.outputs.add(new NodeConnection("Clock Signal",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		this.inputs.add(new NodeConnection("Uptime",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(1)));
		this.inputs.add(new NodeConnection("Downtime",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(1)));
		evalCache = new DataValue[1];
		recalcSize();
	}
	@Override
	public DataValue evaluate(int i) {
		if (cacheValid)
			return evalCache[0];
		float value = 0;
		DataValue d0 = inputs.get(0).evaluate();
		DataValue d1 = inputs.get(1).evaluate();
		if (d0 == null || d1 == null) return null;
		cacheValid = true;
		int uptime = (int)(d0.getNumber());
		int downtime = (int)(d1.getNumber());
		if (uptime <= 0) return evalCache[0] = new DataValueFloat(0);
		if (downtime <= 0) return evalCache[0] = new DataValueFloat(1);
		int total = uptime+downtime;
		if (!LeafiaPassiveServer.tickedNodes.contains(this)) { // ???????????????????????????
			LeafiaPassiveServer.tickedNodes.add(this);
			internalCounter++;
		}
		internalCounter = Math.floorMod(internalCounter,total);
		if (internalCounter < uptime)
			value = 1;
		return evalCache[0] = new DataValueFloat(value);
	}
	@Override
	public float[] getColor() {
		return AddonNodesRegister.colorUtility;
	}
	@Override
	public String getDisplayName() {
		return "Clock";
	}
	@Override
	public void readFromNBT(NBTTagCompound tag,NodeSystem sys) {
		super.readFromNBT(tag,sys);
		internalCounter = tag.getInteger("counter");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_clock");
		tag.setInteger("counter",internalCounter);
		return super.writeToNBT(tag,sys);
	}
}
