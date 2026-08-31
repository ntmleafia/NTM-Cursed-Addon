package com.leafia.contents.machines.controlpanel.instruments.types.graph;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.controls.ControlType;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.render.loader.IModelCustom;
import com.leafia.AddonBase;
import com.leafia.contents.machines.controlpanel.CCPSyncPacketBase;
import com.leafia.contents.machines.controlpanel.instruments.AddonControlType;
import com.leafia.dev.render.LeafiaBrush;
import com.leafia.dev.render.LeafiaBrush.BrushMode;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.transformer.LeafiaGls;
import com.llib.math.LeafiaColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Graph extends Control {
	float width = 6;
	float height = 4;
	int verticalLineSpan = 1;
	int horizontalLineDivision = 10;
	int segments = 40;
	int decimalLength = 2;
	float textOffset = 0.5f;
	float red = 0;
	float green = 255;
	float blue = 0;
	GraphSkin skin = GraphSkin.CONSOLE;
	static final float borderThickness = 0.1f;
	public Graph(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
		vars.put("minValue",new DataValueFloat(0));
		vars.put("maxValue",new DataValueFloat(1));
		configMap.put("red",new DataValueFloat(red));
		configMap.put("green",new DataValueFloat(green));
		configMap.put("blue",new DataValueFloat(blue));
		configMap.put("skin",new DataValueString("Console"));
		configMap.put("segments",new DataValueFloat(40));
		configMap.put("verticalLineSpan",new DataValueFloat(1));
		configMap.put("horizontalLineDivision",new DataValueFloat(10));
		configMap.put("decimalLength",new DataValueFloat(2));
		configMap.put("textOffset",new DataValueFloat(0.5f));
		configMap.put("width",new DataValueFloat(6));
		configMap.put("height",new DataValueFloat(4));
	}
	@Override
	public SubElementBaseConfig getConfigSubElement(GuiControlEdit gui,Map<String,DataValue> configs) {
		return new GraphCSE(gui,configs);
	}
	@SideOnly(Side.CLIENT)
	void drawPreview(float greenTint) {
		LeafiaBrush brush = LeafiaBrush.instance;
		TextureManager texmg = Minecraft.getMinecraft().getTextureManager();
		texmg.bindTexture(AddonBase.solid);
		LeafiaGls.color(0.25f,0.25f*greenTint,0.25f);
		brush.startDrawingQuads();
		brush.addVertexWithUV(0,1,0,0,1);
		brush.addVertexWithUV(1,1,0,1,1);
		brush.addVertexWithUV(1,0,0,1,0);
		brush.addVertexWithUV(0,0,0,0,0);
		brush.draw();
		LeafiaGls.translate(0.5,0.5,0);
		{
			double totalWidth = width+borderThickness*2;
			double totalHeight = height+borderThickness*2;
			LeafiaGls.scale(width/totalWidth,height/totalHeight);
			LeafiaGls.translate(-0.5,-0.5,0);
		}
		{
			{
				// BG
				LeafiaColor bgColor = new LeafiaColor(skin.bgColor);
				LeafiaGls.color(bgColor.getRed(),bgColor.getGreen()*greenTint,bgColor.getBlue());
				brush.startDrawingQuads();
				brush.addVertexWithUV(0,1,0,0,1);
				brush.addVertexWithUV(1,1,0,1,1);
				brush.addVertexWithUV(1,0,0,1,0);
				brush.addVertexWithUV(0,0,0,0,0);
				brush.draw();
			}
			LeafiaGls.pushMatrix();
			double areaWidth = width-textOffset;
			LeafiaGls.scale(areaWidth/width,1,1);
			if (skin.hasGrid) {
				// GRID
				LeafiaColor gridColor = new LeafiaColor(skin.gridColor);
				LeafiaGls.color(gridColor.getRed(),gridColor.getGreen()*greenTint,gridColor.getBlue());
				brush.startDrawing(BrushMode.LINES,DefaultVertexFormats.POSITION);
				for (int i = 0; i < segments; i+=verticalLineSpan) {
					double x = i/(segments-1d);
					brush.addVertex(x,0,0);
					brush.addVertex(x,1,0);
				}
				if (horizontalLineDivision > 0) {
					for (int i = 0; i <= horizontalLineDivision; i++) {
						double y = i/(double)horizontalLineDivision;
						brush.addVertex(0,y,0);
						brush.addVertex(1,y,0);
					}
				}
				brush.draw();
			}
			{
				// GRAPH
				LeafiaGls.color(red/255,green/255*greenTint,blue/255);
				brush.startDrawing(BrushMode.LINES,DefaultVertexFormats.POSITION);
				brush.addVertex(0,0.5,0);
				brush.addVertex(1/5d,0.5,0);

				brush.addVertex(1/5d,0.5,0);
				brush.addVertex(2/5d,1,0);

				brush.addVertex(2/5d,1,0);
				brush.addVertex(3/5d,0,0);

				brush.addVertex(3/5d,0,0);
				brush.addVertex(4/5d,0.5,0);

				brush.addVertex(4/5d,0.5,0);
				brush.addVertex(5/5d,0.5,0);
				brush.draw();
			}
			LeafiaGls.popMatrix();
		}
		LeafiaGls.color(1,1,1);
	}
	@SideOnly(Side.CLIENT)
	@Override
	public void renderControl(float[] renderBox,Control selectedControl,GuiControlEdit gui) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(renderBox[0],renderBox[1],0);
		LeafiaGls.scale(renderBox[2]-renderBox[0],renderBox[3]-renderBox[1],1);
		drawPreview(selectedControl == this ? 0.8F : 1F);
		LeafiaGls.popMatrix();
	}
	@Override
	protected void onConfigMapChanged() {
		for (Entry<String,DataValue> entry : configMap.entrySet()) {
			switch(entry.getKey()) {
				case "red" -> red = MathHelper.clamp(entry.getValue().getNumber(),0,255);
				case "green" -> green = MathHelper.clamp(entry.getValue().getNumber(),0,255);
				case "blue" -> blue = MathHelper.clamp(entry.getValue().getNumber(),0,255);
				case "skin" -> {
					for (GraphSkin s : GraphSkin.skins) {
						if (s.name.equals(entry.getValue().toString())) {
							skin = s;
							break;
						}
					}
				}
				case "segments" -> {
					segments = (int)entry.getValue().getNumber();
					rebuildArray();
				}
				case "verticalLineSpan" -> verticalLineSpan = (int)entry.getValue().getNumber();
				case "horizontalLineDivision" -> horizontalLineDivision = (int)entry.getValue().getNumber();
				case "decimalLength" -> decimalLength = MathHelper.clamp((int)entry.getValue().getNumber(),0,5);
				case "textOffset" -> textOffset = entry.getValue().getNumber();
				case "width" -> width = entry.getValue().getNumber();
				case "height" -> height = entry.getValue().getNumber();
			}
		}
	}
	public double getMinValue() {
		return vars.get("minValue").getNumber();
	}
	public double getMaxValue() {
		return vars.get("maxValue").getNumber();
	}
	// we ain't doing enums because fuck those
	public static class GraphSkin {
		public final String name;
		public final boolean emissive;
		public final boolean hasGrid;
		public final int bgColor;
		public final int gridColor;
		public final int textColor;
		GraphSkin(String name,boolean emissive,boolean hasGrid,int bgColor,int gridColor,int textColor) {
			this.name = name;
			this.emissive = emissive;
			this.hasGrid = hasGrid;
			this.bgColor = bgColor;
			this.gridColor = gridColor;
			this.textColor = textColor;
		}
		static List<GraphSkin> skins = new ArrayList<>();
		public static GraphSkin registerOrGet(String name,boolean emissive,boolean hasGrid,int bgColor,int gridColor,int textColor) {
			for (GraphSkin skin : skins) {
				if (skin.name.equals(name))
					return skin;
			}
			GraphSkin skin = new GraphSkin(name,emissive,hasGrid,bgColor,gridColor,textColor);
			skins.add(skin);
			return skin;
		}
		public static final GraphSkin CONSOLE = registerOrGet("Console",true,false,0,0,0);
		public static final GraphSkin LIGHT = registerOrGet("Light",false,true,0xFFFFFF,0xCCCCCC,0xAAAAAA);
		public static final GraphSkin DARK = registerOrGet("Dark",true,true,0x1A1A1A,0x3A3A3A,0xCCCCCC);
	}
	@Override
	public ControlType getControlType() {
		return AddonControlType.GRAPH;
	}
	@Override
	public float[] getSize() {
		return new float[]{ width+borderThickness*2,height+borderThickness*2,0.1f };
	}
	@Override
	public AxisAlignedBB getBoundingBox() {
		return null;
	}
	@Override
	public void receiveEvent(ControlEvent evt) {
		if (evt.name.equals("tick")) {
			BlockPos pos = panel.parent.getControlPos();
			CCPGraphSyncPacket packet = new CCPGraphSyncPacket(
					pos,
					panel.controls.indexOf(this),
					this
			);
			LeafiaCustomPacket.__start(packet).__sendToAllAround(
					panel.parent.getControlWorld().provider.getDimension(),
					pos,256
			);
		}
		super.receiveEvent(evt);
	}
	public static class CCPGraphSyncPacket extends CCPSyncPacketBase {
		Graph graph;
		public CCPGraphSyncPacket() { }
		public CCPGraphSyncPacket(BlockPos pos,int idx,Graph graph) {
			super(pos,idx);
			this.graph = graph;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			super.encode(buf);
			buf.writeShort(graph.values.length);
			for (int i = 0; i < graph.values.length; i++)
				buf.writeFloat((float)graph.values[i]);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			boolean success = load(buf);
			double[] values = new double[buf.readShort()];
			for (int i = 0; i < values.length; i++)
				values[i] = buf.readFloat();
			return (ctx)->{
				if (!success) return;
				if (instrument instanceof Graph g)
					g.values = values;
			};
		}
	}
	public double[] values = new double[segments];
	public void pushValue(double value) {
		for (int i = 0; i < values.length-1; i++)
			values[i] = values[i+1];
		values[values.length-1] = value;
	}
	public double scaleValue(double value) {
		double minValue = getMinValue();
		double maxValue = getMaxValue();
		if (maxValue == minValue)
			return maxValue;
		else
			return (value-minValue)/(maxValue-minValue);
	}
	static DoubleBuffer dbuf = null;
	public void rebuildArray() {
		if (values.length != segments)
			values = new double[segments];
	}
	@Override
	public void render() {
		if (dbuf == null)
			dbuf = GLAllocation.createDirectByteBuffer(16*4).asDoubleBuffer(); // i have no idea how this even works
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		TextureManager texmg = Minecraft.getMinecraft().getTextureManager();
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(posX-width/2,0.001,posY-height/2);
		LeafiaBrush brush = LeafiaBrush.instance;
		texmg.bindTexture(AddonBase.solid);
		{
			LeafiaGls.color(0.25f,0.25f,0.25f);
			brush.startDrawingQuads();
			brush.addVertexWithUV(-borderThickness,0,height+borderThickness,0,1);
			brush.addVertexWithUV(width+borderThickness,0,height+borderThickness,1,1);
			brush.addVertexWithUV(width+borderThickness,0,-borderThickness,1,0);
			brush.addVertexWithUV(-borderThickness,0,-borderThickness,0,0);
		}
		brush.draw();
		LeafiaGls.translate(0,0.001,0);
		float lX = OpenGlHelper.lastBrightnessX;
		float lY = OpenGlHelper.lastBrightnessY;
		{ // i had to vibecode this shit
			GL11.glEnable(GL11.GL_CLIP_PLANE0);
			dbuf.put(new double[]{ 1,0,0,0 });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE0,dbuf);

			GL11.glEnable(GL11.GL_CLIP_PLANE1);
			dbuf.put(new double[]{ -1,0,0,width });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE1,dbuf);

			GL11.glEnable(GL11.GL_CLIP_PLANE2);
			dbuf.put(new double[]{ 0,0,1,0 });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE2,dbuf);

			GL11.glEnable(GL11.GL_CLIP_PLANE3);
			dbuf.put(new double[]{ 0,0,-1,height });
			dbuf.rewind();
			GL11.glClipPlane(GL11.GL_CLIP_PLANE3,dbuf);
		}
		double minValue = getMinValue();
		double maxValue = getMaxValue();
		{
			// background
			LeafiaColor bgColor = new LeafiaColor(skin.bgColor);
			LeafiaGls.color(bgColor.getRed(),bgColor.getGreen(),bgColor.getBlue());
			brush.startDrawingQuads();
			brush.addVertexWithUV(0,0,height,0,1);
			brush.addVertexWithUV(width,0,height,1,1);
			brush.addVertexWithUV(width,0,0,1,0);
			brush.addVertexWithUV(0,0,0,0,0);
			brush.draw();
		}
		{
			// grid
			LeafiaGls.translate(0,0.001,0);
			LeafiaColor gridColor = new LeafiaColor(skin.gridColor);
			LeafiaGls.color(gridColor.getRed(),gridColor.getGreen(),gridColor.getBlue());
			double thickness = 0.01;
			brush.startDrawingQuads();
			for (int i = 0; i < values.length; i+=verticalLineSpan) {
				double x = i/(values.length-1d);
				double sc = (width-textOffset)/width;
				brush.addVertexWithUV(x*width*sc-thickness,0,height,0,1);
				brush.addVertexWithUV(x*width*sc+thickness,0,height,1,1);
				brush.addVertexWithUV(x*width*sc+thickness,0,0,1,0);
				brush.addVertexWithUV(x*width*sc-thickness,0,0,0,0);
			}
			brush.draw();
			if (horizontalLineDivision > 0) {
				for (int i = 0; i <= horizontalLineDivision; i++) {
					brush.startDrawingQuads();
					double y = i/(double)horizontalLineDivision;
					brush.addVertexWithUV(0,0,y*height+thickness,0,1);
					brush.addVertexWithUV(width-textOffset,0,y*height+thickness,1,1);
					brush.addVertexWithUV(width-textOffset,0,y*height-thickness,1,0);
					brush.addVertexWithUV(0,0,y*height-thickness,0,0);
					brush.draw();
					double value = maxValue+(minValue-maxValue)/horizontalLineDivision*i;
					LeafiaGls.pushMatrix();
					LeafiaGls.translate(width-textOffset,0,y*height);
					LeafiaGls.scale(0.02);
					LeafiaGls.rotate(90,1,0,0);
					int ypos = -7/2;
					if (i == 0) ypos = 0;
					if (i == horizontalLineDivision) ypos = -7;
					font.drawString(String.format("%01."+decimalLength+"f",value),1,ypos,skin.textColor);
					LeafiaGls.color(gridColor.getRed(),gridColor.getGreen(),gridColor.getBlue());
					LeafiaGls.popMatrix();
					texmg.bindTexture(AddonBase.solid);
				}
			}
		}
		{
			// graph
			LeafiaGls.translate(0,0.001,0);
			LeafiaGls.color(red/255,green/255,blue/255);
			if (skin.emissive) {
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240,240);
				LeafiaGls.disableLighting();
			}
			double thickness = 0.05;
			brush.startDrawingQuads();
			double sc = (width-textOffset)/width;
			double x0 = -1;
			double y0 = -1;
			double delta0 = 0;
			double value0 = 0;
			double spacing = 1/(values.length-1d);
			double upperPointX = 0;
			double upperPointY = 0;
			double lowerPointX = 0;
			double lowerPointY = 0;
			for (int i = 0; i < values.length; i++) {
				double x = spacing*i;
				double y = scaleValue(values[i]);
				double x1 = x*width;
				double y1 = height*(1-y);
				double delta1 = scaleValue(values[i])-value0;
				if (i == 0)
					delta1 = 0;
				if (x0 >= 0) { // second point or later
					double ang1 = Math.atan2(delta1,spacing);
					double up = ang1+Math.PI/2;
					double upX = Math.cos(up)*thickness;
					double upY = -Math.sin(up)*thickness; // i forgot vertical axis is inverted in this context
					{
						// draw edge
						if (delta1 > delta0) {
							brush.addVertexWithUV(x0*sc,0,y0,0,1);
							brush.addVertexWithUV(lowerPointX,0,lowerPointY,1,1);
							brush.addVertexWithUV(x0*sc-upX,0,y0-upY,1,0);
							brush.addVertexWithUV(x0*sc,0,y0,0,0);
						} else if (delta1 < delta0) {
							brush.addVertexWithUV(x0*sc,0,y0,0,1);
							brush.addVertexWithUV(x0*sc+upX,0,y0+upY,1,1);
							brush.addVertexWithUV(upperPointX,0,upperPointY,1,0);
							brush.addVertexWithUV(x0*sc,0,y0,0,0);
						}
					}
					{ // actual fucking line
						brush.addVertexWithUV(x0*sc-upX,0,y0-upY,0,1);

						brush.addVertexWithUV(x1*sc-upX,0,y1-upY,1,1);
						brush.addVertexWithUV(x1*sc+upX,0,y1+upY,1,0);
						lowerPointX = x1*sc-upX; lowerPointY = y1-upY;
						upperPointX = x1*sc+upX; upperPointY = y1+upY;

						brush.addVertexWithUV(x0*sc+upX,0,y0+upY,0,0);
					}
				}
				delta0 = delta1;
				value0 = scaleValue(values[i]);
				x0 = x1;
				y0 = y1;
			}
			brush.draw();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,lX,lY);
			LeafiaGls.enableLighting();
		}
		GL11.glDisable(GL11.GL_CLIP_PLANE0);
		GL11.glDisable(GL11.GL_CLIP_PLANE1);
		GL11.glDisable(GL11.GL_CLIP_PLANE2);
		GL11.glDisable(GL11.GL_CLIP_PLANE3);
		LeafiaGls.color(1,1,1);
		LeafiaGls.popMatrix();
	}
	ResourceLocation dummy = new ResourceLocation("nope");
	@SideOnly(Side.CLIENT)
	@Override
	public IModelCustom getModel() {
		return null;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getGuiTexture() {
		return dummy;
	}
	@Override
	public Control newControl(ControlPanel controlPanel) {
		return new Graph(name,registryName,controlPanel);
	}
	@Override
	public void populateDefaultNodes(List<ControlEvent> list) { }
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("graphData")) {
			NBTTagList list = tag.getTagList("graphData",5);
			if (list.tagCount() != segments) return;
			segments = list.tagCount();
			rebuildArray();
			for (int i = 0; i < list.tagCount(); i++)
				values[i] = list.getFloatAt(i);
		}
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		for (double value : values)
			list.appendTag(new NBTTagFloat((float)value));
		tag.setTag("graphData",list);
		return super.writeToNBT(tag);
	}
}
