package com.leafia.contents.machines.controlpanel.instruments.types.drawing;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.controls.ControlType;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.render.loader.IModelCustom;
import com.leafia.AddonBase;
import com.leafia.dev.render.LeafiaBrush;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Drawing extends Control {
	int r = 100;
	int g = 100;
	int b = 100;
	float width;
	float height;

	int scale = 25;
	String text = "";
	boolean white = false;
	public Drawing(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
		configMap.put("red",new DataValueFloat(r));
		configMap.put("green",new DataValueFloat(g));
		configMap.put("blue",new DataValueFloat(b));
		configMap.put("width",new DataValueFloat(0.2f));
		configMap.put("height",new DataValueFloat(0.2f));
		configMap.put("scale",new DataValueFloat(scale));
		configMap.put("text",new DataValueString(text));
		configMap.put("white",new DataValueFloat(0));
	}
	@Override
	public SubElementBaseConfig getConfigSubElement(GuiControlEdit gui,Map<String,DataValue> configs) {
		return new DrawingCSE(gui,configs);
	}
	@Override
	protected void onConfigMapChanged() {
		for (Entry<String,DataValue> entry : configMap.entrySet()) {
			switch(entry.getKey()) {
				case "red" -> r = (int)MathHelper.clamp(entry.getValue().getNumber(),0,100);
				case "green" -> g = (int)MathHelper.clamp(entry.getValue().getNumber(),0,100);
				case "blue" -> b = (int)MathHelper.clamp(entry.getValue().getNumber(),0,100);
				case "width" -> width = entry.getValue().getNumber();
				case "height" -> height = entry.getValue().getNumber();
				case "scale" -> scale = (int)entry.getValue().getNumber();
				case "text" -> text = entry.getValue().toString();
				case "white" -> white = entry.getValue().getNumber() >= 1;
			}
		}
	}
	@Override
	public ControlType getControlType() {
		return ControlType.LABEL;
	}
	@Override
	public float[] getSize() {
		return new float[]{1,1,0.01f};
	}
	@Override
	public void fillBox(float[] box) {
		box[0] = posX;
		box[1] = posY;
		box[2] = posX + width;
		box[3] = posY + height;
	}
	void draw(float greenTint) {
		TextureManager texmg = Minecraft.getMinecraft().getTextureManager();
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		texmg.bindTexture(AddonBase.solid);
		LeafiaBrush brush = LeafiaBrush.instance;
		LeafiaGls.translate(0,0,-0.002);
		LeafiaGls.color(r/100f,g/100f*greenTint,b/100f);
		brush.startDrawingQuads();
		brush.addVertexWithUV(0,height,0,0,1);
		brush.addVertexWithUV(width,height,0,1,1);
		brush.addVertexWithUV(width,0,0,1,0);
		brush.addVertexWithUV(0,0,0,0,0);
		brush.draw();
		if (!text.isBlank()) {
			float s = scale/500F;
			LeafiaGls.translate(width/2,height/2,-0.002);
			LeafiaGls.scale(s);
			LeafiaGls.translate(-font.getStringWidth(text)/2d,0,0);
			font.drawString(text,0,-7/2,white ? 0xFFFFFF : 0);
		}
	}
	@Override
	public AxisAlignedBB getBoundingBox() {
		return null;
	}
	@Override
	public void render() {
		LeafiaGls.color(1,1,1);
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(posX-1/2d,0,posY-1/2d);
		LeafiaGls.rotate(90,1,0,0);
		draw(1);
		LeafiaGls.popMatrix();
	}
	@Override
	public void renderControl(float[] renderBox,Control selectedControl,GuiControlEdit gui) {
		LeafiaGls.pushMatrix();
		LeafiaGls.disableDepth();
		LeafiaGls.translate(renderBox[0],renderBox[1],0);
		LeafiaGls.scale((renderBox[2]-renderBox[0])/width,(renderBox[3]-renderBox[1])/height,1);
		draw(selectedControl == this ? 0.8F : 1F);
		LeafiaGls.enableDepth();
		LeafiaGls.popMatrix();
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IModelCustom getModel() {
		return null;
	}
	static final ResourceLocation nullRl = new ResourceLocation("null");
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture() {
		return nullRl;
	}
	@Override
	public Control newControl(ControlPanel controlPanel) {
		return new Drawing(name,registryName,controlPanel);
	}
	@Override
	public void populateDefaultNodes(List<ControlEvent> list) { }
}
