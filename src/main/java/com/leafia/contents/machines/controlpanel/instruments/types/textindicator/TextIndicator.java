// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.textindicator;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.controls.ControlType;
import com.hbm.inventory.control_panel.controls.configs.SubElementBaseConfig;
import com.hbm.render.loader.IModelCustom;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.contents.machines.controlpanel.CCPSyncPacketBase;
import com.leafia.contents.machines.controlpanel.instruments.types.AddonInstrumentModels;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import static com.leafia.AddonBase.getIntegrated;

public class TextIndicator extends Control {
	float red = 255;
	float green = 0;
	float blue = 0;
	String text = "A3-5";
	public TextIndicator(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
		vars.put("isLit",new DataValueFloat(0));
		configMap.put("red",new DataValueFloat(red));
		configMap.put("green",new DataValueFloat(green));
		configMap.put("blue",new DataValueFloat(blue));
		configMap.put("text",new DataValueString(text));
	}
	@Override
	public SubElementBaseConfig getConfigSubElement(GuiControlEdit gui,Map<String,DataValue> configs) {
		return new TextIndicatorCSE(gui,configs);
	}
	@Override
	protected void onConfigMapChanged() {
		for (Entry<String,DataValue> entry : configMap.entrySet()) {
			switch(entry.getKey()) {
				case "red" -> red = MathHelper.clamp(entry.getValue().getNumber(),0,255);
				case "green" -> green = MathHelper.clamp(entry.getValue().getNumber(),0,255);
				case "blue" -> blue = MathHelper.clamp(entry.getValue().getNumber(),0,255);
				case "text" -> text = entry.getValue().toString();
			}
		}
	}
	@Override
	public ControlType getControlType() {
		return ControlType.INDICATOR;
	}
	@Override
	public float[] getSize() {
		return new float[]{ 6.75F/1.5f,/*2.75F/1.5f*/1.8F,0.375F/1.5f };
	}
	static final ResourceLocation tex = getIntegrated("control_panel/instruments/textindicator_light.png");
	@Override
	public void render() {
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		LeafiaGls.color(1,1,1);
		TextureManager texmg = Minecraft.getMinecraft().getTextureManager();
		WaveFrontObjectVAO mdl = (WaveFrontObjectVAO)getModel();
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(posX,0,posY);
		LeafiaGls.scale(1/1.5f); // now it's too big >:/
		texmg.bindTexture(AddonBase.solid);
		LeafiaGls.color(0.29f,0.29f,0.29f);
		mdl.renderAllExcept("Plane.001");
		float lX = OpenGlHelper.lastBrightnessX;
		float lY = OpenGlHelper.lastBrightnessY;
		texmg.bindTexture(tex);
		float offset = 20;
		LeafiaGls.color(
				red/(255+offset)*0.25f+offset/255,
				green/(255+offset)*0.25f+offset/255,
				blue/(255+offset)*0.25f+offset/255
		);
		offset = 80;
		mdl.renderPart("Plane.001");
		LeafiaGls.enableBlend();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240,240);
		LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE);
		LeafiaGls.translate(0,0.001,0);
		LeafiaGls.color(
				red/(255+offset)+offset/255,
				green/(255+offset)+offset/255,
				blue/(255+offset)+offset/255,
				(float)Math.pow((float)current/duration,0.5)
		);
		mdl.renderPart("Plane.001");
		LeafiaGls.color(1,1,1);
		LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,lX,lY);
		LeafiaGls.disableBlend();
		LeafiaGls.translate(0,0.376,0);
		LeafiaGls.scale(0.1);
		LeafiaGls.rotate(90,1,0,0);
		font.drawString(text,-font.getStringWidth(text)/2,-7/2,0x4a4a4a);
		LeafiaGls.color(1,1,1);
		LeafiaGls.popMatrix();
	}
	@Override
	public void renderControl(float[] renderBox,Control selectedControl,GuiControlEdit gui) {
		float boxMinX = renderBox[0];
		float boxMinY = renderBox[1];
		float boxMaxX = renderBox[2];
		float boxMaxY = renderBox[3];
		super.renderControl(renderBox,selectedControl,gui);
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		LeafiaGls.pushMatrix();
		LeafiaGls.translate((boxMinX+boxMaxX)/2,(boxMinY+boxMaxY)/2,0);
		LeafiaGls.scale(0.075);
		font.drawString(text,-font.getStringWidth(text)/2,-7/2,0x4a4a4a);
		LeafiaGls.popMatrix();
	}
	public int current = 0;
	public static int duration = 3;
	@Override
	public void receiveEvent(ControlEvent evt) {
		if (evt.name.equals("tick")) {
			int target = vars.get("isLit").getBoolean() ? duration : 0;
			if (current != target) {
				if (current < target) current++;
				else current--;
			}
			BlockPos pos = panel.parent.getControlPos();
			CCPTextIndicatorSyncPacket packet = new CCPTextIndicatorSyncPacket(
					pos,
					panel.controls.indexOf(this),
					current
			);
			LeafiaCustomPacket.__start(packet).__sendToAllAround(
					panel.parent.getControlWorld().provider.getDimension(),
					pos,256
			);
		}
		super.receiveEvent(evt);
	}
	public static class CCPTextIndicatorSyncPacket extends CCPSyncPacketBase {
		int value;
		public CCPTextIndicatorSyncPacket() { }
		public CCPTextIndicatorSyncPacket(BlockPos pos,int idx,int value) {
			super(pos,idx);
			this.value = value;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			super.encode(buf);
			buf.writeByte(value);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			boolean success = load(buf);
			int value = buf.readByte();
			return (ctx)->{
				if (!success) return;
				if (instrument instanceof TextIndicator indicator)
					indicator.current = value;
			};
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IModelCustom getModel() {
		return AddonInstrumentModels.text_indicator;
	}
	static final ResourceLocation gui_rl = getIntegrated("control_panel/instruments/textindicator.png");
	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getGuiTexture() {
		return gui_rl;
	}
	@Override
	public Control newControl(ControlPanel controlPanel) {
		return new TextIndicator(name,registryName,controlPanel);
	}
	@Override
	public void populateDefaultNodes(List<ControlEvent> list) { }
	@Override
	public AxisAlignedBB getBoundingBox() {
		return null;
	}
}
