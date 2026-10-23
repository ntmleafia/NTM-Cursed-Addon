// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes.ror;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.modular.StockNodesRegister;
import com.hbm.inventory.control_panel.nodes.NodeOutput;
import com.hbm.tileentity.network.RTTYSystem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class NodeRoRSender extends NodeOutput {
	World world;
	public NodeRoRSender(float x,float y,World world) {
		super(x,y);
		this.world = world;
		inputs.add(new NodeConnection("Frequency",this,inputs.size(),true,DataType.STRING,new DataValueString("")));
		inputs.add(new NodeConnection("Signal",this,inputs.size(),true,DataType.STRING,new DataValueString("")));
		recalcSize();
		evalCache = new DataValue[1];
	}
	@Override
	public float[] getColor() {
		return StockNodesRegister.colorOutput;
	}
	@Override
	public String getDisplayName() {
		return "Transmit RoR";
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_ror_sender");
		return super.writeToNBT(tag,sys);
	}
	@Override
	public boolean doOutput(IControllable from,Map<String,NodeSystem> sendNodeMap,Map<String,BlockPos> positions) {
		DataValue freq = inputs.get(0).evaluate();
		DataValue signal = inputs.get(1).evaluate();
		if (signal != null && freq != null && world != null) {
			String s = signal.toString();
			if (signal instanceof DataValueFloat f)
				s = Integer.toString((int)f.getNumber());
			RTTYSystem.broadcast(world,freq.toString(),s);
		}
		return false;
	}
}
