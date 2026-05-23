package com.leafia.contents.machines.controlpanel.ic10;

import com.hbm.inventory.control_panel.types.DataValue;
import com.hbm.inventory.control_panel.types.DataValueComposite;
import com.leafia.contents.machines.controlpanel.ic10.IC10.IC10Argument;
import com.leafia.contents.machines.controlpanel.ic10.IC10.IC10Instruction;
import com.leafia.contents.machines.controlpanel.ic10.IC10.IC10Type;
import com.leafia.contents.machines.controlpanel.ic10.SubElementIC10Editor.EditorColors;
import com.leafia.contents.machines.controlpanel.nodes.pack.NodeIC10;
import com.leafia.settings.AddonConfig;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class IC10State {
	public NodeIC10 node;
	public DataValueComposite output = new DataValueComposite();
	public Map<Integer,Object> register = new HashMap<>();
	public Map<String,Object> labels = new HashMap<>();
	public Map<String,String> aliases = new HashMap<>();
	public Object[] stack = new Object[AddonConfig.ic10maxstack];
	public String error = null;
	public boolean compiled = false;
	public int line = 0;
	public static final int register_sp = -1;
	public static final int register_ra = -2;
	public int yield = 0;

	public IC10State(NodeIC10 node) {
		this.node = node;
	}

	public NBTTagCompound serialize() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("line",line);
		tag.setInteger("yield",yield);
		if (error != null)
			tag.setString("error",error);
		NBTTagCompound regs = new NBTTagCompound();
		for (Entry<Integer,Object> entry : register.entrySet()) {
			if (entry.getValue() instanceof String s)
				regs.setString(Integer.toString(entry.getKey()),s);
			else if (entry.getValue() instanceof Double d)
				regs.setDouble(Integer.toString(entry.getKey()),d);
		}
		tag.setTag("registry",regs);
		NBTTagCompound stacks = new NBTTagCompound();
		int index = 0;
		for (Object o : stack) {
			if (o != null) {
				if (o instanceof String s)
					regs.setString(Integer.toString(index),s);
				else if (o instanceof Double d)
					regs.setDouble(Integer.toString(index),d);
			}
			index++;
		}
		tag.setTag("stack",stacks);
		return tag;
	}
	public void deserialize(NBTTagCompound tag) {
		if (compiled)
			throw new LeafiaDevFlaw("Already compiled!");
		//register.clear();
		//stack = new Object[AddonConfig.ic10maxstack];
		line = tag.getInteger("line");
		yield = tag.getInteger("yield");
		if (tag.hasKey("error"))
			error = tag.getString("error");
		NBTTagCompound regs = tag.getCompoundTag("registry");
		for (String s : regs.getKeySet()) {
			NBTBase base = regs.getTag(s);
			int index = Integer.parseInt(s);
			if (base instanceof NBTTagString ns)
				register.put(index,ns.getString());
			else if (base instanceof NBTTagDouble nd)
				register.put(index,nd.getDouble());
		}
		NBTTagCompound stacks = tag.getCompoundTag("stack");
		for (int i = 0; i < stack.length; i++) {
			if (stacks.hasKey(Integer.toString(i))) {
				NBTBase base = regs.getTag(Integer.toString(i));
				if (base instanceof NBTTagString ns)
					stack[i] = ns.getString();
				else if (base instanceof NBTTagDouble nd)
					stack[i] = nd.getDouble();
			}
		}
	}

	public void setRegister(int index,Object value) {
		if (value instanceof String s) {
			if (s.isEmpty()) {
				register.remove(index);
				return;
			}
		} else if (value instanceof Double d) {
			if (d == 0) {
				register.remove(index);
				return;
			}
		}
		if (index < -2 || index > AddonConfig.ic10maxregisters+1)
			error = "OutOfRegisterBounds";
		else
			register.put(index,value);
	}
	public int getRegisterIndex(String str) {
		if (labels.containsKey(str)) {
			error = "IncorrectVariable";
			return 0;
		}
		String out = aliases.getOrDefault(str,str);
		if (out.equals("sp")) return register_sp;
		if (out.equals("ra")) return register_ra;
		if (out.startsWith("r")) {
			Integer index = tonumber(out.substring(1));
			if (index == null) {
				error = "IncorrectVariable";
				return 0;
			} else if (index < 0 || index > AddonConfig.ic10maxregisters) {
				error = "OutOfRegisterBounds";
				return 0;
			}
			return index;
		} else {
			error = "IncorrectVariable";
			return 0;
		}
	}
	public Object getValue(String str) {
		if (labels.containsKey(str)) return labels.get(str);
		String out = aliases.get(str);
		if (out == null) return str;
		if (out.equals("sp")) return register.get(register_sp);
		if (out.equals("ra")) return register.get(register_ra);
		if (out.startsWith("r")) {
			Integer index = tonumber(out.substring(1));
			if (index == null)
				error = "IncorrectVariable";
			else if (index < 0 || index > AddonConfig.ic10maxregisters)
				error = "OutOfRegisterBounds";
		} else
			return str;
		return "";
	}
	public double getNumber(String str) {
		Object object = getValue(str);
		if (object instanceof Number n)
			return n.doubleValue();
		else if (object instanceof String s) {
			Object v = s;
			if (labels.containsKey(s))
				v = labels.get(s);
			if (aliases.containsKey(s))
				s = aliases.get(s);
			if (s.equals("ra"))
				v = register.get(register_ra);
			else if (s.equals("sp"))
				v = register.get(register_sp);
			else if (s.startsWith("r")) {
				Integer idx = tonumber(s.substring(1));
				if (idx != null && idx >= 0 && idx <= AddonConfig.ic10maxregisters)
					v = register.getOrDefault(idx,0d);
			}
			if (v instanceof String vs) {
				Double d = tonumberd(vs);
				if (d == null)
					error = "IncorrectVariable";
				else
					return d;
			} else if (v instanceof Double d)
				return d;
			else
				error = "IncorrectVariable";
			return 0;
		} else
			throw new LeafiaDevFlaw("what");
	}
	public String input(String key) {
		DataValue value = node.inputs.get(0).evaluate();
		if (value instanceof DataValueComposite composite)
			return composite.getValueOf(key);
		else
			return "";
	}
	public void output(String key,String value) {
		output.setValueOf(key,value);
	}

	// UTILITY //
	boolean equals(Object a,Object b) {
		if (a == null)
			return b == null;
		return a.equals(b);
	}
	@Nullable static Integer tonumber(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ignored) {}
		return null;
	}
	@Nullable static Double tonumberd(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException ignored) {}
		return null;
	}
	public static Object parseConstant(String s) {
		if (s.equals("ra"))
			return "ra";
		else if (s.equals("sp"))
			return "sp";
		else if (s.startsWith("r")) {
			Integer idx = tonumber(s.substring(1));
			if (idx != null && idx >= 0 && idx <= AddonConfig.ic10maxregisters)
				return s;
		}
		if (s.startsWith("\"") && s.endsWith("\""))
			return s.substring(1,s.length()-1);
		else {
			try {
				return Double.parseDouble(s);
			} catch (NumberFormatException e) {
				return null;
			}
		}
	}
	public Object parseValue(String s) {
		if (labels.containsKey(s))
			return labels.get(s);
		if (aliases.containsKey(s))
			s = aliases.get(s);
		if (s.equals("ra"))
			return register.get(register_ra);
		else if (s.equals("sp"))
			return register.get(register_sp);
		else if (s.startsWith("r")) {
			Integer idx = tonumber(s.substring(1));
			if (idx != null && idx >= 0 && idx <= AddonConfig.ic10maxregisters)
				return register.getOrDefault(idx,"");
		}
		if (s.startsWith("\"") && s.endsWith("\""))
			return s.substring(1,s.length()-1);
		else {
			try {
				return Double.parseDouble(s);
			} catch (NumberFormatException e) {
				return null;
			}
		}
	}
	void compile() {
		compiled = true;
		int ln = 0;
		for (ArrayList<Object> insn : node.instructions) {
			int args = 0;
			String mode = null;
			String name = null;
			for (Object o : insn) {
				if (o instanceof String s) {
					args++;
					if (args == 1) { // first argument
						if (s.length() >= 2 && s.endsWith(":")) { // jump labels
							labels.put(s.substring(0,s.length()-1),labels.getOrDefault(s.substring(0,s.length()-1),ln));
							break;
						} else if (s.equals("define")) // constants
							mode = "labels";
						else if (s.equals("alias")) // alias
							mode = "aliases";
					} else if (mode != null) {
						if (args == 2) // memorize name
							name = s;
						else if (args == 3) { // actually set value at third argument
							Object value = parseConstant(s);
							if (value == null) {
								error = "CompileError";
								return;
							}
							if (mode.equals("labels"))
								labels.put(name,s);
							else if (mode.equals("aliases"))
								aliases.put(name,s);
						}
					} else // if its unrelated instruction
						break;
				}
			}
			ln++;
		}
	}
	public void update() {
		if (!compiled)
			compile();
		if (error != null) return;
		if (yield > 0) {
			yield--;
			if (yield > 0)
				return;
		}
		for (int i = 0; i < 128; i++) {
			if (yield > 0)
				break;
			if (line >= node.instructions.size()) {
				error = "OutOfInstructions";
				return;
			}
			ArrayList<Object> insn = node.instructions.get(line);
			line++;
			int args = 0;
			IC10Instruction insnType = null;
			Object[] pack = null;
			for (Object o : insn) {
				if (o instanceof String s) {
					if (s.equals("#")) break;
					args++;
					if (args == 1) {
						if (s.endsWith(":")) break;
						if (s.equals("define")) break;
						insnType = IC10.instructions.get(s);
						if (insnType == null) {
							error = "UnrecognizedInstruction";
							return;
						}
						pack = new Object[insnType.args.size()];
					} else {
						if (args-2 >= insnType.args.size()) {
							error = "IncorrectArgumentCount";
							return;
						}
						Object value = s;
						IC10Type argType = insnType.args.get(args-2).type;
						if (argType != IC10Type.REGISTER && argType != IC10Type.NAME) {
							value = parseValue(s);
							if (value == null) {
								error = "IncorrectVariable";
								return;
							}
							if (argType == IC10Type.NUMBER || argType == IC10Type.INTEGER) {
								if (value instanceof String vs) {
									if (vs.isEmpty())
										value = 0;
									else {
										try {
											value = Double.parseDouble(vs);
										} catch (NumberFormatException e) {
											error = "IncorrectVariable";
											return;
										}
									}
								}
								if (argType == IC10Type.INTEGER) {
									if (value instanceof Double vd) {
										if (vd.intValue() != vd) {
											error = "IncorrectVariable";
											return;
										}
										value = vd.intValue();
									}
								}
							}
						} else if (argType == IC10Type.REGISTER) {
							value = getRegisterIndex(s);
							if (error != null)
								return;
						}
						if (argType == IC10Type.NAME || argType == IC10Type.STRING) {
							if (!(value instanceof String))
								value = value.toString();
						}
						if (value instanceof Integer vi && argType == IC10Type.NUMBER)
							value = vi.doubleValue();
						pack[args-2] = value;
					}
				}
			}
			if (insnType != null) {
				if (args-2 < insnType.args.size()-1) {
					error = "IncorrectArgumentCount";
					return;
				}
				try { // fuck off
					insnType.function.accept(this,pack);
				} catch (Exception e) {
					error = "InternalError (See minecraft logs)";
					System.out.println("IC10 ERROR: "+e.getMessage());
					System.out.println("IC10 threw an unexpected error! Please create a bug report with this log attached.");
					System.out.println("\tInstruction: "+IC10.instructionNames.get(insnType));
					System.out.println("\tLine: "+(line-1));
					System.out.println("\tArguments:");
					for (Object object : pack) {
						System.out.println(object.toString()+" ("+object.getClass().getSimpleName()+")");
					}
					System.out.println("\tStack trace:");
					e.printStackTrace();
					return;
				}
			}
		}
	}
}
