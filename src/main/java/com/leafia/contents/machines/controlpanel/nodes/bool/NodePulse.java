// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes.bool;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.modular.StockNodesRegister;
import com.hbm.inventory.control_panel.nodes.Node;
import net.minecraft.nbt.NBTTagCompound;

public class NodePulse extends Node {
	enum PulseMode { OFF_ON,ON_OFF,ALWAYS }
	boolean wasOn = false;
	PulseMode mode = PulseMode.OFF_ON;
	public NodePulse(float x,float y) {
		super(x,y);
		NodeDropdown modeSelector = new NodeDropdown(this,otherElements.size(),s->{
			try {
				mode = PulseMode.valueOf(s.replace(" -> ","_"));
			} catch (IllegalArgumentException ignored) {}
			return null;
		},()->mode.name().replace("_"," -> "));
		for (PulseMode value : PulseMode.values())
			modeSelector.list.addItems(value.name().replace("_"," -> "));
		otherElements.add(modeSelector);
		this.outputs.add(new NodeConnection("Pulse",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		this.inputs.add(new NodeConnection("Input",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(1)));
		evalCache = new DataValue[1];
		recalcSize();
	}
	@Override
	public DataValue evaluate(int i) {
		if (cacheValid)
			return evalCache[0];
		DataValue d0 = inputs.get(0).evaluate();
		if (d0 != null) {
			cacheValid = true;
			boolean value = d0.getBoolean();
			float output = 0;
			if (value && !wasOn) {
				if (mode == PulseMode.ALWAYS || mode == PulseMode.OFF_ON)
					output = 1;
			} else if (!value && wasOn) {
				if (mode == PulseMode.ALWAYS || mode == PulseMode.ON_OFF)
					output = 1;
			}
			wasOn = value;
			return evalCache[0] = new DataValueFloat(output);
		}
		return null;
	}
	@Override
	public float[] getColor() {
		return StockNodesRegister.colorBoolean;
	}
	@Override
	public String getDisplayName() {
		return "Pulse";
	}
	@Override
	public void readFromNBT(NBTTagCompound tag,NodeSystem sys) {
		super.readFromNBT(tag,sys);
		wasOn = tag.getBoolean("wasOn");
		try {
			mode = PulseMode.valueOf(tag.getString("mode"));
		} catch (IllegalArgumentException ignored) { }
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_pulse");
		tag.setBoolean("wasOn",wasOn);
		tag.setString("mode",mode.name());
		return super.writeToNBT(tag,sys);
	}
}
