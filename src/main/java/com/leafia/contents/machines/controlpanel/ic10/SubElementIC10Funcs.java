package com.leafia.contents.machines.controlpanel.ic10;

import com.hbm.inventory.control_panel.GuiControlEdit;
import com.hbm.inventory.control_panel.SubElement;
import com.leafia.contents.machines.controlpanel.ic10.IC10.IC10Argument;
import com.leafia.contents.machines.controlpanel.ic10.IC10.IC10Instruction;
import com.leafia.dev.LeafiaUtil.ScrollUtil;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.overwrite_contents.interfaces.IMixinGuiControlEdit;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.net.URI;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SubElementIC10Funcs extends SubElement {
	public static ResourceLocation texture = new ResourceLocation("leafia","textures/gui/control_panel/gui_ic10_funcs.png");
	public static ResourceLocation extra = new ResourceLocation("leafia","textures/gui/control_panel/gui_ic10_funcs_extra.png");
	public SubElementIC10Funcs(GuiControlEdit gui) {
		super(gui);
	}
	public FiaUIRect scrollRect;
	public GuiButton btn_back;
	public GuiButton btn_src;
	public static class ListObject {
		int parenthesis = 0;
	}
	public static class ListInstruction extends ListObject {
		public List<String> list = new ArrayList<>();
	}
	public static class ListCategory extends ListObject {
		public String name;
		public List<ListObject> contents = new ArrayList<>();
		public FiaUIRect expandButton;
		public boolean unfolded = false;
		public boolean visible = false;
	}
	int parenthesisOffset = 6;
	public List<ListObject> contents = new ArrayList<>();
	public List<ListCategory> cats = new ArrayList<>();
	public void addContents(List<ListObject> contents,Map<String,Object> map,int parenthesis) {
		for (Entry<String,Object> entry : map.entrySet()) {
			if (entry.getValue() instanceof Map<?,?> category) {
				ListCategory item = new ListCategory();
				item.parenthesis = parenthesis;
				item.expandButton = new FiaUIRect(gui,55+(2+parenthesis*parenthesisOffset)/2,15,9/2,9/2);
				item.name = entry.getKey();
				//noinspection unchecked
				addContents(item.contents,(Map<String,Object>)category,parenthesis+1);
				contents.add(item);
				cats.add(item);
			} else if (entry.getValue() instanceof IC10Instruction insn) {
				ListInstruction item = new ListInstruction();
				item.parenthesis = parenthesis;
				StringBuilder title = new StringBuilder(TextFormatting.YELLOW+entry.getKey()+TextFormatting.GRAY);
				for (IC10Argument arg : insn.args)
					title.append(" ").append(arg.name).append("<").append(arg.type.name().toLowerCase()).append(">");
				item.list.add(title.toString());
				item.list.addAll(Arrays.asList(insn.desc));
				contents.add(item);
			}
		}
	}
	@Override
	public void initGui() {
		int cX = gui.width/2;
		int cY = gui.height/2;
		btn_back = gui.addButton(new GuiButton(gui.currentButtonId(), gui.getGuiLeft()+7, gui.getGuiTop()+13, 30, 20, "Back"));
		btn_src = gui.addButton(new GuiButton(gui.currentButtonId(), gui.getGuiLeft()+7, gui.getGuiTop()+45, 40, 20, "Source"));
		super.initGui();
		scrollRect = new FiaUIRect(gui,236,13,12,229);
		contents.clear();
		cats.clear();
		addContents(contents,IC10.insn_def,0);
	}
	@Override
	protected void renderBackground(){
		gui.mc.getTextureManager().bindTexture(texture);
		gui.drawTexturedModalRect(gui.getGuiLeft(), gui.getGuiTop(), 0, 0, gui.getXSize()-5, gui.getYSize()-5);
	}
	public void drawStretchedModal(int x,int y,int tx,int ty,int tw,int th,int w,int h) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x,y,0);
		LeafiaGls.scale((double)w/tw,(double)h/th,1);
		gui.drawTexturedModalRect(0,0,tx,ty,tw,th);
		LeafiaGls.popMatrix();
	}
	public void drawPanel(int x,int y,int w,int h) {
		gui.mc.getTextureManager().bindTexture(extra);

		drawStretchedModal(x,y,24,0,1,1,1,1);
		drawStretchedModal(x+w-1,y,26,0,1,1,1,1);
		drawStretchedModal(x+w-1,y+h-1,26,2,1,1,1,1);
		drawStretchedModal(x,y+h-1,24,2,1,1,1,1);

		drawStretchedModal(x+1,y,25,0,1,1,w-2,1);
		drawStretchedModal(x+1,y+h-1,25,2,1,1,w-2,1);
		drawStretchedModal(x,y+1,24,1,1,1,1,h-2);
		drawStretchedModal(x+w-1,y+1,26,1,1,1,1,h-2);

		drawStretchedModal(x+1,y+1,25,1,1,1,w-2,h-2);
	}
	public static void openSource() {
		try {
			if (Desktop.isDesktopSupported())
				Desktop.getDesktop().browse(new URI("https://github.com/ntmleafia/NTM-Cursed-Addon/blob/main/src/main/java/com/leafia/contents/machines/controlpanel/ic10/IC10.java"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void actionPerformed(GuiButton button){
		IMixinGuiControlEdit mixin = (IMixinGuiControlEdit)gui;
		if(button == btn_back)
			mixin.leafia$popElement();
		if (button == btn_src)
			openSource();
	}
	@Override
	protected void enableButtons(boolean enable) {
		btn_back.enabled = enable;
		btn_back.visible = enable;
		btn_src.enabled = enable;
		btn_src.visible = enable;
	}
	public static DoubleBuffer dbuf = null;
	public int scroll = 0;
	public int getContentHeight() {
		return lastY;
	}
	public int getMaxScroll() {
		return getContentHeight()-229;
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
		if (delta != 0)
			scroll = MathHelper.clamp(scroll+(int)(delta*10),0,Math.max(getMaxScroll(),0));
	}
	boolean scrollin = false;
	@Override
	protected void mouseClicked(int mouseX,int mouseY,int button) {
		if (button == 0) {
			if (scrollRect.isMouseIn(mouseX,mouseY))
				scrollin = true;
			for (ListCategory cat : cats) {
				if (cat.expandButton.isMouseIn(mouseX,mouseY) && cat.visible) {
					gui.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK,2));
					cat.unfolded = !cat.unfolded;
				}
			}
		}
	}
	@Override
	protected void mouseReleased(int mouseX,int mouseY,int state) {
		if (state == 0)
			scrollin = false;
	}
	int lastY = 0;
	public void iterate(List<ListObject> list) {
		for (ListObject obj : list) {
			if (obj instanceof ListCategory cat) {
				int o = parenthesisOffset*cat.parenthesis;
				drawPanel(o,lastY,175*2-o,13);
				if (cat.expandButton.isMouseIn(mouseX+gui.guiLeft,mouseY+gui.guiTop))
					LeafiaGls.color(0.9f,0.9f,0.9f);
				gui.drawTexturedModalRect(2+o,lastY+2,cat.unfolded ? 9 : 0,15,9,9);
				LeafiaGls.color(1,1,1);
				gui.getFontRenderer().drawString(cat.name,12+o,lastY+3,0xFFFFFF);
				cat.expandButton.y = 13+(lastY+2)/2-scroll;
				lastY += 13;
				cat.visible = true;
				if (cat.unfolded)
					iterate(cat.contents);
			} else if (obj instanceof ListInstruction insn) {
				int o = parenthesisOffset*insn.parenthesis;
				int space = 10;
				int h = insn.list.size()*space+3;
				drawPanel(o,lastY,175*2-o,h);
				for (int i = 0; i < insn.list.size(); i++)
					gui.getFontRenderer().drawString(insn.list.get(i),2+o,lastY+3+space*i,0xFFFFFF);
				lastY += h;
			}
		}
	}
	public void updateList() {
		for (ListCategory cat : cats)
			cat.visible = false;
		lastY = 0;
		LeafiaGls.pushMatrix();
		LeafiaGls.scale(0.5);
		iterate(contents);
		LeafiaGls.popMatrix();
		lastY /= 2;
	}
	@Override
	protected void drawScreen() {
		if (dbuf == null)
			dbuf = GLAllocation.createDirectByteBuffer(16*4).asDoubleBuffer(); // i have no idea how this even works
		int maxScroll = Math.max(getMaxScroll(),0);
		LeafiaGls.disableLighting();
		scroll = MathHelper.clamp(scroll,0,maxScroll);
		int scrollBarPos = 0;
		if (maxScroll > 0) {
			if (scrollin)
				scroll = ScrollUtil._getScrollOffset(15,229,mouseY-13,maxScroll);
			scrollBarPos = ScrollUtil.getScrollBarPos(15,229,ScrollUtil._getScrollRatio(scroll,maxScroll));
		}
		gui.mc.getTextureManager().bindTexture(extra);
		LeafiaGls.color(1,1,1);
		gui.drawTexturedModalRect(gui.getGuiLeft()+236,gui.getGuiTop()+13+scrollBarPos,(maxScroll > 0) ? 0 : 12,0,12,15);

		LeafiaGls.pushMatrix();
		LeafiaGls.translate(gui.getGuiLeft()+55,gui.getGuiTop()+13,0);
		{
			GL11.glEnable(GL11.GL_CLIP_PLANE2);
			dbuf.put(new double[]{ 0,1,0,0 });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE2,dbuf);

			GL11.glEnable(GL11.GL_CLIP_PLANE3);
			dbuf.put(new double[]{ 0,-1,0,229 });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE3,dbuf);
		}
		LeafiaGls.pushMatrix();
		{
			LeafiaGls.translate(0,-scroll,0);
			gui.mc.getTextureManager().bindTexture(extra);
			LeafiaGls.color(1,1,1);
			updateList();
			LeafiaGls.popMatrix();
		}
		GL11.glDisable(GL11.GL_CLIP_PLANE2);
		GL11.glDisable(GL11.GL_CLIP_PLANE3);
		LeafiaGls.enableLighting();
		LeafiaGls.popMatrix();
	}
}
