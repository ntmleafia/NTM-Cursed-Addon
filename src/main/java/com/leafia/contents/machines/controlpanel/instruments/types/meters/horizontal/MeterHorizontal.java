package com.leafia.contents.machines.controlpanel.instruments.types.meters.horizontal;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.controls.ControlType;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.render.loader.IModelCustom;
import com.leafia.AddonBase;
import com.leafia.dev.LeafiaBrush;
import com.leafia.dev.LeafiaBrush.BrushMode;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MeterHorizontal extends Control {
	int length = 5;
	int r = 255;
	int g = 153;
	int b = 0;
	float min = 0;
	float max = 5;
	int divisions = 6;
	int subdivisions = 5;
	int decimals = 0;
	static final ResourceLocation nullRl = new ResourceLocation("null");
	public MeterHorizontal(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
		vars.put("value",new DataValueFloat(0));
		configMap.put("length",new DataValueFloat(length));
		configMap.put("r",new DataValueFloat(r));
		configMap.put("g",new DataValueFloat(g));
		configMap.put("b",new DataValueFloat(b));
		configMap.put("minValue",new DataValueFloat(min));
		configMap.put("maxValue",new DataValueFloat(max));
		configMap.put("divisions",new DataValueFloat(divisions));
		configMap.put("subdivisions",new DataValueFloat(subdivisions));
		configMap.put("decimals",new DataValueFloat(decimals));
	}
	@Override
	public SubElementBaseConfig getConfigSubElement(GuiControlEdit gui,Map<String,DataValue> configs) {
		return new MeterHorizontalCSE(gui,configs);
	}
	@Override
	protected void onConfigMapChanged() {
		for (Entry<String,DataValue> entry : configMap.entrySet()) {
			switch(entry.getKey()) {
				case "length" -> length = (int)entry.getValue().getNumber();
				case "r" -> r = (int)entry.getValue().getNumber();
				case "g" -> g = (int)entry.getValue().getNumber();
				case "b" -> b = (int)entry.getValue().getNumber();
				case "minValue" -> min = entry.getValue().getNumber();
				case "maxValue" -> max = entry.getValue().getNumber();
				case "decimals" -> decimals = (int)entry.getValue().getNumber();
				case "divisions" -> divisions = (int)entry.getValue().getNumber();
				case "subdivisions" -> subdivisions = (int)entry.getValue().getNumber();
			}
		}
	}
	@Override
	public ControlType getControlType() {
		return ControlType.METER;
	}
	@Override
	public float[] getSize() {
		return new float[]{ 5,1,0.01f };
	}
	@Override
	public AxisAlignedBB getBoundingBox() {
		return null;
	}
	@Override
	public void fillBox(float[] box) {
		float height = getSize()[1];
		box[0] = posX;
		box[1] = posY;
		box[2] = posX + length;
		box[3] = posY + height;
	}
	public void draw(float greenTint,float value) {
		TextureManager texmg = Minecraft.getMinecraft().getTextureManager();
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		texmg.bindTexture(AddonBase.solid);
		boolean uni = font.getUnicodeFlag();
		font.setUnicodeFlag(false);
		LeafiaBrush brush = LeafiaBrush.instance;
		LeafiaGls.translate(0,0,-0.002);
		{
			LeafiaGls.color(0.1f,0.1f*greenTint,0.1f);
			brush.startDrawingQuads();
			brush.addVertexWithUV(0,1,0,0,1);
			brush.addVertexWithUV(length,1,0,1,1);
			brush.addVertexWithUV(length,0,0,1,0);
			brush.addVertexWithUV(0,0,0,1,0);
			brush.draw();
		}
		LeafiaGls.translate(0,0,-0.002);
		float length1 = length-0.2f;
		{
			LeafiaGls.pushMatrix();
			LeafiaGls.translate(0.1,0.1,0);
			LeafiaGls.scale(1,0.8,1);
			LeafiaGls.color(1,greenTint,1);
			brush.startDrawingQuads();
			brush.addVertexWithUV(0,1,0,0,1);
			brush.addVertexWithUV(length1,1,0,1,1);
			brush.addVertexWithUV(length1,0,0,1,0);
			brush.addVertexWithUV(0,0,0,1,0);
			brush.draw();
			LeafiaGls.popMatrix();
		}
		LeafiaGls.translate(0,0,-0.002);
		LeafiaGls.color(0,0,0);
		float padding = 0.4f;
		float endpoint = length-padding;
		LeafiaGls.translate(0,0.1,0);
		int startY = 22;
		for (int i0 = 0; i0 < divisions; i0++) {
			float i = i0/(float)(divisions-1);
			LeafiaGls.pushMatrix();
			if (subdivisions > 0 && i0 < divisions-1) {
				boolean isOdd = subdivisions%2 == 1;
				for (int j = 1; j <= subdivisions; j++) {
					float increment = 1f/(divisions-1)/(subdivisions+1);
					LeafiaGls.pushMatrix();
					LeafiaGls.translate(lerp(padding,endpoint,i+increment*j),0,0);
					LeafiaGls.scale(0.025);
					LeafiaGls.translate(-0.5,0,0);
					int height = 3;
					if (isOdd) {
						if (j-1 == subdivisions/2)
							height = 5;
					}
					brush.startDrawingQuads();
					brush.addVertexWithUV(0,startY+3,0,0,1);
					brush.addVertexWithUV(1,startY+3,0,1,1);
					brush.addVertexWithUV(1,startY-height,0,1,0);
					brush.addVertexWithUV(0,startY-height,0,0,0);
					brush.draw();
					LeafiaGls.popMatrix();
				}
			}
			LeafiaGls.translate(lerp(padding,endpoint,i),0,0);
			LeafiaGls.scale(0.025);
			LeafiaGls.translate(-0.5,0,0);
			brush.startDrawingQuads();
			brush.addVertexWithUV(0,startY+3,0,0,1);
			brush.addVertexWithUV(1,startY+3,0,1,1);
			brush.addVertexWithUV(1,startY-7,0,1,0);
			brush.addVertexWithUV(0,startY-7,0,0,0);
			brush.draw();
			String s = String.format("%01."+decimals+"f",lerp(min,max,i0/(float)(divisions-1)));
			//LeafiaGls.translate(-font.getStringWidth(s)/2f,0,0);
			font.drawString(s,-(font.getStringWidth(s)-1)/2,startY-8-7,0);
			texmg.bindTexture(AddonBase.solid);
			LeafiaGls.popMatrix();
		}
		font.setUnicodeFlag(uni);
		float needleWidth = 0.05f;
		LeafiaGls.translate(0,0,-0.002);
		float needleHeight = 0.7f;
		brush.startDrawingQuads();
		brush.addVertexWithUV(padding-needleWidth,needleHeight,0,0,1);
		brush.addVertexWithUV(endpoint+needleWidth,needleHeight,0,1,1);
		brush.addVertexWithUV(endpoint+needleWidth,needleHeight-needleWidth*2,0,1,0);
		brush.addVertexWithUV(padding-needleWidth,needleHeight-needleWidth*2,0,0,0);
		brush.draw();

		LeafiaGls.translate(0,0,-0.002);
		float needleOutline = needleWidth/3;
		brush.startDrawing(BrushMode.TRIANGLE_FAN,DefaultVertexFormats.POSITION_TEX);
		brush.addVertexWithUV(lerp(padding,endpoint,value)-needleWidth-needleOutline,needleHeight-needleWidth*2-needleOutline,0,0,0);
		brush.addVertexWithUV(lerp(padding,endpoint,value)-needleWidth-needleOutline,needleHeight+needleOutline,0,0,1);
		brush.addVertexWithUV(lerp(padding,endpoint,value)+needleWidth+needleOutline,needleHeight+needleOutline,0,1,1);
		brush.addVertexWithUV(lerp(padding,endpoint,value)+needleWidth+needleOutline,needleHeight-needleWidth*2-needleOutline,0,1,0);
		brush.addVertexWithUV(lerp(padding,endpoint,value),needleHeight-needleWidth*3-needleOutline,0,0,0);
		brush.draw();
		LeafiaGls.translate(0,0,-0.002);
		float lX = OpenGlHelper.lastBrightnessX;
		float lY = OpenGlHelper.lastBrightnessY;
		texmg.bindTexture(AddonBase.solid_e);
		LeafiaGls.color(r/255f,g/255f*greenTint,b/255f);
		LeafiaGls.disableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240,240);
		brush.startDrawing(BrushMode.TRIANGLE_FAN,DefaultVertexFormats.POSITION_TEX);
		brush.addVertexWithUV(lerp(padding,endpoint,value)-needleWidth,needleHeight-needleWidth*2,0,0,0);
		brush.addVertexWithUV(lerp(padding,endpoint,value)-needleWidth,needleHeight,0,0,1);
		brush.addVertexWithUV(lerp(padding,endpoint,value)+needleWidth,needleHeight,0,1,1);
		brush.addVertexWithUV(lerp(padding,endpoint,value)+needleWidth,needleHeight-needleWidth*2,0,1,0);
		brush.addVertexWithUV(lerp(padding,endpoint,value),needleHeight-needleWidth*3,0,0,0);
		brush.draw();
		LeafiaGls.enableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,lX,lY);

		LeafiaGls.color(1,1,1);
	}
	@Override
	public void render() {
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		LeafiaGls.color(1,1,1);
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(posX-5/2d,0,posY-0.5);
		LeafiaGls.rotate(90,1,0,0);
		draw(1,MathHelper.clamp(ratio(min,max,vars.get("value").getNumber()),0,1));
		LeafiaGls.popMatrix();
		LeafiaGls.shadeModel(GL11.GL_FLAT);
	}
	@Override
	public void renderControl(float[] renderBox,Control selectedControl,GuiControlEdit gui) {
		LeafiaGls.pushMatrix();
		LeafiaGls.disableDepth();
		LeafiaGls.translate(renderBox[0],renderBox[1],0);
		LeafiaGls.scale((renderBox[2]-renderBox[0])/length,renderBox[3]-renderBox[1],1);
		draw(selectedControl == this ? 0.8F : 1F,0);
		LeafiaGls.enableDepth();
		LeafiaGls.popMatrix();
	}
	static float lerp(float a,float b,float t) {
		return a+(b-a)*t;
	}
	static float ratio(float a,float b,float t) {
		float delta = b-a;
		if (delta == 0)
			return a;
		return (t-a)/delta;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IModelCustom getModel() {
		return null;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture() {
		return nullRl;
	}
	@Override
	public Control newControl(ControlPanel controlPanel) {
		return new MeterHorizontal(name,registryName,controlPanel);
	}
	@Override
	public void populateDefaultNodes(List<ControlEvent> list) { }
}
