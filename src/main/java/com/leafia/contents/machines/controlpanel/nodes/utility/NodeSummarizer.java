// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.nodes.utility;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.AddonNodesRegister;
import com.leafia.contents.machines.controlpanel.nodes.NodeBulkQuery.CombineMethod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class NodeSummarizer extends Node {
	public CombineMethod combineMethod = CombineMethod.AVERAGE;
	public float[] internalList = new float[40];
	public int needle = 0;
	public NodeSummarizer(float x,float y) {
		super(x,y);
		NodeDropdown combineSelector = new NodeDropdown(this, otherElements.size(),s -> {
			try {
				combineMethod = CombineMethod.valueOf(s);
			} catch (IllegalArgumentException ignored) {}
			return null;
		}, () -> combineMethod.name());
		this.otherElements.add(combineSelector);
		for (CombineMethod value : CombineMethod.values())
			combineSelector.list.addItems(value.name());
		this.outputs.add(new NodeConnection("Summary",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		this.inputs.add(new NodeConnection("Enable",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(0)));
		this.inputs.add(new NodeConnection("New Value",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(0)));
		this.inputs.add(new NodeConnection("Clear",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(0)));
		this.inputs.add(new NodeConnection("Trim Count",this,inputs.size(),true,DataType.NUMBER,new DataValueFloat(20)));
		recalcSize();
	}
	@Override
	public DataValue evaluate(int unused) {
		if (inputs.get(0) == null) return null;
		if (inputs.get(1) == null) return null;
		if (inputs.get(2) == null) return null;
		if (inputs.get(3) == null) return null;
		int length = (int)MathHelper.clamp(inputs.get(3).evaluate().getNumber(),1,500);
		if (length != internalList.length)
			internalList = new float[length];
		if (inputs.get(0).evaluate().getBoolean()) {
			if (needle >= internalList.length) {
				for (int i = 0; i < internalList.length-1; i++)
					internalList[i] = internalList[i+1];
				internalList[internalList.length-1] = inputs.get(1).evaluate().getNumber();
			} else {
				internalList[needle] = inputs.get(1).evaluate().getNumber();
				needle++;
			}
		}
		if (inputs.get(2).evaluate().getBoolean()) {
			Arrays.fill(internalList,0);
			needle = 0;
		}
		float value = 0;
		int total = 0;
		boolean firstTime = true;
		for (float v : internalList) {
			boolean stopLoop = false;
			switch(combineMethod) {
				case ADD -> value += v;
				case MULTIPLY -> value *= v;
				case AVERAGE -> {
					value += v;
					total++;
					if (total >= needle-1)
						stopLoop = true;
				} case HIGHEST -> {
					if (firstTime) value = v;
					else value = Math.max(v,value);
					firstTime = false;
				} case LOWEST -> {
					if (firstTime) value = v;
					else value = Math.min(v,value);
					firstTime = false;
				}
			}
			if (stopLoop) break;
		}
		if (total > 0 && combineMethod == CombineMethod.AVERAGE)
			value /= total;
		return new DataValueFloat(value);
	}
	@Override
	public float[] getColor() {
		return AddonNodesRegister.colorUtility;
	}
	@Override
	public String getDisplayName() {
		return "Summarizer";
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_summarizer");
		tag.setString("combineMethod",combineMethod.name());
		NBTTagList list = new NBTTagList();
		for (float v : internalList)
			list.appendTag(new NBTTagFloat(v));
		tag.setTag("list",list);
		tag.setInteger("needle",needle);
		return super.writeToNBT(tag,sys);
	}
	@Override
	public void readFromNBT(NBTTagCompound tag, NodeSystem sys) {
		try {
			combineMethod = CombineMethod.valueOf(tag.getString("combineMethod"));
		} catch (IllegalArgumentException ignored) {}
		if (tag.hasKey("list")) {
			NBTTagList list = tag.getTagList("list",5);
			internalList = new float[list.tagCount()];
			for (int i = 0; i < internalList.length; i++)
				internalList[i] = list.getFloatAt(i);
		}
		needle = tag.getInteger("needle");
		super.readFromNBT(tag,sys);
	}
}
