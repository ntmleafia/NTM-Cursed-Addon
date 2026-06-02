package com.leafia.contents.machines.controlpanel.nodes.pack;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.nodes.Node;
import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValue.DataType;
import com.hbm.inventory.control_panel.types.DataValueComposite;
import com.hbm.inventory.control_panel.types.DataValueFloat;
import com.hbm.inventory.control_panel.types.DataValueString;
import com.leafia.contents.machines.controlpanel.ic10.IC10State;
import com.leafia.contents.machines.controlpanel.ic10.SubElementIC10Editor;
import com.leafia.overwrite_contents.interfaces.IMixinGuiControlEdit;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.nbt.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NodeIC10 extends Node {
	// \{(.*)\};
	// asList($1),
	static <T> ArrayList<T> asArrayList(T ...a) {
		return new ArrayList<>(Arrays.asList(a));
	}
	public ArrayList<ArrayList<Object>> instructions = asArrayList(
			asArrayList("alias",1,"timer",1,"r0"),
			asArrayList("alias",1,"on",1,"r1"),
			asArrayList(),
			asArrayList("#"," reset variables"),
			asArrayList("move",1,"timer",1,"0"),
			asArrayList("move",1,"on",1,"0"),
			asArrayList(),
			asArrayList("Loop:"),
			asArrayList(2,"#"," check if timer equals 3"),
			asArrayList(2,"seq",1,"on",1,"timer",1,"3"),
			asArrayList(2,"s",1,"\"signal\"",1,"on"),
			asArrayList(2),
			asArrayList(2,"#"," adds timer 1 and modulo by 4"),
			asArrayList(2,"#"," (1mod4=1, 2mod4=2, 3mod4=3, 4mod4=0, 5mod4=1, 6mod4=2...)"),
			asArrayList(2,"add",1,"timer",1,"timer",1,"1"),
			asArrayList(2,"mod",1,"timer",1,"timer",1,"4"),
			asArrayList(2),
			asArrayList(2,"#"," 1 second = 2 stationeers ticks"),
			asArrayList(2,"yield"),
			asArrayList(2,"yield"),
			asArrayList(2,"j",1,"Loop")
	);
	public IC10State state = new IC10State(this);
	public Control ctrl;
	public NodeIC10(float x,float y,Control ctrl) {
		super(x,y);
		this.ctrl = ctrl;
		otherElements.add(new NodeButton("Edit Code",this,otherElements.size()) {
			@Override
			public void onClicked(SubElement subElement) {
				IMixinGuiControlEdit mixin = (IMixinGuiControlEdit)subElement.gui;
				SubElementIC10Editor editor = mixin.leafia$ic10Editor();
				editor.instructions = instructions;
				mixin.leafia$pushElement(editor);
			}
		});
		otherElements.add(new NodeButton("Reset State",this,otherElements.size()) {
			@Override
			public void onClicked(SubElement subElement) {
				state = new IC10State(NodeIC10.this);
			}
		});
		outputs.add(new NodeConnection("Output",this,outputs.size(),false,DataType.COMPOSITE,new DataValueComposite()));
		outputs.add(new NodeConnection("Error",this,outputs.size(),false,DataType.STRING,new DataValueString("")));
		outputs.add(new NodeConnection("Line",this,outputs.size(),false,DataType.NUMBER,new DataValueFloat(0)));
		inputs.add(new NodeConnection("Input",this,inputs.size(),true,DataType.COMPOSITE,new DataValueComposite()));
		recalcSize();
	}
	@Override
	public DataValue evaluate(int i) {
		if (!cacheValid) {
			cacheValid = true;
			state.update();
		}
		if (i == 0)
			return state.output;
		else if (i == 1)
			return new DataValueString(state.error != null ? state.error : "");
		else
			return new DataValueFloat(state.line-1);
	}
	@Override
	public float[] getColor() {
		return DataType.COMPOSITE.getColor();
	}
	@Override
	public String getDisplayName() {
		return "IC10 Sequence";
	}
	static byte[] append(byte[] array,byte b) {
		byte[] newarray = new byte[array.length+1];
		System.arraycopy(array,0,newarray,0,array.length);
		newarray[array.length] = b;
		return newarray;
	}
	static byte[] append(byte[] array,byte[] add) {
		byte[] newarray = new byte[array.length+add.length];
		System.arraycopy(array,0,newarray,0,array.length);
		System.arraycopy(add,0,newarray,array.length,add.length);
		return newarray;
	}
	@Override
	public void readFromNBT(NBTTagCompound tag,NodeSystem sys) {
		super.readFromNBT(tag,sys);
		if (tag.hasKey("instructions") || tag.hasKey("compressed")) {
			byte[] data;
			if (tag.hasKey("compressed"))
				data = gunzip(tag.getByteArray("compressed"));
			else
				data = tag.getByteArray("instructions");
			instructions.clear();
			byte[] cache = new byte[0];
			boolean writingString = false;
			ArrayList<Object> instruction = new ArrayList<>();
			for (byte b : data) {
				if (b < 0 && writingString) {
					instruction.add(new String(cache,StandardCharsets.UTF_8));
					cache = new byte[0];
					writingString = false;
				}
				if (b == -1) {
					instructions.add(instruction);
					instruction = new ArrayList<>();
				} else if (b < -2)
					instruction.add(-(b+2));
				else if (b >= 0) {
					writingString = true;
					cache = append(cache,b);
				}
			}
		}
		if (tag.hasKey("state")) {
			state = new IC10State(this);
			state.deserialize(tag.getCompoundTag("state"));
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag,NodeSystem sys) {
		tag.setString("nodeType","leafia_ic10");
		byte[] codes = new byte[0];
		for (ArrayList<Object> instruction : instructions) {
			boolean lastWasString = false;
			for (Object object : instruction) {
				if (object instanceof String s) {
					if (lastWasString)
						codes = append(codes,(byte)(-2));
					codes = append(codes,s.getBytes(StandardCharsets.UTF_8));
					lastWasString = true;
				} else if (object instanceof Number n) {
					byte spaces = (byte)(-n.intValue()-2);
					if (spaces >= -2)
						spaces = Byte.MIN_VALUE;
					codes = append(codes,spaces);
					lastWasString = false;
				} else
					throw new LeafiaDevFlaw("Invalid IC10 instruction: "+object.getClass().getSimpleName());
			}
			codes = append(codes,(byte)(-1));
		}
		gzip(tag,codes);
		tag.setTag("state",state.serialize());
		return super.writeToNBT(tag,sys);
	}
	static byte[] gunzip(byte[] data) {
		try {
			try (var gis = new GZIPInputStream(new ByteArrayInputStream(data))) {
				return gis.readAllBytes();          // Java 9+
			}
		} catch (IOException io) {
			System.err.println("ERROR: IC10 node zip compression failed!");
			io.printStackTrace();
			return new byte[0];
		}
	}
	static void gzip(NBTTagCompound tag,byte[] data) {
		try {
			var bos = new ByteArrayOutputStream();
			try (var gz = new GZIPOutputStream(bos)) {
				gz.write(data);
			}
			byte[] comp = bos.toByteArray();
			tag.setByteArray("compressed",comp);
			System.out.println("IC10 node zip compression success! ("+data.length+" -> "+comp.length+" bytes)");
		} catch (IOException io) {
			System.err.println("ERROR: IC10 node zip compression failed!");
			io.printStackTrace();
			tag.setByteArray("instructions",data);
		}
	}
}
