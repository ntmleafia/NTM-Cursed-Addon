package com.leafia.contents.machines.controlpanel.ic10;

import com.leafia.settings.AddonConfig;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import static com.leafia.contents.machines.controlpanel.ic10.IC10.IC10Type.*;

public class IC10 {
	public static Map<String,Object> insn_def = null;
	/// ANY doesn't include REGISTER and NAME
	public enum IC10Type {
		NUMBER,
		REGISTER,
		STRING,
		INTEGER,
		/// Doesn't include REGISTER and NAME
		ANY,
		NAME
	}
	public static class IC10Instruction {
		List<IC10Argument> args = new ArrayList<>();
		BiConsumer<IC10State,Object[]> function;
		String[] desc;
		public IC10Instruction(List<IC10Argument> args,BiConsumer<IC10State,Object[]> function,String... desc) {
			this.args = args;
			this.function = function;
			this.desc = desc;
		}
	}
	public static class IC10Argument {
		String name;
		IC10Type type;
		protected IC10Argument(String name,IC10Type type) {
			this.name = name;
			this.type = type;
		}
	}
	/// short wrapper for new IC10Instruction
	public static IC10Instruction make(List<IC10Argument> args,BiConsumer<IC10State,Object[]> function,String... desc) {
		return new IC10Instruction(args,function,desc);
	}
	public static List<IC10Argument> args(Object... args) {
		List<IC10Argument> list = new ArrayList<>();
		int i = 0;
		String lastName = null;
		for (Object arg : args) {
			int o = i%2;
			if (o == 0)
				lastName = (String)arg;
			else {
				IC10Type type = (IC10Type)arg;
				list.add(new IC10Argument(lastName,type));
				lastName = null;
			}
			i++;
		}
		return list;
	}
	public static Map<String,Object> map(Object... objects) {
		Map<String,Object> map = new HashMap<>();
		int i = 0;
		String key = null;
		for (Object obj : objects) {
			int o = i%2;
			if (o == 0)
				key = (String)obj;
			else {
				map.put(key,obj);
				key = null;
			}
			i++;
		}
		return map;
	}
	public static final Map<String,IC10Instruction> instructions = new HashMap<>();
	public static final Map<IC10Instruction,String> instructionNames = new HashMap<>();
	public static final Map<IC10Instruction,String> category = new HashMap<>();
	static void iter(Map<String,Object> d,String path) {
		for (Entry<String,Object> entry : d.entrySet()) {
			if (entry.getValue() instanceof Map<?,?> mop)
				iter((Map<String,Object>)mop,path.isEmpty() ? entry.getKey() : path+"/"+entry.getKey());
			else {
				instructions.put(entry.getKey(),(IC10Instruction)entry.getValue());
				instructionNames.put((IC10Instruction)entry.getValue(),entry.getKey());
				category.put((IC10Instruction)entry.getValue(),path);
			}
		}
	}
	/// CALL THIS EVERYTIME YOU MODIFY insn_def OR IT WILL NOT WORK!
	public static void rebuildInsnTable() {
		instructions.clear();
		category.clear();
		iter(insn_def,"");
	}
	public static IC10Instruction define = make(
			args("name",NAME,"num",NUMBER),
			(env,args)->{},
			"Creates a label that will be replaced",
			"throughout the program with the provided value."
	);
	static boolean equals(Object a,Object b) {
		if (a == null)
			return b == null;
		return a.equals(b);
	}
	static {
		insn_def = map(
				"setup",map(
						"alias",make(
								args("name",NAME,"target",REGISTER),
								(env,args)->{ },
								"Labels register with name."
						)
				),
				"l",make(
						args("register",REGISTER,"key",STRING),
						(env,args)->{
							String value = env.input((String)args[1]);
							env.setRegister((int)args[0],value);
						},
						"Register = value of key in Input"
				),
				"s",make(
						args("key",STRING,"a",STRING),
						(env,args)->{
							env.output((String)args[0],(String)args[1]);
						},
						"Value of key in Output = a"
				),
				"math",map(
						"rand",make(
								args("register",REGISTER),
								(env,args)->{
									env.setRegister((int)args[0],env.node.ctrl.panel.parent.getControlWorld().rand.nextDouble());
								},
								"Register = A random decimal value between 0 and 1"
						),
						"ops",map(
								"move",make(
										args("register",REGISTER,"a",ANY),
										(env,args)->{
											env.setRegister((int)args[0],args[1]);
										},
										"Register = a"
								),
								"add",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1]+(double)args[2]);
										},
										"Register = a + b"
								),
								"sub",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1]-(double)args[2]);
										},
										"Register = a - b"
								),
								"mul",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1]*(double)args[2]);
										},
										"Register = a * b"
								),
								"div",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1]/(double)args[2]);
										},
										"Register = a / b"
								),
								"mod",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],MathHelper.positiveModulo((double)args[1],(double)args[2]));
										},
										"Register = a mod b (positive modulo)"
								),
								"pow",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.pow((double)args[1],(double)args[2]));
										},
										"Register = a ^ b"
								)
						),
						"advanced",map(
								"sqrt",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.sqrt((double)args[1]));
										},
										"Register = square root of a"
								),
								"exp",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.exp((double)args[1]));
										},
										"Register = exp(a)"
								),
								"log",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.log((double)args[1]));
										},
										"Register = log(a)"
								)
						),
						"utility",map(
								"max",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.max((double)args[1],(double)args[2]));
										},
										"Register = max of a or b"
								),
								"min",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.min((double)args[1],(double)args[2]));
										},
										"Register = min of a or b"
								),
								"floor",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(int)Math.floor((double)args[1]));
										},
										"Register = Register = largest integer less than or equal a"
								),
								"ceil",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(int)Math.floor((double)args[1]));
										},
										"Register = smallest integer greater than or equal a"
								),
								"round",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(int)Math.round((double)args[1]));
										},
										"Register = a rounded to nearest integer"
								)
						),
						"trigonometry",map(
								"sin",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.sin((double)args[1]));
										},
										"Returns the sine of the specified angle (radians)"
								),
								"asin",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.asin((double)args[1]));
										},
										"Returns the angle (radians) whos sine is the specified value"
								),
								"cos",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.cos((double)args[1]));
										},
										"Returns the cosine of the specified angle (radians)"
								),
								"acos",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.acos((double)args[1]));
										},
										"Returns the angle (radians) whos cosine is the specified value"
								),
								"tan",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.tan((double)args[1]));
										},
										"Returns the tangent of the specified angle (radians)"
								),
								"atan",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.atan((double)args[1]));
										},
										"Returns the angle (radians) whos tangent is the specified value"
								),
								"atan2",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],Math.atan2((double)args[1],(double)args[2]));
										},
										"Returns the angle (radians) whose tangent is the\",\"quotient of two specified values: a (y) and b (x)"
								)
						)
				),
				"conditionals",map(
						"register",map(
								"seq",make(
										args("register",REGISTER,"a",ANY,"b",ANY),
										(env,args)->{
											env.setRegister((int)args[0],equals(args[1],args[2]) ? 1 : 0);
										},
										"Register = 1 if a == b, otherwise 0"
								),
								"seqz",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] == 0 ? 1 : 0);
										},
										"Register = 1 if a == 0, otherwise 0"
								),
								"sne",make(
										args("register",REGISTER,"a",ANY,"b",ANY),
										(env,args)->{
											env.setRegister((int)args[0],!equals(args[1],args[2]) ? 1 : 0);
										},
										"Register = 1 if a != b, otherwise 0"
								),
								"snez",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] != 0 ? 1 : 0);
										},
										"Register = 1 if a != 0, otherwise 0"
								),
								"sgt",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] > (double)args[2] ? 1 : 0);
										},
										"Register = 1 if a > b, otherwise 0"
								),
								"sgtz",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] > 0 ? 1 : 0);
										},
										"Register = 1 if a > 0, otherwise 0"
								),
								"sge",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] >= (double)args[2] ? 1 : 0);
										},
										"Register = 1 if a >= b, otherwise 0"
								),
								"sgez",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] >= 0 ? 1 : 0);
										},
										"Register = 1 if a >= 0, otherwise 0"
								),
								"slt",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] < (double)args[2] ? 1 : 0);
										},
										"Register = 1 if a < b, otherwise 0"
								),
								"sltz",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] < 0 ? 1 : 0);
										},
										"Register = 1 if a < 0, otherwise 0"
								),
								"sle",make(
										args("register",REGISTER,"a",NUMBER,"b",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] <= (double)args[2] ? 1 : 0);
										},
										"Register = 1 if a <= b, otherwise 0"
								),
								"slez",make(
										args("register",REGISTER,"a",NUMBER),
										(env,args)->{
											env.setRegister((int)args[0],(double)args[1] <= 0 ? 1 : 0);
										},
										"Register = 1 if a <= 0, otherwise 0"
								)
						),
						"branch",map(
								"beq",make(
										args("a",ANY,"b",ANY,"c",INTEGER),
										(env,args)->{
											if (equals(args[1],args[2]))
												env.line = (int)args[3];
										},
										"Branch to line c if a == b"
								),
								"beqz",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] == 0)
												env.line = (int)args[2];
										},
										"Branch to line b if a == 0"
								),
								"bne",make(
										args("a",ANY,"b",ANY,"c",INTEGER),
										(env,args)->{
											if (!equals(args[1],args[2]))
												env.line = (int)args[3];
										},
										"Branch to line c if a != b"
								),
								"bnez",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] != 0)
												env.line = (int)args[2];
										},
										"Branch to line b if a != 0"
								),
								"bgt",make(
										args("a",NUMBER,"b",NUMBER,"c",INTEGER),
										(env,args)->{
											if ((double)args[0] > (double)args[1])
												env.line = (int)args[3];
										},
										"Branch to line c if a > b"
								),
								"bgtz",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] > 0)
												env.line = (int)args[2];
										},
										"Branch to line b if a > 0"
								),
								"bge",make(
										args("a",NUMBER,"b",NUMBER,"c",INTEGER),
										(env,args)->{
											if ((double)args[0] >= (double)args[1])
												env.line = (int)args[3];
										},
										"Branch to line c if a >= b"
								),
								"bgez",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] >= 0)
												env.line = (int)args[2];
										},
										"Branch to line b if a >= 0"
								),
								"blt",make(
										args("a",NUMBER,"b",NUMBER,"c",INTEGER),
										(env,args)->{
											if ((double)args[0] < (double)args[1])
												env.line = (int)args[3];
										},
										"Branch to line c if a < b"
								),
								"bltz",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] < 0)
												env.line = (int)args[2];
										},
										"Branch to line b if a < 0"
								),
								"ble",make(
										args("a",NUMBER,"b",NUMBER,"c",INTEGER),
										(env,args)->{
											if ((double)args[0] <= (double)args[1])
												env.line = (int)args[3];
										},
										"Branch to line c if a <= b"
								),
								"blez",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] <= 0)
												env.line = (int)args[2];
										},
										"Branch to line b if a <= 0"
								)
						),
						"relative branch",map(
								"breq",make(
										args("a",ANY,"b",ANY,"c",INTEGER),
										(env,args)->{
											if (equals(args[1],args[2]))
												env.line += (int)args[3];
										},
										"Skip c lines if a == b"
								),
								"breqz",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] == 0)
												env.line += (int)args[2];
										},
										"Skip b lines if a == 0"
								),
								"brne",make(
										args("a",ANY,"b",ANY,"c",INTEGER),
										(env,args)->{
											if (!equals(args[1],args[2]))
												env.line += (int)args[3];
										},
										"Skip c lines if a != b"
								),
								"brnez",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] != 0)
												env.line += (int)args[2];
										},
										"Skip b lines if a != 0"
								),
								"brgt",make(
										args("a",NUMBER,"b",NUMBER,"c",INTEGER),
										(env,args)->{
											if ((double)args[0] > (double)args[1])
												env.line += (int)args[3];
										},
										"Skip c lines if a > b"
								),
								"brgtz",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] > 0)
												env.line += (int)args[2];
										},
										"Skip b lines if a > 0"
								),
								"brge",make(
										args("a",NUMBER,"b",NUMBER,"c",INTEGER),
										(env,args)->{
											if ((double)args[0] >= (double)args[1])
												env.line += (int)args[3];
										},
										"Skip c lines if a >= b"
								),
								"brgez",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] >= 0)
												env.line += (int)args[2];
										},
										"Skip b lines if a >= 0"
								),
								"brlt",make(
										args("a",NUMBER,"b",NUMBER,"c",INTEGER),
										(env,args)->{
											if ((double)args[0] < (double)args[1])
												env.line += (int)args[3];
										},
										"Skip c lines if a < b"
								),
								"brltz",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] < 0)
												env.line += (int)args[2];
										},
										"Skip b lines if a < 0"
								),
								"brle",make(
										args("a",NUMBER,"b",NUMBER,"c",INTEGER),
										(env,args)->{
											if ((double)args[0] <= (double)args[1])
												env.line += (int)args[3];
										},
										"Skip c lines if a <= b"
								),
								"brlez",make(
										args("a",NUMBER,"b",INTEGER),
										(env,args)->{
											if ((double)args[0] <= 0)
												env.line += (int)args[2];
										},
										"Skip b lines if a <= 0"
								)
						)
				),
				"stack",map(
						"push",make(
								args("a",ANY),
								(env,args)->{
									if ((int)env.getNumber("sp") >= AddonConfig.ic10maxstack) {
										env.error = "StackOverflow";
										return;
									}
									env.stack[(int)env.getNumber("sp")] = args[0];
									env.setRegister(IC10State.register_sp,(int)env.getNumber("sp")+1);
								},
								"Pushes the value of a to the stack at sp and increments sp"
						),
						"pop",make(
								args("reg",REGISTER),
								(env,args)->{
									env.setRegister(IC10State.register_sp,(int)env.getNumber("sp")-1);
									if ((int)env.getNumber("sp") < 0) {
										env.error = "StackUnderflow";
										return;
									}
									env.setRegister((int)args[0],env.stack[(int)env.getNumber("sp")]);
								},
								"Register = the value at the top of the stack and decrements sp"
						),
						"peek",make(
								args("reg",REGISTER),
								(env,args)->{
									if ((int)env.getNumber("sp") < 0) {
										env.error = "StackUnderflow";
										return;
									}
									env.setRegister((int)args[0],env.stack[(int)env.getNumber("sp")]);
								},
								"Register = the value at the top of the stack"
						)
				),
				"j",make(
						args("a",INTEGER),
						(env,args)->{
							env.line = (int)args[0];
						},
						"Jump execution to line a"
				),
				"jr",make(
						args("a",INTEGER),
						(env,args)->{
							env.line += (int)args[0];
						},
						"Skip execution by a lines"
				),
				"jal",make(
						args("a",INTEGER),
						(env,args)->{
							env.setRegister(IC10State.register_ra,env.line+1);
							env.line = (int)args[0];
						},
						"Jump execution to line a and store next line number in ra"
				),
				"yield",make(
						args(),
						(env,args)->{
							env.yield = 10;
						},
						"Pauses execution for 1 stationeers tick (= half a second)",
						"assuming this node is in the tick event"
				),
				"sleep",make(
						args("a",INTEGER),
						(env,args)->{
							env.yield = (int)args[0];
						},
						"Pauses execution for a minecraft ticks",
						"assuming this node is in the tick event"
				)
		);
		rebuildInsnTable();
	}
}
