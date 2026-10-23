// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes.utility;

import com.hbm.inventory.control_panel.NodeConnection;
import com.hbm.inventory.control_panel.NodeSystem;
import com.hbm.inventory.control_panel.nodes.Node;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.leafia.contents.machines.controlpanel.AddonNodesRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class NodeRNG extends Node {
	World world;
	public NodeRNG(float x,float y,World world) {
		super(x,y);
		this.world = world;
		outputs.add(new NodeConnection("Random",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		inputs.add(new NodeConnection("Min",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(0)));
		inputs.add(new NodeConnection("Max",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(1)));
		recalcSize();
		evalCache = new DataValue[1];
	}
	@Override
	public DataValue evaluate(int i) {
		if (cacheValid)
			return evalCache[0];
		DataValue min = inputs.get(0).evaluate();
		DataValue max = inputs.get(1).evaluate();
		if (min == null || max == null) return null;
		float value = Float.NaN;
		try {
			value = world.rand.nextInt((int)max.getNumber()-(int)min.getNumber()+1)+(int)min.getNumber();
		} catch (Exception ignored) {}
		evalCache[0] = new DataValueFloat(value);
		return evalCache[0];
	}
	@Override
	public float[] getColor() {
		return AddonNodesRegister.colorUtility;
	}
	@Override
	public String getDisplayName() {
		return "RNG";
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_rng");
		return super.writeToNBT(tag,sys);
	}
}
