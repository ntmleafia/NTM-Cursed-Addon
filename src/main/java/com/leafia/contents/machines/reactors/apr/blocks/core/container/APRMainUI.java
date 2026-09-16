package com.leafia.contents.machines.reactors.apr.blocks.core.container;

import com.hbm.inventory.gui.element.GUIElements;
import com.hbm.lib.Library;
import com.hbm.util.I18nUtil;
import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE;
import com.leafia.contents.machines.reactors.apr.blocks.core.APRCoreTE.APRChamber;
import com.leafia.dev.LeafiaClientUtil;
import com.leafia.dev.container_utility.LeafiaPacket;
import com.leafia.dev.gui.FiaUIRect;
import com.leafia.dev.gui.LCEGuiInfoContainer;
import com.leafia.dev.render.LeafiaBrush;
import com.leafia.dev.render.LeafiaBrush.BrushMode;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.Arrays;

import static com.hbm.render.NTMRenderHelper.bindTexture;
import static com.leafia.AddonBase.getIntegrated;

public class APRMainUI extends LCEGuiInfoContainer {
	public final APRCoreTE te;
	public static final ResourceLocation tex = getIntegrated("machines/reactors/apr/gui_main.png");
	public APRMainUI(EntityPlayer player,APRCoreTE te) {
		super(new APRMainContainer(player,te));
		this.te = te;
		xSize = 211;
		ySize = 220;
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX,int mouseY) {
		String name = this.te.hasCustomName() ? this.te.getName() : I18n.format(this.te.getName(),new Object[0]);
		this.fontRenderer.drawString(name,this.xSize/2-this.fontRenderer.getStringWidth(name)/2,4,4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory"),17+8,this.ySize-96+2,4210752);
		this.fontRenderer.drawString(I18n.format("tile.apr_core.gui.output"),138,24,4210752);
		this.fontRenderer.drawString(I18n.format("tile.apr_core.gui.control"),138,51,4210752);
		this.fontRenderer.drawString(I18n.format("tile.apr_core.gui.rps"),176,51,4210752);
		String power = Library.getShortNumber(te.getPowerOutput()*20)+"HE/s";
		this.fontRenderer.drawString(power, 181+29-10+2-this.fontRenderer.getStringWidth(power), 37, 0xD84EDB);
	}
	static DoubleBuffer dbuf = null;
	public void drawGraph() {
		if (dbuf == null)
			dbuf = GLAllocation.createDirectByteBuffer(16*4).asDoubleBuffer(); // i have no idea how this even works
		float scale = 0.25f;
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(guiLeft+9,guiTop+84,0);
		enableClipPlanes();
		{
			LeafiaGls.scale(scale);
			float border = 4;
			float width = 50/scale;
			float height = 25/scale-border*2;
			int divisions = 8;
			LeafiaBrush brush = LeafiaBrush.instance;
			for (int i = 0; i <= divisions; i++) {
				float ratio = i/(float)divisions;
				float y = height*(1-ratio)+border;
				LeafiaGls.pushMatrix();
				LeafiaGls.translate(0,y,0);
				LeafiaGls.disableTexture2D();
				float brightness = 0.126f;
				String s = Integer.toString(8192*i/divisions);
				brush.startDrawing(BrushMode.LINES,DefaultVertexFormats.POSITION_COLOR);
				brush.addVertexWithColor(fontRenderer.getStringWidth(s)+1,0,0,brightness,brightness,brightness,1);
				brush.addVertexWithColor(width,0,0,brightness,brightness,brightness,1);
				brush.draw();
				LeafiaGls.enableTexture2D();
				LeafiaGls.translate(0,-3.5,0);
				fontRenderer.drawString(s,0,0,0x3f3f3f);
				LeafiaGls.popMatrix();
				LeafiaGls.color(1,1,1);
			}
			for (int j = 0; j < te.chamberDatas.size(); j++) {
				double lastY = 0;
				for (int i = 0; i < APRCoreTE.graphXSegments; i++) {
					double xRatio0 = (i-1)/(APRCoreTE.graphXSegments-1d);
					double xRatio1 = i/(APRCoreTE.graphXSegments-1d);
					APRChamber data = te.chamberDatas.get(j);
					float alpha = j == te.page ? 1 : 0.4f;
					double yRatio = (1-data.graph[i]/8192d)*height+border;
					if (i > 0) {
						LeafiaGls.disableTexture2D();
						brush.startDrawing(BrushMode.LINES,DefaultVertexFormats.POSITION_COLOR);
						brush.addVertexWithColor(xRatio0*width,lastY,0,0,1,0,alpha);
						brush.addVertexWithColor(xRatio1*width,yRatio,0,0,1,0,alpha);
						brush.draw();
						LeafiaGls.enableTexture2D();
					}
					lastY = yRatio;
				}
			}
		}
		GL11.glDisable(GL11.GL_CLIP_PLANE0);
		GL11.glDisable(GL11.GL_CLIP_PLANE1);
		GL11.glDisable(GL11.GL_CLIP_PLANE2);
		GL11.glDisable(GL11.GL_CLIP_PLANE3);
		LeafiaGls.popMatrix();
		bindTexture(tex);
	}
	public void enableClipPlanes() {
		GL11.glEnable(GL11.GL_CLIP_PLANE0);
		dbuf.put(new double[]{ 1,0,0,0 });
		dbuf.rewind();
		GL11.glClipPlane(GL11.GL_CLIP_PLANE0,dbuf);

		GL11.glEnable(GL11.GL_CLIP_PLANE1);
		dbuf.put(new double[]{ -1,0,0,50 });
		dbuf.rewind();
		GL11.glClipPlane(GL11.GL_CLIP_PLANE1,dbuf);

		GL11.glEnable(GL11.GL_CLIP_PLANE2);
		dbuf.put(new double[]{ 0,0,1,0 });
		dbuf.rewind();
		GL11.glClipPlane(GL11.GL_CLIP_PLANE2,dbuf);

		GL11.glEnable(GL11.GL_CLIP_PLANE3);
		dbuf.put(new double[]{ 0,0,-1,25 });
		dbuf.rewind();
		GL11.glClipPlane(GL11.GL_CLIP_PLANE3,dbuf);
	}
	public FiaUIRect control;
	public FiaUIRect tank;
	public FiaUIRect pageLeft;
	public FiaUIRect pageRight;
	public FiaUIRect empty;
	public FiaUIRect hull;
	public FiaUIRect graph;
	public FiaUIRect damage;
	@Override
	public void initGui() {
		super.initGui();
		control = new FiaUIRect(this,138,59,37,19);
		tank = new FiaUIRect(this,153,96,52,16);
		pageLeft = new FiaUIRect(this,65,95,18,18);
		pageRight = new FiaUIRect(this,83,95,18,18);
		empty = new FiaUIRect(this,199,124,8,8);
		hull = new FiaUIRect(this,77,25,4,52);
		graph = new FiaUIRect(this,9,84,50,25);
		damage = new FiaUIRect(this,77,80,126,2);
	}
	public boolean draggingControl = false;
	@Override
	protected void mouseClicked(int mouseX,int mouseY,int mouseButton) throws IOException {
		super.mouseClicked(mouseX,mouseY,mouseButton);
		if (mouseButton == 0) {
			if (control.isMouseIn(mouseX,mouseY)) {
				draggingControl = true;
				playClick(1);
			}
			// intentionally using getPageSafely() here so in case someone hacks the page number to
			// ridiculous value it just gets reset to normal ranges
			if (pageLeft.isMouseIn(mouseX,mouseY)) {
				if (te.page > 0) {
					LeafiaPacket._start(te).__write(APRCoreTE.idPage,getPageSafely(-1)).__sendToServer();
					playClick(1);
				} else
					playDenied();
			}
			if (pageRight.isMouseIn(mouseX,mouseY)) {
				if (te.page < te.chambers.size()-1) {
					LeafiaPacket._start(te).__write(APRCoreTE.idPage,getPageSafely(1)).__sendToServer();
					playClick(1);
				} else
					playDenied();
			}
			if (empty.isMouseIn(mouseX,mouseY)) {
				boolean tooHot = false;
				for (APRChamber data1 : te.chamberDatas) {
					if (data1.blanketTemp > 100) {
						tooHot = true;
						break;
					}
				}
				if (tooHot || !te.inventory.getStackInSlot(0).isEmpty())
					playDenied();
				else
					playClick(1);
				LeafiaPacket._start(te).__write(APRCoreTE.idVoid,true).__sendToServer();
			}
		}
	}
	@Override
	protected void mouseReleased(int mouseX,int mouseY,int state) {
		super.mouseReleased(mouseX,mouseY,state);
		if (state == 0)
			draggingControl = false;
	}
	@Override
	public void drawScreen(int mouseX,int mouseY,float partialTicks) {
		super.drawScreen(mouseX,mouseY,partialTicks);
		super.renderHoveredToolTip(mouseX,mouseY);
		if (control.isMouseIn(mouseX,mouseY))
			drawHoveringText(I18nUtil.resolveKey("tile.apr_core.gui.control_value",(int)Math.ceil(te.targetControl)),mouseX,mouseY);
		if (pageLeft.isMouseIn(mouseX,mouseY) || pageRight.isMouseIn(mouseX,mouseY))
			drawHoveringText(I18nUtil.resolveKey("tile.apr_core.gui.page",(te.page+1)+"/"+te.chambers.size()),mouseX,mouseY);
		if (tank.isMouseIn(mouseX,mouseY)) {
			GUIElements.drawHoveringText(
					Arrays.asList(
							I18nUtil.resolveKey("tile.apr_core.gui.ap"),
							(int)Math.ceil(te.particles)+"/"+te.particleCap+"mB"
					),
					mouseX, mouseY,
					fontRenderer,itemRender,
					width,height,
					6,GUIElements.STANDARD_LINE_DIST,
					GUIElements.STANDARD_COLOR_BACKGROUND,
					GUIElements.STANDARD_COLOR_BACKGROUND,
					0xffee35ff,0xff59f7ff
			);
		}
		LeafiaClientUtil.renderTankInfo(te.oxygen,this,mouseX,mouseY,guiLeft+102,guiTop+25,16,52);
		LeafiaClientUtil.renderTankInfo(te.lox,this,mouseX,mouseY,guiLeft+120,guiTop+25,16,52);
		if (te.chamberDatas.isEmpty()) return;
		APRChamber data = te.chamberDatas.get(getPageSafely());
		if (data != null) {
			LeafiaClientUtil.renderTankInfo(data.input,this,mouseX,mouseY,guiLeft+26,guiTop+25,16,52);
			LeafiaClientUtil.renderTankInfo(data.output,this,mouseX,mouseY,guiLeft+44,guiTop+25,16,52);
		}
		drawCustomInfoStat(mouseX,mouseY,guiLeft-16,guiTop+21,16,16,guiLeft,guiTop+21,I18nUtil.resolveKeyArray("tile.apr_core.gui.tips.hullTemp"));
		drawCustomInfoStat(mouseX,mouseY,guiLeft-16,guiTop+21+16,16,16,guiLeft,guiTop+21+16,I18nUtil.resolveKeyArray("tile.apr_core.gui.tips.blanketTemp"));
		drawCustomInfoStat(mouseX,mouseY,guiLeft-16,guiTop+21+32,16,16,guiLeft,guiTop+21+32,I18nUtil.resolveKeyArray("tile.apr_core.gui.tips.rps"));
		if (empty.isMouseIn(mouseX,mouseY)) {
			boolean tooHot = false;
			for (APRChamber data1 : te.chamberDatas) {
				if (data1.blanketTemp > 100) {
					tooHot = true;
					break;
				}
			}
			if (tooHot)
				drawHoveringText(Arrays.asList(I18nUtil.resolveKey("tile.apr_core.gui.void"),TextFormatting.RED+I18nUtil.resolveKey("tile.apr_core.gui.void.hot")),mouseX,mouseY);
			else if (!te.inventory.getStackInSlot(0).isEmpty())
				drawHoveringText(Arrays.asList(I18nUtil.resolveKey("tile.apr_core.gui.void"),TextFormatting.RED+I18nUtil.resolveKey("tile.apr_core.gui.void.slot")),mouseX,mouseY);
			else
				drawHoveringText(Arrays.asList(I18nUtil.resolveKey("tile.apr_core.gui.void"),TextFormatting.GOLD+I18nUtil.resolveKey("tile.apr_core.gui.void.capsule",(int)(te.particles/1000))),mouseX,mouseY);
		}
		if (graph.isMouseIn(mouseX,mouseY) && data != null) {
			int hottestChamber = 0;
			double hottestTemp = 0;
			int i = 0;
			for (APRChamber data1 : te.chamberDatas) {
				if (data1.blanketTemp > hottestTemp) {
					hottestTemp = data1.blanketTemp;
					hottestChamber = i;
				}
				i++;
			}
			drawHoveringText(Arrays.asList(I18nUtil.resolveKey("tile.apr_core.gui.heat.blanket",String.format("%1.1f°C",data.graph[APRCoreTE.graphXSegments-1])),TextFormatting.GOLD+I18nUtil.resolveKey("tile.apr_core.gui.heat.blanket.hottest","#"+(hottestChamber+1))),mouseX,mouseY);
		}
		if (hull.isMouseIn(mouseX,mouseY))
			drawHoveringText(I18nUtil.resolveKey("tile.apr_core.gui.heat.hull",String.format("%1.1f°C",te.hullTemp)),mouseX,mouseY);
		if (damage.isMouseIn(mouseX,mouseY))
			drawHoveringText(I18nUtil.resolveKey("tile.apr_core.gui.damage",(int)Math.ceil(te.damage*100d/APRCoreTE.maxDamage)+"%"),mouseX,mouseY);
	}
	public int getLeverOffset(double control) {
		return (int)((control-10)/90*(37-4));
	}
	public int getPageSafely(int offset) {
		return Math.max(Math.min(te.page+offset,te.chamberDatas.size()-1),0);
	}
	public int getPageSafely() {
		return getPageSafely(0);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks,int mouseX,int mouseY) {
		drawDefaultBackground();
		LeafiaGls.color(1,1,1);
		bindTexture(tex);
		drawTexturedModalRect(guiLeft,guiTop,0,0,xSize,ySize);
		if (te.particleCap > 0)
			drawTexturedModalRect(guiLeft+153,guiTop+96,0,231,(int)(52*te.particles/te.particleCap),16);
		drawGraph();
		if (draggingControl) {
			double control = (mouseX-guiLeft-138+2)/(37d-4);
			control = control*90+10;
			LeafiaPacket._start(te).__write(APRCoreTE.idFunnelDesired,Math.max(10,Math.min(control,100))).__sendToServer();
		}
		LeafiaGls.color(1,1,1,1);
		drawTexturedModalRect(guiLeft+138+getLeverOffset(te.targetControl),guiTop+59,211,0,4,19);
		LeafiaGls.color(1,1,1,0.4f);
		drawTexturedModalRect(guiLeft+138+getLeverOffset(te.control),guiTop+59,211,0,4,19);
		LeafiaGls.color(1,1,1,1);
		if (pageLeft.isMouseIn(mouseX,mouseY))
			drawTexturedModalByFiaRect(pageLeft,219,0);
		if (pageRight.isMouseIn(mouseX,mouseY))
			drawTexturedModalByFiaRect(pageRight,237,0);
		LeafiaGls.color(0.75f,0.75f,0.75f);
		if (te.page <= 0)
			drawTexturedModalByFiaRect(pageLeft,65,95);
		if (te.page >= te.chambers.size()-1)
			drawTexturedModalByFiaRect(pageRight,83,95);
		LeafiaGls.color(1,1,1);
		{
			int barLength = (int)Math.ceil((te.hullTemp-20)*hull.h/(1538-20));
			drawTexturedModalRect(hull.absX(),hull.absY()+hull.h-barLength,215,52-barLength,hull.w,barLength);
		}
		{
			int barLength = te.damage*damage.w/APRCoreTE.maxDamage;
			drawTexturedModalRect(damage.absX(),damage.absY(),0,247,barLength,damage.h);
		}
		{
			int rps = (int)Math.round(te.rps);
			int x = guiLeft+194;
			boolean firstDigit = true;
			while (firstDigit || rps > 0) {
				drawTexturedModalRect(x,guiTop+63,6*(rps%10),220,6,11);
				x -= 7;
				firstDigit = false;
				rps = rps/10;
			}
		}
		te.oxygen.renderTank(guiLeft+102,guiTop+25+52,zLevel,16,52);
		te.lox.renderTank(guiLeft+120,guiTop+25+52,zLevel,16,52);
		if (te.chamberDatas.isEmpty()) return;
		APRChamber data = te.chamberDatas.get(getPageSafely());
		if (data != null) {
			data.input.renderTank(guiLeft+26,guiTop+25+52,zLevel,16,52);
			data.output.renderTank(guiLeft+44,guiTop+25+52,zLevel,16,52);
		}
		drawInfoPanel(guiLeft-16,guiTop+21,16,16,2);
		drawInfoPanel(guiLeft-16,guiTop+21+16,16,16,2);
		drawInfoPanel(guiLeft-16,guiTop+21+32,16,16,3);
	}
}
