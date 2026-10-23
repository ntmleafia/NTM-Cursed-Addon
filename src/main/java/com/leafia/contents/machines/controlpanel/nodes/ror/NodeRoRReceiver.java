// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes.ror;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.types.DataValueString;
import com.hbm.inventory.control_panel.NodeConnection;
import com.hbm.inventory.control_panel.NodeSystem;
import com.hbm.inventory.control_panel.modular.StockNodesRegister;
import com.hbm.inventory.control_panel.nodes.Node;
import com.hbm.tileentity.network.RTTYSystem;
import com.hbm.tileentity.network.RTTYSystem.RTTYChannel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class NodeRoRReceiver extends Node {
	World world;
	String lastValue = "0";
	public NodeRoRReceiver(float x,float y,World world) {
		super(x,y);
		this.world = world;
		outputs.add(new NodeConnection("Signal",this,outputs.size(),false,DataType.STRING,new DataValueString("")));
		inputs.add(new NodeConnection("Frequency",this,inputs.size(),true,DataType.STRING,new DataValueString("")));
		recalcSize();
		evalCache = new DataValue[1];
	}
	@Override
	public DataValue evaluate(int i) {
		if (cacheValid)
			return evalCache[0];
		String value = lastValue;
		DataValue in = inputs.get(0).evaluate();
		if (in != null) {
			RTTYChannel chan = RTTYSystem.listen(world,in.toString());
			if (chan != null)
				value = ""+chan.signal;
		}
		lastValue = value;
		evalCache[0] = new DataValueString(value);
		cacheValid = true;
		return evalCache[0];
	}
	@Override
	public float[] getColor() {
		return StockNodesRegister.colorInput;
	}
	@Override
	public String getDisplayName() {
		return "Receive RoR";
	}
	@Override
	public void readFromNBT(NBTTagCompound tag,NodeSystem sys) {
		super.readFromNBT(tag,sys);
		lastValue = tag.getString("lastValue");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_ror_receiver");
		tag.setString("lastValue",lastValue);
		return super.writeToNBT(tag,sys);
	}
}
