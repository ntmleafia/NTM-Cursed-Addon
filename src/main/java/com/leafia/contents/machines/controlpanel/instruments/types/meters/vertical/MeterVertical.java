package com.leafia.contents.machines.controlpanel.instruments.types.meters.vertical;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.controls.ControlType;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.render.loader.IModelCustom;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.contents.machines.controlpanel.CCPSyncPacketBase;
import com.leafia.contents.machines.controlpanel.instruments.types.AddonInstrumentModels;
import com.leafia.dev.render.LeafiaBrush;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.nio.DoubleBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import static com.leafia.AddonBase.getIntegrated;

public class MeterVertical extends Control {
	int needleOffset = 0;
	float max = 1;
	float min = -1;
	int divisions = 20;
	int subdivisions = 3;
	int decimals = 1;
	boolean clamp = false;
	public MeterVertical(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
		vars.put("value",new DataValueFloat(0));
		configMap.put("needleOffset",new DataValueFloat(needleOffset));
		configMap.put("minValue",new DataValueFloat(min));
		configMap.put("maxValue",new DataValueFloat(max));
		configMap.put("decimals",new DataValueFloat(decimals));
		configMap.put("divisions",new DataValueFloat(divisions));
		configMap.put("subdivisions",new DataValueFloat(subdivisions));
		configMap.put("clamp",new DataValueFloat(0));
	}
	@Override
	public SubElementBaseConfig getConfigSubElement(GuiControlEdit gui,Map<String,DataValue> configs) {
		return new MeterVerticalCSE(gui,configs);
	}
	@Override
	protected void onConfigMapChanged() {
		for (Entry<String,DataValue> entry : configMap.entrySet()) {
			switch(entry.getKey()) {
				case "needleOffset" -> needleOffset = (int)entry.getValue().getNumber();
				case "minValue" -> min = entry.getValue().getNumber();
				case "maxValue" -> max = entry.getValue().getNumber();
				case "decimals" -> decimals = (int)entry.getValue().getNumber();
				case "divisions" -> divisions = (int)entry.getValue().getNumber();
				case "subdivisions" -> subdivisions = (int)entry.getValue().getNumber();
				case "clamp" -> clamp = entry.getValue().getBoolean();
			}
		}
	}
	@Override
	public ControlType getControlType() {
		return ControlType.METER;
	}
	@Override
	public float[] getSize() {
		return new float[]{ 1.7f,5f,1.25f };
	}
	@Override
	public AxisAlignedBB getBoundingBox() {
		return null;
	}
	@SideOnly(Side.CLIENT)
	@Override
	public IModelCustom getModel() {
		return AddonInstrumentModels.meter_v;
	}
	static DoubleBuffer dbuf = null;
	//float ang = 360/32f;
	float curAngle = 0;
	@Override
	public void receiveEvent(ControlEvent evt) {
		if (evt.name.equals("tick")) {
			float val = vars.get("value").getNumber();
			if (clamp) {
				if (min < max)
					val = Math.min(Math.max(val,min),max);
				else
					val = Math.min(Math.max(val,max),min);
			}
			float desiredAngle = -getAngleForRatio(ratio(min,max,val));
			float delta = desiredAngle-curAngle;
			float threshold = 2;
			if (Math.abs(delta) <= threshold)
				curAngle = desiredAngle;
			else {
				float move = Math.max(Math.abs(delta/2.5f),threshold)*Math.signum(delta);
				curAngle += move;
			}
			BlockPos pos = panel.parent.getControlPos();
			CCPMeterVerticalSyncPacket packet = new CCPMeterVerticalSyncPacket(
					pos,
					panel.controls.indexOf(this),
					curAngle
			);
			LeafiaCustomPacket.__start(packet).__sendToAllAround(
					panel.parent.getControlWorld().provider.getDimension(),
					pos,256
			);
		}
		super.receiveEvent(evt);
	}
	public static class CCPMeterVerticalSyncPacket extends CCPSyncPacketBase {
		float value;
		public CCPMeterVerticalSyncPacket() { }
		public CCPMeterVerticalSyncPacket(BlockPos pos,int idx,float value) {
			super(pos,idx);
			this.value = value;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			super.encode(buf);
			buf.writeFloat(value);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			boolean success = load(buf);
			float value = buf.readFloat();
			return (ctx)->{
				if (!success) return;
				if (instrument instanceof MeterVertical meter)
					meter.curAngle = value;
			};
		}
	}
	@Override
	public void render() {
		if (dbuf == null)
			dbuf = GLAllocation.createDirectByteBuffer(16*4).asDoubleBuffer(); // i have no idea how this even works
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		LeafiaGls.color(1,1,1);
		TextureManager texmg = Minecraft.getMinecraft().getTextureManager();
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		WaveFrontObjectVAO mdl = (WaveFrontObjectVAO)getModel();
		texmg.bindTexture(AddonBase.solid);
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(posX,0,posY);
		boolean uni = font.getUnicodeFlag();
		font.setUnicodeFlag(false);
		{
			LeafiaGls.color(0.1F,0.1F,0.1F);
			mdl.renderPart("Base");
			{
				GL11.glEnable(GL11.GL_CLIP_PLANE0);
				dbuf.put(new double[]{ 0,1,0,0 });
				dbuf.rewind();
				GL11.glClipPlane(GL11.GL_CLIP_PLANE0,dbuf);
			}
			LeafiaGls.color(1,1,1);

			LeafiaGls.translate(0,-1.75,0);
			LeafiaGls.rotate(-needleOffset*(360/32f),1,0,0);
			LeafiaGls.translate(0,1.75,0);
			mdl.renderPart("Needle");

			LeafiaGls.translate(0,-1.75,0);
			LeafiaGls.rotate(curAngle,1,0,0);
			LeafiaGls.translate(0,1.75,0);
			mdl.renderPart("Meter");
			LeafiaBrush brush = LeafiaBrush.instance;
			for (int i0 = 0; i0 <= divisions; i0++) {
				float i = i0/(float)divisions;
				LeafiaGls.pushMatrix();
				LeafiaGls.translate(0,-1.75,0);
				LeafiaGls.rotate(getAngleForRatio(i),1,0,0);
				texmg.bindTexture(AddonBase.solid);
				LeafiaGls.color(0,0,0);
				int offset = 5;
				int startX = 26-offset+1;
				int endX = 18-offset-3;
				if (subdivisions > 0 && i0 < divisions) {
					for (int j = 1; j <= subdivisions; j++) {
						LeafiaGls.pushMatrix();
						int endX2 = 22-offset-3;
						float increment = 1f/divisions/(subdivisions+1);
						LeafiaGls.rotate(getAngleForRatio(increment*j+0.5f),1,0,0);
						LeafiaGls.translate(0,2.9,0);
						LeafiaGls.rotate(90,1,0,0);
						LeafiaGls.scale(0.025);
						LeafiaGls.translate(0,-0.5,0);
						brush.startDrawingQuads();
						brush.addVertexWithUV(endX2,1,0,0,1);
						brush.addVertexWithUV(startX,1,0,1,1);
						brush.addVertexWithUV(startX,0,0,1,0);
						brush.addVertexWithUV(endX2,0,0,0,0);
						brush.draw();
						LeafiaGls.popMatrix();
					}
				}
				LeafiaGls.translate(0,2.9,0);
				{
					LeafiaGls.rotate(90,1,0,0);
					LeafiaGls.scale(0.025);
					LeafiaGls.translate(0,-0.5,0);

					brush.startDrawingQuads();
					brush.addVertexWithUV(endX,1,0,0,1);
					brush.addVertexWithUV(startX,1,0,1,1);
					brush.addVertexWithUV(startX,0,0,1,0);
					brush.addVertexWithUV(endX,0,0,0,0);
					brush.draw();
					String s = String.format("%01."+decimals+"f",lerp(min,max,i0/(float)divisions));
					font.drawString(s,15-3-font.getStringWidth(s),-7/2,0);
				}
				LeafiaGls.popMatrix();
			}
		}
		font.setUnicodeFlag(uni);
		GL11.glDisable(GL11.GL_CLIP_PLANE0);
		LeafiaGls.color(1,1,1);
		LeafiaGls.popMatrix();
		LeafiaGls.shadeModel(GL11.GL_FLAT);
	}
	float getAngleForRatio(float ratio) {
		ratio -= 0.5f;
		return -360/(divisions+1f)*ratio*divisions;
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
	static final ResourceLocation[] guiRls = new ResourceLocation[7];
	static final ResourceLocation failsafeRl = new ResourceLocation("ERROR");
	static {
		for (int i = -3; i <= 3; i++)
			guiRls[i+3] = getIntegrated("control_panel/instruments/meter_vertical_"+i+".png");
	}
	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getGuiTexture() {
		if (needleOffset >= -3 && needleOffset <= 3)
			return guiRls[needleOffset+3];
		return failsafeRl;
	}
	@Override
	public Control newControl(ControlPanel controlPanel) {
		return new MeterVertical(name,registryName,controlPanel);
	}
	@Override
	public void populateDefaultNodes(List<ControlEvent> list) { }
}
