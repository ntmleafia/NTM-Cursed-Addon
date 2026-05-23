package com.leafia.contents.machines.controlpanel.ic10;

import com.custom_hbm.util.LCETuple.Pair;
import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.SubElement;
import com.leafia.AddonBase;
import com.leafia.contents.machines.controlpanel.ic10.IC10.IC10Argument;
import com.leafia.contents.machines.controlpanel.ic10.IC10.IC10Instruction;
import com.leafia.contents.machines.controlpanel.ic10.IC10.IC10Type;
import com.leafia.dev.LeafiaBrush;
import com.leafia.dev.LeafiaUtil.ScrollUtil;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.overwrite_contents.interfaces.IMixinGuiControlEdit;
import com.leafia.settings.AddonConfig;
import com.leafia.transformer.LeafiaGls;
import com.llib.exceptions.LeafiaDevFlaw;
import com.llib.math.LeafiaColor;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.nio.DoubleBuffer;
import java.util.*;
import java.util.Map.Entry;

public class SubElementIC10Editor extends SubElement {
	// \{(.*)\};
	// asList($1),
	static <T> ArrayList<T> asArrayList(T ...a) {
		return new ArrayList<>(Arrays.asList(a));
	}
	public ArrayList<ArrayList<Object>> instructions;
	ArrayList<UndoData> undoHistory = new ArrayList<>();
	int undoCurrent = -1;
	UndoData lastUndo = null;
	public static class UndoData { }
	public static class UndoDataAddRemove extends UndoData {
		public final int line;
		public int pos;
		public String str;
		public final boolean isAdd;
		public UndoDataAddRemove(int line,int pos,String str,boolean isAdd) {
			this.line = line;
			this.pos = pos;
			this.str = str;
			this.isAdd = isAdd;
		}
	}
	public static class UndoDataReplace extends UndoData {
		public final int lst;
		public final ArrayList<ArrayList<Object>> before;
		public final ArrayList<ArrayList<Object>> after;
		public final int[] cursorBefore;
		public final int[] cursorAfter;
		public UndoDataReplace(int lst,ArrayList<ArrayList<Object>> before,ArrayList<ArrayList<Object>> after,int[] cursorBefore,int[] cursorAfter) {
			this.lst = lst;
			this.before = before;
			this.after = after;
			this.cursorBefore = cursorBefore;
			this.cursorAfter = cursorAfter;
		}
	}
	String substringSafely(String s,int start) {
		return s.substring(Math.min(Math.max(start,0),s.length()));
	}
	String substringSafely(String s,int start,int end) {
		return s.substring(Math.min(Math.max(start,0),s.length()),Math.min(end,s.length()));
	}
	void addString(int z,int pos,String str) {
		ArrayList<Object> insn = instructions.get(z);
		String s = toString(insn);
		String suffix = "";
		s = substringSafely(s,0,pos-1)+str+substringSafely(s,pos-1);
		instructions.set(z,formatInstruction(asArrayList(s)));
	}
	void removeString(int z,int pos,int length) {
		ArrayList<Object> insn = instructions.get(z);
		int posEnd = pos-length;
		String s = toString(insn);
		s = substringSafely(s,0,posEnd)+substringSafely(s,pos);
		instructions.set(z,formatInstruction(asArrayList(s)));
	}
	public SubElementIC10Editor(GuiControlEdit gui) {
		super(gui);
		/*for (int i = 0; i < instructions.size(); i++) {
			instructions.set(i,formatInstruction(asArrayList(toString(instructions.get(i)))));
		}*/
	}
	public GuiButton btn_back;
	public GuiButton btn_funcs;
	@Override
	public void initGui() {
		int cX = gui.width/2;
		int cY = gui.height/2;
		btn_back = gui.addButton(new GuiButton(gui.currentButtonId(), gui.getGuiLeft()+7, gui.getGuiTop()+13, 30, 20, "Back"));
		btn_funcs = gui.addButton(new GuiButton(gui.currentButtonId(), gui.getGuiLeft()+54, gui.getGuiTop()+13, 58, 20, "Functions"));
		super.initGui();
		scrollXRect = new FiaUIRect(gui,65,237,178,5);
		scrollYRect = new FiaUIRect(gui,244,48,5,188);
	}
	@Override
	protected void actionPerformed(GuiButton button){
		IMixinGuiControlEdit mixin = (IMixinGuiControlEdit)gui;
		if(button == btn_back)
			mixin.leafia$popElement();
		//if (button == btn_funcs)
		//	mixin.leafia$pushElement(gui.variables);
	}
	@Override
	protected void enableButtons(boolean enable){
		btn_back.enabled = enable;
		btn_back.visible = enable;
		btn_funcs.enabled = enable;
		btn_funcs.visible = enable;
	}
	public static ResourceLocation texture = new ResourceLocation("leafia","textures/gui/control_panel/gui_ic10_editor.png");
	@Override
	protected void renderBackground(){
		gui.mc.getTextureManager().bindTexture(texture);
		gui.drawTexturedModalRect(gui.getGuiLeft(), gui.getGuiTop(), 0, 0, gui.getXSize()-5, gui.getYSize()-5);
	}
	// (\w+) = \{(\w+),(\w+),(\w+),(\w+)\};
	// public static final LeafiaColor $1 = new LeafiaColor($2/255d,$3/255d,$4/255d,$5/255d);
	String toString(ArrayList<Object> insn) {
		StringBuilder builder = new StringBuilder();
		for (Object object : insn) {
			if (object instanceof Number n) {
				for (int i = 0; i < n.intValue(); i++)
					builder.append(' ');
			} else if (object instanceof String s)
				builder.append(s);
			else
				throw new LeafiaDevFlaw("what");
		}
		return builder.toString();
	}
	int lenString(ArrayList<Object> insn) {
		int len = 0;
		for (Object object : insn) {
			if (object instanceof Number n)
				len += n.intValue();
			else if (object instanceof String s)
				len += s.length();
			else
				throw new LeafiaDevFlaw("what");
		}
		return len;
	}
	public static class EditorColors {
		public static final LeafiaColor plain = new LeafiaColor(0xFF/255d,0xFF/255d,0xFF/255d,255/255d);
		public static final LeafiaColor label = new LeafiaColor(0xFF/255d,0x88/255d,0xFF/255d,255/255d);
		public static final LeafiaColor comment = new LeafiaColor(0xAA/255d,0xAA/255d,0xAA/255d,127/255d);
		public static final LeafiaColor func = new LeafiaColor(0xFF/255d,0xAA/255d,0x30/255d,255/255d);
		public static final LeafiaColor number = new LeafiaColor(0x00/255d,0xAA/255d,0xAA/255d,255/255d);
		public static final LeafiaColor invalid = new LeafiaColor(0xFF/255d,0/255d,0/255d,255/255d);
		public static final LeafiaColor suggestion = new LeafiaColor(0xFF/255d,0x88/255d,0x88/255d,127/255d);
		public static final LeafiaColor register = new LeafiaColor(0x88/255d,0x88/255d,0xFF/255d,255/255d);
		public static final LeafiaColor string = new LeafiaColor(0.435,0.784,0.135,1);
	}
	Map<Integer,String> labels = new HashMap<>();
	Map<Integer,String> aliases = new HashMap<>();
	boolean LMBdown = false;
	int[] cursor = null;
	int[] selStart = null;
	void updateDefsForLine(int ln) {
		labels.remove(ln);
		aliases.remove(ln);
		ArrayList<Object> insn = instructions.get(ln);
		int arg = 0;
		int mode = -1;
		for (Object v0 : insn) {
			if (v0 instanceof String v) {
				arg++;
				if (v.equals("#")) return;
				if (arg == 1) {
					if (v.equals("alias"))
						mode = 0;
					else if (v.equals("define"))
						mode = 1;
					else if (v.length() >= 2 && v.endsWith(":")) {
						labels.put(ln,v.substring(0,v.length()-1));
						return;
					} else
						return;
				} else {
					if (mode == 0) {
						if (arg == 2)
							aliases.put(ln,v);
						else if (arg == 3) { // verify value
							if (v.equals("ra") || v.equals("sp"))
								return;
							else if (v.startsWith("r")) {
								Integer idx = tonumber(v.substring(1));
								if (idx != null && idx >= 0 && idx <= AddonConfig.ic10maxregisters)
									return;
								else
									break; // invalidate
							} else break; // invalidate
						}
					} else if (mode == 1) {
						if (arg == 2)
							labels.put(ln,v);
						else if (arg == 3) { // verify value
							if (tonumberd(v) != null)
								return;
							else break; // invalidate
						}
					}
				}
			}
		}
		labels.remove(ln);
		aliases.remove(ln);
	}
	public static final int textSpacing = 6;
	public static final int lineHeight = 12;
	public static final int heightOffset = 3;
	FontRenderer font = gui.getFontRenderer();
	boolean equals(Object a,Object b) {
		if (a == null)
			return b == null;
		return a.equals(b);
	}
	@Nullable Integer tonumber(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ignored) {}
		return null;
	}
	@Nullable Double tonumberd(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException ignored) {}
		return null;
	}
	public static class TypingData {
		List<String> words = new ArrayList<>();
		int index = -1;
	}
	TypingData typing = null;
	@Nullable TypingData getTypingData(ArrayList<Object> insn,int pos) {
		int current = 0;
		TypingData data = new TypingData();
		for (Object object : insn) {
			if (object instanceof String s)
				data.words.add(s);
		}
		for (Object object : insn) {
			if (object instanceof Number n)
				current += n.intValue();
			else if (object instanceof String s) {
				current += s.length();
				data.index++;
			}
			if (current >= pos) {
				if (data.index >= 0)
					return data;
				else
					return null;
			}
		}
		return null;
	}
	void renderInstruction(int ln) {
		ArrayList<Object> insn = instructions.get(ln);
		int offset = 0;
		int arg = 0;
		Object insnType = null;
		for (Object v0 : insn) {
			if (v0 instanceof Number num)
				offset += num.intValue();
			else {
				String v = (String)v0;
				LeafiaColor color = equals(insnType,"invalid") ? EditorColors.invalid : EditorColors.plain;
				arg++;
				Object lastInsnType = insnType;
				if (arg == 1) {
					insnType = "invalid";
					color = EditorColors.invalid;
					if (v.endsWith(":")) { insnType = "label"; color = EditorColors.label; }
					if (IC10.instructions.containsKey(v)) { insnType = IC10.instructions.get(v); color = EditorColors.func; }
					if (v.equals("define")) { insnType = IC10.define; color = EditorColors.label; }
					//if (cursor != null && ln == cursor[1] && insn.size() == 1)
					//	typing = v;
				}
				if (v.equals("#")) insnType = "comment";
				/*if (lastInsnType != insnType && lastInsnType instanceof IC10Instruction lastInsn) {
					if (arg-1 <= lastInsn.args.size()) {
						for (int sarg = arg-1; sarg <= lastInsn.size()-2; sarg++) {
							Object data = lastInsn.get(sarg-1);
							local ghost = data[1].."<"..ic10_type(data[2])..">"
							setLineText(item,ghost,offset,colors.suggestion)
							offset = offset + string.len(ghost)+1
						}
					}
				}*/ // what the fuck was this for
				if (equals(insnType,"comment")) color = EditorColors.comment;
				if (insnType instanceof IC10Instruction insnI && arg > 1) { // argument type coloring
					if (arg-1 <= insnI.args.size()) {
						IC10Argument data = insnI.args.get(arg-1-1);
						IC10Type ex_t = data.type;
						boolean ok = false;
						boolean forceNope = false;
						if (ex_t == IC10Type.NUMBER || ex_t == IC10Type.INTEGER || ex_t == IC10Type.STRING || ex_t == IC10Type.ANY) {
							if (insnI != IC10.define) { // label or registry checks (not usable in constant definition ofc)
								for (String l : labels.values()) { // constant check (pink)
									if (l.equals(v)) {
										ok = true;
										color = EditorColors.label;
										break;
									}
								}
								if (!ok && !forceNope) { // alias check (uncolored)
									for (Entry<Integer,String> entry : aliases.entrySet()) {
										int aln = entry.getKey();
										String a = entry.getValue();
										if (a.equals(v) && (/*ln == null ||*/ (ln >= aln))) {
											ok = true;
											break;
										}
									}
								}
								if (!ok && !forceNope) { // direct register check (blue)
									if (v.equals("ra") || v.equals("sp")) {
										ok = true;
										color = EditorColors.register;
									} else if (v.startsWith("r")) {
										Integer idx = tonumber(v.substring(1));
										if (idx != null && idx >= 0 && idx <= AddonConfig.ic10maxregisters) {
											ok = true;
											color = EditorColors.register;
										} else
											forceNope = true;
									}
								}
							}
							if (!ok && !forceNope) { // number check (turquoise)
								if (ex_t == IC10Type.INTEGER || ex_t == IC10Type.NUMBER || ex_t == IC10Type.ANY) {
									Double d = tonumberd(v);
									if (d != null) {
										ok = true;
										color = EditorColors.number;
										if (ex_t == IC10Type.INTEGER && (int) (double) d != d)
											ok = false;
									}
								}
							}
							if (!ok && !forceNope) {
								if (ex_t == IC10Type.STRING || ex_t == IC10Type.ANY) {
									if (v.startsWith("\"") && v.endsWith("\"")) {
										ok = true;
										color = EditorColors.string;
									}
								}
							}
							if (!ok)
								color = EditorColors.invalid;
						} else if (ex_t == IC10Type.REGISTER) {
							for (Entry<Integer,String> entry : aliases.entrySet()) {
								int aln = entry.getKey();
								String a = entry.getValue();
								if (a.equals(v) && (/*ln == null ||*/ (ln >= aln))) {
									ok = true;
									break;
								}
							}
							if (!ok && !forceNope) { // direct register check (blue)
								if (v.equals("ra") || v.equals("sp")) {
									ok = true;
									color = EditorColors.register;
								} else  if (v.startsWith("r")) {
									Integer idx = tonumber(v.substring(1));
									if (idx != null && idx >= 0 && idx <= AddonConfig.ic10maxregisters) {
										ok = true;
										color = EditorColors.register;
									}
								}
							}
							if (!ok)
								color = EditorColors.invalid;
						}
					} else // if this argument is out of bounds
						color = EditorColors.invalid;
				}
				setLineText(ln,v,offset,color);
				offset += v.length();
			}
		}
		if (insnType instanceof IC10Instruction insnI) {
			if (arg <= insnI.args.size()) {
				for (int sarg = arg; sarg <= insnI.args.size(); sarg++) {
					IC10Argument data = insnI.args.get(sarg-1);
					String ghost = data.name+"<"+data.type.name().toLowerCase()+">";
					setLineText(ln,ghost,offset,EditorColors.suggestion);
					offset = offset + ghost.length()+1;
				}
			}
		}
	}
	ArrayList<Object> formatInstruction(ArrayList<Object> insn) {
		boolean isComment = false;
		int needle = 0;
		while (needle < insn.size()) {
			Object obj = insn.get(needle);
			if (isComment) {
				if (obj instanceof Number n)
					insn.set(needle," ".repeat(n.intValue()));
				if (!insn.get(needle-1).equals("#")) {
					insn.set(needle-1,(String)insn.get(needle-1)+(String)obj);
					insn.remove(needle);
				} else
					needle++;
			} else if (obj.toString().startsWith("#")) {
				isComment = true;
				if (insn.get(needle).toString().length() > 1) {
					insn.add(needle+1,insn.get(needle).toString().substring(1));
					insn.set(needle,"#");
				}
				needle++;
			} else {
				if (obj instanceof String s) {
					int spaceStart = s.indexOf(' ');
					if (spaceStart != -1) {
						// note: spaceEnd here is 1 lower than position required by substring
						// start, end range is a number like in lua here
						int spaceEnd = spaceStart;
						while (spaceEnd+1 < s.length()) {
							if (s.charAt(spaceEnd+1) == ' ')
								spaceEnd++;
							else
								break;
						}
						int spaces = 1+spaceEnd-spaceStart;
						String subA = s.substring(0,spaceStart);
						String subB = s.substring(spaceEnd+1);
						insn.set(needle,subA);
						insn.add(needle+1,spaces);
						insn.add(needle+2,subB);
						obj = insn.get(needle);
					}
				}
				if (equals(obj,"") || equals(obj,0))
					insn.remove(needle);
				else {
					if (needle == 0)
						needle++;
					else {
						Object prev = insn.get(needle-1);
						if (prev instanceof Number n0 && obj instanceof Number n1) {
							insn.set(needle-1,n0.intValue()+n1.intValue());
							insn.remove(needle);
						} else if (prev instanceof String s0 && obj instanceof String s1) {
							insn.set(needle-1,s0+s1);
							insn.remove(needle);
						} else
							needle++;
					}
				}
			}
		}
		return insn;
	}
	void setLineText(int line,String text,int start,LeafiaColor color) {
		char[] c = text.toCharArray();
		//LeafiaGls.color(1,1,1,color.getAlpha());
		for (int i = 0; i < c.length; i++) {
			String s = Character.toString(c[i]);
			int width = font.getStringWidth(s);
			//if (width < textSpacing)
				font.drawString(s,(start+i)*textSpacing+(int)(textSpacing/2f-width/2f)+2,line*lineHeight+heightOffset,color.toARGB());
			/*else {
				LeafiaGls.pushMatrix();
				LeafiaGls.translate((start+i)*textSpacing+textSpacing/2f,line*lineHeight,0);
				LeafiaGls.scale((textSpacing-1f)/width,1,1);
				font.drawString(s,-width/2,0,color.toARGB()&0xFFFFFF);
				LeafiaGls.popMatrix();
			}*/
		}
		//LeafiaGls.color(1,1,1);
	}
	boolean posEquals(int[] p1,int[] p2) {
		if (p1 == null) return p2 == null;
		if (p2 == null) return p1 == null;
		return p1[0] == p2[0] && p1[1] == p2[1];
	}
	boolean isCursorBeforeSelectionStart() { // true if cursor < selStart
		if (cursor[0] < selStart[0]) return true;
		if (cursor[0] == selStart[0]) {
			if (cursor[1] < selStart[1])
				return true;
		}
		return false;
	}
	Pair<int[],int[]> getSelectionSorted() {
		boolean opposite = isCursorBeforeSelectionStart();
		int[] sst = opposite ? cursor : selStart;
		int[] sen = opposite ? selStart : cursor;
		return new Pair<>(sst,sen);
	}
	Pair<Integer,Integer> getSelectionRangeForLine(int ln,int[] sst,int[] sen) {
		if (sst == null) {
			Pair<int[],int[]> sorted = getSelectionSorted();
			sst = sorted.getA();
			sen = sorted.getB();
		}
		int pst;
		int pen;
		if (sen[0] == ln)
			pen = sen[1];
		else
			pen = lenString(instructions.get(ln));
		if (sst[0] == ln)
			pst = sst[1];
		else
			pst = 0;
		return new Pair<>(pst,pen);
	}
	void deleteSelection(int[] sst,int[] sen) {
		if (sst == null) {
			Pair<int[],int[]> sorted = getSelectionSorted();
			sst = sorted.getA();
			sen = sorted.getB();
		}
		cursor = sst;
		String startInsnStr = toString(instructions.get(sst[0]));
		preservePosition = cursor[1];
		String endInsnStr = (sen[0] == sst[0]) ? startInsnStr : toString(instructions.get(sen[0]));
		instructions.set(sst[0],formatInstruction(asArrayList(substringSafely(startInsnStr,0,sst[1])+substringSafely(endInsnStr,sen[1]))));
		int moveUp = sen[0]-sst[0];
		for (int i = 0; i < moveUp; i++)
			instructions.remove(sst[0]+1);
	}
	void drawRect(double x0,double y0,double x1,double y1,double r,double g,double b,double a) {
		gui.mc.getTextureManager().bindTexture(AddonBase.solid);
		LeafiaGls.color((float)r/255,(float)g/255,(float)b/255,(float)a/255);
		LeafiaBrush brush = LeafiaBrush.instance;
		brush.startDrawingQuads();
		brush.addVertexWithUV(x0,y1,0,0,1);
		brush.addVertexWithUV(x1,y1,0,1,1);
		brush.addVertexWithUV(x1,y0,0,1,0);
		brush.addVertexWithUV(x0,y0,0,0,0);
		brush.draw();
		LeafiaGls.color(1,1,1);
	}
	double cursorFlick = 0;
	long lastTimeMillis = System.currentTimeMillis();
	@Override
	protected void update() {
		if (LMBdown && selStart != null) {
			cursor = getPositionFromMouse();
			if (cursor != null)
				preservePosition = cursor[1];
		}
	}
	public static List<String> filterByPrefix(Collection<String> col,String pfx) {
		List<String> list = new ArrayList<>();
		for (String s : col) {
			if (s.toLowerCase().startsWith(pfx.toLowerCase()))
				list.add(s);
		}
		return list;
	}
	public static final float globalScale = 0.5f;
	public double scrollRatioX = 0;
	public double scrollRatioY = 0;
	public boolean scrollingX = false;
	public boolean scrollingY = false;
	public FiaUIRect scrollXRect;
	public FiaUIRect scrollYRect;
	public int scrollXCount = 1;
	public int scrollYCount = 1;
	public int scrollXThing = 1;
	public int scrollYThing = 1;
	public static DoubleBuffer dbuf = null;
	public static final int scrollAreaX = 178;
	public static final int scrollAreaY = 188;
	@Override
	protected void drawScreen() {
		if (dbuf == null)
			dbuf = GLAllocation.createDirectByteBuffer(16*4).asDoubleBuffer(); // i have no idea how this even works

		gui.mc.getTextureManager().bindTexture(texture);
		double dt = (System.currentTimeMillis()-lastTimeMillis)/1000d;
		lastTimeMillis = System.currentTimeMillis();
		LeafiaGls.disableLighting();

		// DRAWING SCROLLER
		scrollXCount = 1;
		for (ArrayList<Object> insn : instructions)
			scrollXCount = Math.max(scrollXCount,lenString(insn)/2+1);
		scrollYCount = instructions.size();
		double scrollXDiv = Math.max(1,scrollXCount)/30d;
		double scrollYDiv = Math.max(1,scrollYCount)/31d;
		int scrollBarXSize = (int)MathHelper.clamp(scrollAreaX/scrollXDiv,20,scrollAreaX);
		int scrollBarYSize = (int)MathHelper.clamp(scrollAreaY/scrollYDiv,20,scrollAreaY);
		scrollXThing = Math.max(0,scrollXCount-30);
		scrollYThing = Math.max(0,scrollYCount-31);
		if (scrollingX)
			scrollRatioX = ScrollUtil._getScrollRatio(scrollBarXSize,scrollAreaX,mouseX-65);
		if (scrollingY)
			scrollRatioY = ScrollUtil._getScrollRatio(scrollBarYSize,scrollAreaY,mouseY-48);
		{
			int pos = gui.guiLeft+65+ScrollUtil.getScrollBarPos(scrollBarXSize,scrollAreaX,scrollRatioX);
			gui.drawTexturedModalRect(pos,gui.guiTop+237,73,251,2,5);
			gui.drawTexturedModalRect(pos+scrollBarXSize-2,gui.guiTop+237,73+scrollAreaX-2,251,2,5);
			gui.drawTexturedModalRect(pos+2,gui.guiTop+237,73+2,251,scrollBarXSize-4,5);
		}
		{
			int pos = gui.guiTop+48+ScrollUtil.getScrollBarPos(scrollBarYSize,scrollAreaY,scrollRatioY);
			gui.drawTexturedModalRect(gui.guiLeft+244,pos,251,63,5,2);
			gui.drawTexturedModalRect(gui.guiLeft+244,pos+scrollBarYSize-2,251,63+scrollAreaY-2,5,2);
			gui.drawTexturedModalRect(gui.guiLeft+244,pos+2,251,63+2,5,scrollBarYSize-4);
		}

		// DRAWING EDITOR
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(gui.getGuiLeft()+65,gui.getGuiTop()+48,0);
		{
			GL11.glEnable(GL11.GL_CLIP_PLANE2);
			dbuf.put(new double[]{ 0,1,0,0 });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE2,dbuf);

			GL11.glEnable(GL11.GL_CLIP_PLANE3);
			dbuf.put(new double[]{ 0,-1,0,scrollAreaY });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE3,dbuf);
		}
		LeafiaGls.translate(0,-scrollYThing*lineHeight*scrollRatioY*globalScale,0);
		LeafiaGls.scale(globalScale);
		for (int i = 0; i < instructions.size(); i++)
			font.drawString(Integer.toString(i),-2-font.getStringWidth(Integer.toString(i)),i*lineHeight+heightOffset,0xAAAAAA);
		LeafiaGls.popMatrix();

		LeafiaGls.pushMatrix();
		LeafiaGls.translate(gui.getGuiLeft()+65,gui.getGuiTop()+48,0);
		{ // i had to vibecode this shit
			GL11.glEnable(GL11.GL_CLIP_PLANE0);
			dbuf.put(new double[]{ 1,0,0,0 });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE0,dbuf);

			GL11.glEnable(GL11.GL_CLIP_PLANE1);
			dbuf.put(new double[]{ -1,0,0,scrollAreaX });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE1,dbuf);
		}
		LeafiaGls.translate(-scrollXThing*2*textSpacing*scrollRatioX*globalScale,-scrollYThing*lineHeight*scrollRatioY*globalScale,0);
		LeafiaGls.scale(globalScale);
		for (int i = 0; i < instructions.size(); i++)
			updateDefsForLine(i);
		for (int i = 0; i < instructions.size(); i++)
			renderInstruction(i);
		if (cursor != null) {
			if (selStart != null && !posEquals(cursor,selStart)) {
				Pair<int[],int[]> sorted = getSelectionSorted();
				int[] sst = sorted.getA();
				int[] sen = sorted.getB();
				for (int ln = sst[0]; ln <= sen[0]; ln++) {
					Pair<Integer,Integer> range = getSelectionRangeForLine(ln,sst,sen);
					int rs = range.getA();
					int re = range.getB();
					int start = rs*textSpacing+2;
					if (rs == 0)
						start = 0;
					drawRect(start,ln*lineHeight,re*textSpacing+2,(ln+1)*lineHeight,28*2,79*2,111*2,64);
				}
			}
			cursorFlick = (cursorFlick+dt)%0.75;
			double flk = Math.floor(cursorFlick/0.375)+1;
			int x = cursor[1]*textSpacing+2;
			int y = cursor[0]*lineHeight;
			drawRect(x-0.5,y,x+0.5,y+lineHeight,28*flk,79*flk,111*flk,255);
		}
		GL11.glDisable(GL11.GL_CLIP_PLANE0);
		GL11.glDisable(GL11.GL_CLIP_PLANE1);
		GL11.glDisable(GL11.GL_CLIP_PLANE2);
		GL11.glDisable(GL11.GL_CLIP_PLANE3);
		if (cursor != null && typing != null) {
			int x = cursor[1]*textSpacing+2;
			int y = (cursor[0]+1)*lineHeight;
			List<String> suggestions = new ArrayList<>();
			if (typing.index == 0) {
				List<String> insns = filterByPrefix(IC10.instructions.keySet(),typing.words.get(typing.index));
				for (String insn : insns) {
					IC10Instruction instruction = IC10.instructions.get(insn);
					StringBuilder args = new StringBuilder();
					for (IC10Argument arg : instruction.args)
						args.append(" ").append(arg.name).append("<").append(arg.type.name().toLowerCase()).append(">");
					suggestions.add(insn+TextFormatting.DARK_GRAY+args);
				}
			} else {
				if (IC10.instructions.containsKey(typing.words.get(0))) {
					IC10Instruction instruction = IC10.instructions.get(typing.words.get(0));
					int argindex = typing.index-1;
					if (argindex < instruction.args.size()) {
						IC10Argument arg = instruction.args.get(argindex);
						if (arg.type != IC10Type.NAME && arg.type != IC10Type.REGISTER) {
							suggestions.addAll(filterByPrefix(aliases.values(),typing.words.get(typing.index)));
							suggestions.addAll(filterByPrefix(labels.values(),typing.words.get(typing.index)));
						} else if (arg.type == IC10Type.REGISTER)
							suggestions.addAll(filterByPrefix(aliases.values(),typing.words.get(typing.index)));
					}
				}
			}
			Collections.sort(suggestions);
			if (!suggestions.isEmpty()) {
				LeafiaGls.pushMatrix();
				LeafiaGls.translate(x-10,y,0);
				gui.drawHoveringText(suggestions,0,0);
				LeafiaGls.popMatrix();
			}
		} else {
			int[] hovering = getPositionFromMouse();
			if (hovering != null && hovering[0] >= 0 && hovering[0] < instructions.size()) {
				ArrayList<Object> insn = instructions.get(hovering[0]);
				int x = hovering[1]*textSpacing+2;
				int y = (hovering[0]+1)*lineHeight;
				TypingData data = getTypingData(insn,hovering[1]);
				if (data != null) {
					if (data.index == 0) {
						if (IC10.instructions.containsKey(data.words.get(0))) {
							IC10Instruction instruction = IC10.instructions.get(data.words.get(0));
							StringBuilder args = new StringBuilder();
							for (IC10Argument arg : instruction.args)
								args.append(" ").append(arg.name).append("<").append(arg.type.name().toLowerCase()).append(">");
							List<String> list = new ArrayList<>();
							if (!args.isEmpty())
								list.add(TextFormatting.DARK_GRAY+args.substring(1));
							list.addAll(Arrays.asList(instruction.desc));
							LeafiaGls.pushMatrix();
							LeafiaGls.translate(x-10,y,0);
							gui.drawHoveringText(list,0,0);
							LeafiaGls.popMatrix();
						}
					}
				}
			}
		}
		LeafiaGls.enableLighting();
		LeafiaGls.popMatrix();
	}
	int mouseX = 0;
	int mouseY = 0;
	public void handleMouseInput() {
		int i = Mouse.getEventX() * gui.width / gui.mc.displayWidth;
		int j = gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
		i -= gui.getGuiLeft();
		j -= gui.getGuiTop();
		mouseX = i;
		mouseY = j;
		double delta = -MathHelper.clamp(Mouse.getEventDWheel(),-1,1);
		if (delta != 0) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
				scrollRatioX = MathHelper.clamp(scrollRatioX+delta/Math.max(scrollXThing,1),0,1);
			else
				scrollRatioY = MathHelper.clamp(scrollRatioY+delta/Math.max(scrollYThing,1),0,1);
		}
	}
	int[] getPositionFromMouse() {
		if (mouseX >= 65 && mouseY >= 48 && mouseX <= 65+178 && mouseY <= 48+188) {
			double offsetMouseX = mouseX;
			double offsetMouseY = mouseY;
			offsetMouseX += scrollXThing*2*textSpacing*scrollRatioX*globalScale;
			offsetMouseY += scrollYThing*lineHeight*scrollRatioY*globalScale;
			int line = (int)((offsetMouseY-48)/globalScale)/lineHeight;
			if (line >= 0 && line < instructions.size())
				return new int[]{line,(int)Math.min(Math.floor((offsetMouseX-65d)/globalScale/textSpacing+0.25),lenString(instructions.get(line)))};
		}
		return null;
	}
	@Override
	protected void mouseClicked(int mouseX,int mouseY,int button) {
		if (button == 0) {
			typing = null;
			LMBdown = true;
			cursor = getPositionFromMouse();
			if (cursor != null)
				preservePosition = cursor[1];
			selStart = cursor;
			if (scrollXRect.isMouseIn(mouseX,mouseY))
				scrollingX = true;
			if (scrollYRect.isMouseIn(mouseX,mouseY))
				scrollingY = true;
		}
	}
	@Override
	protected void mouseReleased(int mouseX,int mouseY,int state) {
		if (state == 0) {
			LMBdown = false;
			scrollingX = false;
			scrollingY = false;
		}
	}
	void addUndo(UndoData data) {
		if (undoCurrent+1 < undoHistory.size())
			undoHistory.subList(undoCurrent+1,undoHistory.size()).clear();
		undoHistory.add(data);
		undoCurrent = undoHistory.size()-1;
		lastUndo = data;
	}
	void undo(boolean isRedo) {
		int index = isRedo ? undoCurrent+1 : undoCurrent;
		if (index >= 0 && index < undoHistory.size()) {
			UndoData change = undoHistory.get(index);
			undoCurrent = isRedo ? undoCurrent+1 : undoCurrent-1;
			if (change instanceof UndoDataAddRemove addrmv) {
				int ln = addrmv.line;
				int pos = addrmv.pos;
				boolean doRemove = addrmv.isAdd; // undoing an addition means it "removing it" so
				if (isRedo) doRemove = !doRemove;
				if (doRemove) {
					removeString(ln,pos+addrmv.str.length()-1,addrmv.str.length());
					cursor = new int[]{ln,pos-1};
				} else {
					addString(ln,pos,addrmv.str);
					cursor = new int[]{ln,pos+addrmv.str.length()-1};
				}
				selStart = cursor;
			} else if (change instanceof UndoDataReplace replace) {
				int lst = replace.lst; // linestart
				ArrayList<ArrayList<Object>> newData = isRedo ? replace.after : replace.before;
				ArrayList<ArrayList<Object>> curData = isRedo ? replace.before : replace.after;
				int move = newData.size()-curData.size();
				for (int ln = lst; ln <= lst-1+Math.min(newData.size(),curData.size()); ln++)
					instructions.set(ln,newData.get(ln-lst));
				if (move > 0) {
					ArrayList<ArrayList<Object>> addLines = new ArrayList<>();
					for (int lno = 1; lno <= move; lno++)
						addLines.add(newData.get(curData.size()+lno-1));
					instructions.addAll(lst+curData.size(),addLines);
				} else {
					for (int i = 0; i < -move; i++)
						instructions.remove(lst+newData.size());
				}
				int[] targetCursorPos = isRedo ? replace.cursorAfter : replace.cursorBefore;
				if (targetCursorPos != null) {
					cursor = targetCursorPos;
					selStart = cursor;
				}
			}
		}
	}
	void copySelection(int[] sst,int[] sen) {
		if (sst == null) {
			Pair<int[],int[]> sorted = getSelectionSorted();
			sst = sorted.getA();
			sen = sorted.getB();
		}
		StringBuilder copyLines = new StringBuilder();
		for (int ln = sst[0]; ln <= sen[0]; ln++) {
			Pair<Integer,Integer> range = getSelectionRangeForLine(ln,sst,sen);
			String s = substringSafely(toString(instructions.get(ln)),range.getA(),range.getB());
			if (ln == sst[0])
				copyLines = new StringBuilder(s);
			else
				copyLines.append("\n").append(s);
		}
		GuiScreen.setClipboardString(copyLines.toString());
	}
	int preservePosition = 0;
	boolean ignoreCTRLflag = false;
	@Override
	protected void keyTyped(char chr,int code) {
		if (code != Keyboard.KEY_LSHIFT && code != Keyboard.KEY_RSHIFT && code != Keyboard.KEY_LCONTROL && code != Keyboard.KEY_RCONTROL)
			typing = null;
		boolean SHIFTdown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
		boolean CTRLdown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
		if (ignoreCTRLflag) CTRLdown = false;
		if (CTRLdown && code == Keyboard.KEY_Z)
			undo(false);
		else if (CTRLdown && code == Keyboard.KEY_Y)
			undo(true);
		else if (cursor != null) {
			cursorFlick = 0.375;
			if (cursor[0] >= 0 && cursor[0] < instructions.size()) {
				ArrayList<Object> insn = instructions.get(cursor[0]);
				boolean isArrowKey = false;
				if (code == Keyboard.KEY_LEFT) {
					if (cursor[1] <= 0 && cursor[0] > 0)
						cursor = new int[]{cursor[0]-1,lenString(instructions.get(cursor[0]-1))};
					else
						cursor[1] = Math.max(cursor[1]-1,0);
					preservePosition = cursor[1];
					isArrowKey = true;
				} else if (code == Keyboard.KEY_RIGHT) {
					if (cursor[1] >= lenString(instructions.get(cursor[0])) && cursor[0]+1 < instructions.size())
						cursor = new int[]{cursor[0]+1,0};
					else
						cursor[1] = Math.min(cursor[1]+1,lenString(instructions.get(cursor[0])));
					preservePosition = cursor[1];
					isArrowKey = true;
				} else if (code == Keyboard.KEY_UP) {
					int index = Math.max(cursor[0]-1,0);
					cursor = new int[]{index,Math.min(lenString(instructions.get(index)),preservePosition)};
					isArrowKey = true;
				} else if (code == Keyboard.KEY_DOWN) {
					int index = Math.min(cursor[0]+1,instructions.size()-1);
					cursor = new int[]{index,Math.min(lenString(instructions.get(index)),preservePosition)};
					isArrowKey = true;
				} else if (CTRLdown) {
					if (code == Keyboard.KEY_C)
						copySelection(null,null);
					else if (code == Keyboard.KEY_X) {
						copySelection(null,null);
						ignoreCTRLflag = true;
						keyTyped('?',Keyboard.KEY_BACK); // press backspace
						ignoreCTRLflag = false;
					} else if (code == Keyboard.KEY_V) {
						ArrayList<ArrayList<Object>> backup = new ArrayList<>();
						if (!posEquals(selStart,cursor)) {
							Pair<int[],int[]> sorted = getSelectionSorted();
							int[] sst = sorted.getA();
							int[] sen = sorted.getB();
							for (int ln = sst[0]; ln <= sen[0]; ln++)
								backup.add(instructions.get(ln));
							deleteSelection(sst,sen);
							cursor = sst;
						} else
							backup.add(instructions.get(cursor[0]));
						int startline = cursor[0];
						ArrayList<ArrayList<Object>> newinsns = new ArrayList<>();

						String strInsn = toString(instructions.get(cursor[0]));
						int indent = 0;
						ArrayList<Object> curInsn = instructions.get(cursor[0]);
						if (!curInsn.isEmpty()) {
							Object first = curInsn.get(0);
							if (first instanceof Number n)
								indent = n.intValue();
						}
						String pre = substringSafely(strInsn,0,cursor[1]);
						String post = substringSafely(strInsn,cursor[1]);
						int sidePos = 0;
						String[] clipboard = GuiScreen.getClipboardString().split("\n");
						for (int lno = 0; lno < clipboard.length; lno++) {
							String s = clipboard[lno];
							sidePos = s.length();
							if (lno == clipboard.length-1)
								s = s+post;
							if (lno == 0) {
								instructions.set(cursor[0],formatInstruction(asArrayList(pre+s)));
								sidePos += pre.length();
							} else
								// ah forget about fucking indents
								instructions.add(cursor[0]+lno,formatInstruction(asArrayList(s)));
							newinsns.add(instructions.get(cursor[0]+lno));
						}

						int[] cursorBefore = cursor;

						cursor = new int[]{cursor[0]+clipboard.length-1,sidePos};
						selStart = cursor;
						preservePosition = cursor[1];

						addUndo(new UndoDataReplace(
								startline,
								backup,
								newinsns,
								cursorBefore,
								cursor
						));
					} else if (code == Keyboard.KEY_A) {
						int index = instructions.size()-1;
						selStart = new int[]{0,0};
						cursor = new int[]{index,lenString(instructions.get(index))};
					}
					return;
				} else {
					if (code != Keyboard.KEY_BACK) {
						if (code == Keyboard.KEY_RETURN) {
							ArrayList<Object> curInsn = instructions.get(cursor[0]);
							String strInsn = toString(curInsn);
							int indent = 0;
							if (!curInsn.isEmpty()) {
								Object first = curInsn.get(0);
								if (first instanceof Number n)
									indent = n.intValue();
							}
							instructions.set(cursor[0],formatInstruction(asArrayList(strInsn.substring(0,cursor[1]))));
							instructions.add(cursor[0]+1,formatInstruction(asArrayList(indent,strInsn.substring(cursor[1]))));
							cursor = new int[]{cursor[0]+1,indent};
							preservePosition = cursor[1];
						} else {
							if (code == Keyboard.KEY_TAB) {
								cursor[1] = cursor[1]+2;
								addString(cursor[0],cursor[1],"  ");
								boolean undoAppended = false;
								if (lastUndo != null && lastUndo instanceof UndoDataAddRemove addrmv && addrmv.isAdd && undoCurrent == undoHistory.size()-1) {
									if (addrmv.line == cursor[0]) {
										if (addrmv.pos == cursor[1]-addrmv.str.length()) {
											addrmv.str = addrmv.str + "  ";
											undoAppended = true;
										}
									}
								}
								if (!undoAppended) {
									addUndo(new UndoDataAddRemove(
											cursor[0],
											cursor[1],
											"  ",
											true
									));
								}
							} else if (!Character.isISOControl(chr)) {
								cursor[1] = cursor[1]+1;
								addString(cursor[0],cursor[1],String.valueOf(chr));
								boolean undoAppended = false;
								if (lastUndo != null && lastUndo instanceof UndoDataAddRemove addrmv && addrmv.isAdd && undoCurrent == undoHistory.size()-1) {
									if (addrmv.line == cursor[0]) {
										if (addrmv.pos == cursor[1]-addrmv.str.length()) {
											addrmv.str = addrmv.str + chr;
											undoAppended = true;
										}
									}
								}
								if (!undoAppended) {
									addUndo(new UndoDataAddRemove(
											cursor[0],
											cursor[1],
											String.valueOf(chr),
											true
									));
								}
							}
							typing = getTypingData(instructions.get(cursor[0]),cursor[1]);
						}
					} else if (!posEquals(selStart,cursor)) {
						Pair<int[],int[]> sorted = getSelectionSorted();
						int[] sst = sorted.getA();
						int[] sen = sorted.getB();
						ArrayList<ArrayList<Object>> backup = new ArrayList<>();
						for (int ln = sst[0]; ln <= sen[0]; ln++)
							backup.add(instructions.get(ln));
						int[] cursorBkp = cursor;
						deleteSelection(sst,sen);
						addUndo(new UndoDataReplace(
								sst[0],
								backup,
								asArrayList(instructions.get(sst[0])),
								cursorBkp,
								cursor
						));
					} else if (cursor[1] > 0) {
						String chr0 = substringSafely(toString(instructions.get(cursor[0])),cursor[1]-1,cursor[1]);
						removeString(cursor[0],cursor[1],1);
						insn = instructions.get(cursor[0]);
						boolean undoAppended = false;
						if (lastUndo != null && lastUndo instanceof UndoDataAddRemove addrmv && !addrmv.isAdd && undoCurrent == undoHistory.size()-1) {
							if (addrmv.line == cursor[0]) {
								if (addrmv.pos == cursor[1]+1) {
									addrmv.str = chr0+addrmv.str;
									addrmv.pos -= 1;
									undoAppended = true;
								}
							}
						}
						if (!undoAppended) {
							addUndo(new UndoDataAddRemove(
									cursor[0],
									cursor[1],
									String.valueOf(chr0),
									false
							));
						}
						cursor[1] = Math.max(cursor[1]-1,0);
						preservePosition = cursor[1];
					} else if (cursor[0] > 0) {
						int[] newcursor = {cursor[0]-1,lenString(instructions.get(cursor[0]-1))};
						instructions.set(cursor[0]-1,formatInstruction(asArrayList(toString(instructions.get(cursor[0]-1))+toString(instructions.get(cursor[0])))));
						instructions.remove(cursor[0]);
						cursor = newcursor;
						preservePosition = cursor[1];
					}
				}
				if (!SHIFTdown || !isArrowKey)
					selStart = new int[]{cursor[0],cursor[1]};
			}
		}
	}
	@Override
	public void onElementOpen() {
		Keyboard.enableRepeatEvents(true);
	}
	@Override
	public void onElementClose() {
		Keyboard.enableRepeatEvents(false);
	}
	@Override
	public void onClose() {
		Keyboard.enableRepeatEvents(false);
	}
}