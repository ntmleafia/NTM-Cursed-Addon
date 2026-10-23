// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.modular.StockNodesRegister;
import com.hbm.inventory.control_panel.nodes.Node;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeBulkQuery extends Node {
	public Control ctrl;

	public enum CombineMethod { ADD,MULTIPLY,AVERAGE,HIGHEST,LOWEST }
	public CombineMethod combineMethod = CombineMethod.ADD;
	public String dataName = "";
	public NodeDropdown dataSelector;

	public NodeBulkQuery(float x, float y, Control ctrl) {
		super(x, y);
		this.ctrl = ctrl;

		this.outputs.add(new NodeConnection("Output", this, outputs.size(), false, DataValue.DataType.GENERIC, new DataValueFloat(0)));

		NodeDropdown combineSelector = new NodeDropdown(this, otherElements.size(),s -> {
			try {
				combineMethod = CombineMethod.valueOf(s);
			} catch (IllegalArgumentException ignored) {}
			return null;
		}, () -> combineMethod.name());
		this.otherElements.add(combineSelector);
		for (CombineMethod value : CombineMethod.values())
			combineSelector.list.addItems(value.name());

		dataSelector = new NodeDropdown(this, otherElements.size(), s -> {
			dataName = s;
			this.outputs.get(0).type = evaluate(0).getType();
			return null;
		}, () -> dataName);
		setDataSelector();
		this.otherElements.add(dataSelector);

		dataName = "";
		recalcSize();
	}

	private void setDataSelector() {
		dataSelector.list.itemNames.clear();
		World world = ctrl.panel.parent.getControlWorld();
		Set<String> alreadyAdded = new HashSet<>();
		for (BlockPos pos : ctrl.taggedLinks.values()) {
			if (!world.isBlockLoaded(pos)) continue;
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof IControllable) {
				IControllable te = (IControllable) tile;
				for (Map.Entry<String, DataValue> var : te.getQueryData().entrySet()) {
					if (!alreadyAdded.contains(var.getKey())) {
						if (var.getValue() instanceof DataValueFloat)
							dataSelector.list.addItems(var.getKey());
						alreadyAdded.add(var.getKey());
					}
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","bulkQuery");
		tag.setString("combineMethod",combineMethod.name());
		tag.setString("dataName",dataName);

		return super.writeToNBT(tag,sys);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, NodeSystem sys) {
		try {
			combineMethod = CombineMethod.valueOf(tag.getString("combineMethod"));
		} catch (IllegalArgumentException ignored) {}
		dataName = tag.getString("dataName");
		setDataSelector();
		super.readFromNBT(tag, sys);
	}

	@Override
	public DataValue evaluate(int inx) {
		if (!dataName.isEmpty()) {
			float value = 0;
			if (combineMethod == CombineMethod.MULTIPLY)
				value = 1;
			int total = 0;
			boolean firstTime = true;
			World world = ctrl.panel.parent.getControlWorld();
			for (BlockPos pos : ctrl.taggedLinks.values()) {
				if (!world.isBlockLoaded(pos)) continue;
				if (!(world.getTileEntity(pos) instanceof IControllable te)) continue;
				DataValue data = te.getQueryData().get(dataName);
				if (data instanceof DataValueFloat) {
					float curValue = data.getNumber();
					switch(combineMethod) {
						case ADD -> value += curValue;
						case MULTIPLY -> value *= curValue;
						case AVERAGE -> {
							value += curValue;
							total++;
						} case HIGHEST -> {
							if (firstTime) value = curValue;
							else value = Math.max(curValue,value);
							firstTime = false;
						} case LOWEST -> {
							if (firstTime) value = curValue;
							else value = Math.min(curValue,value);
							firstTime = false;
						}
					}
				}
			}
			if (total > 0 && combineMethod == CombineMethod.AVERAGE)
				value /= total;
			return new DataValueFloat(value);
		}
		return new DataValueFloat(0);
	}

	@Override
	public float[] getColor() {
		return StockNodesRegister.colorInput;
	}

	@Override
	public String getDisplayName() {
		return "Bulk Query";
	}
}
