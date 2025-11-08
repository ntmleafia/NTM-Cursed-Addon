package com.leafia.contents.machines.controlpanel.nodes;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.DataValue.DataType;
import com.hbm.inventory.control_panel.nodes.Node;
import com.leafia.contents.machines.controlpanel.AddonNodesRegister;
import com.leafia.contents.machines.controlpanel.elements.NodeRetarded;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

public class NodeFormat extends Node {
	String format = "%s";

	public NodeFormat(float x,float y) {
		super(x,y);
		this.outputs.add(new NodeRetarded("Output", this, outputs.size(), false, DataType.STRING, new DataValueString("")));
		this.otherElements.add(new NodeTextBox("Format", this, otherElements.size(), format, (s)->{
			format = s;
			updateFormat();
		}));
		evalCache = new DataValue[1];
		updateFormat();
		recalcSize();
	}

	public void updateFormat() {
		for(NodeConnection c : inputs){
			c.removeConnection();
		}
		this.inputs.clear();
		int argNum = 0;
		for (int i = 0; i < format.length(); i+=2) {
			i = format.indexOf("%",i);
			if (i == -1) break;
			if (i+1 >= format.length()) break;
			boolean readNext = true;
			while (readNext) {
				readNext = false;
				String type = format.substring(i+1,i+2);
				switch(type) {
					case "%": break;
					case "$": case " ": case "-": case "#": case "+": case ".": case ",": case "(":
					case "0": case "1": case "2": case "3": case "4": case "5": case "6": case "7": case "8": case "9":
						i++;
						readNext = true;
						break;
					case "s": case "S":
						argNum++;
						inputs.add(new NodeConnection("Input "+argNum,this,inputs.size(),true,
								DataType.STRING,new DataValueString(""))
						);
						break;
					case "d": case "o": case "x": case "X": case "e": case "E":
					case "f": case "g": case "G": case "a": case "A":
						argNum++;
						inputs.add(new NodeConnection("Input "+argNum,this,inputs.size(),true,
								DataType.NUMBER,new DataValueFloat(0))
						);
						break;
				}
			}
		}
		recalcSize();
	}

	@Override
	public DataValue evaluate(int idx) {
		if (cacheValid)
			return evalCache[0];
		cacheValid = true;

		List<Object> argsList = new ArrayList<>();
		for (int i = 0; i < inputs.size(); i++) {
			DataValue value = inputs.get(i).evaluate();
			if (value instanceof DataValueFloat)
				argsList.add(value.getNumber());
			else if (value instanceof DataValueString)
				argsList.add(value.toString());
		}

		Object[] args = new Object[argsList.size()];
		for (int i = 0; i < argsList.size(); i++)
			args[i] = argsList.get(i);

		String output = "ERROR";
		try {
			output = String.format(format,args);
		} catch (IllegalFormatException ignored) {}

		return evalCache[0] = new DataValueString(output);
	}

	@Override
	public float[] getColor() {
		return AddonNodesRegister.colorString;
	}

	@Override
	public String getDisplayName() {
		return "Format";
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","format");
		tag.setString("format",format);
		return super.writeToNBT(tag,sys);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag,NodeSystem sys) {
		format = tag.getString("format");
		super.readFromNBT(tag,sys);
	}
}
