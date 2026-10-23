// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.starbound;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.controls.ControlType;
import com.hbm.inventory.control_panel.nodes.NodeGetVar;
import com.hbm.inventory.control_panel.nodes.NodeMath;
import com.hbm.inventory.control_panel.nodes.NodeMath.Operation;
import com.hbm.inventory.control_panel.nodes.NodeSetVar;
import com.hbm.render.loader.IModelCustom;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.machines.controlpanel.CCPSyncPacketBase;
import com.leafia.contents.machines.controlpanel.instruments.types.AddonInstrumentModels;
import com.leafia.dev.custompacket.LeafiaCustomPacket;
import com.leafia.dev.optimization.bitbyte.LeafiaBuf;
import com.leafia.init.LeafiaSoundEvents;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static com.leafia.AddonBase.getIntegrated;

public class LargeSwitch extends Control {
	float curLevel = 50;
	public LargeSwitch(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
		vars.put("position",new DataValueFloat(0));
	}
	@Override
	public ControlType getControlType() {
		return ControlType.SWITCH;
	}
	@Override
	public float[] getSize() {
		return new float[]{ 6,6,0.5f };
	}
	@SideOnly(Side.CLIENT)
	@Override
	public IModelCustom getModel() {
		return AddonInstrumentModels.large_switch;
	}
	static final ResourceLocation palette = getIntegrated("control_panel/instruments/largeswitch_palette.png");
	@Override
	public void render() {
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		LeafiaGls.color(1,1,1);
		TextureManager texmg = Minecraft.getMinecraft().getTextureManager();
		WaveFrontObjectVAO mdl = (WaveFrontObjectVAO)getModel();
		texmg.bindTexture(palette);
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(posX,0,posY);
		LeafiaGls.scale(3); // that was tiny as shit
		mdl.renderAllExcept("Switch");
		LeafiaGls.translate(0,0.125,0);
		LeafiaGls.rotate(-65*(1-curLevel/50),1,0,0);
		LeafiaGls.translate(0,-0.125,0);
		mdl.renderPart("Switch");
		LeafiaGls.popMatrix();
		LeafiaGls.shadeModel(GL11.GL_FLAT);
	}
	static final float increment = 200/7f;
	@Override
	public void receiveEvent(ControlEvent evt) {
		if (evt.name.equals("ctrl_press"))
			panel.parent.getControlWorld().playSound(null,panel.parent.getControlPos(),LeafiaSoundEvents.sbWallSwitch,SoundCategory.BLOCKS,0.5f,1);
		else if (evt.name.equals("tick")) {
			vars.put("position",new DataValueFloat(Math.max(Math.min(vars.get("position").getNumber(),100),0)));
			float tgtLevel = vars.get("position").getNumber();
			float delta = tgtLevel-curLevel;
			float difference = Math.abs(delta);
			if (difference > 0) {
				if (difference <= increment)
					curLevel = tgtLevel;
				else {
					float direction = Math.signum(delta);
					curLevel += direction*increment;
				}
			}
			BlockPos pos = panel.parent.getControlPos();
			CCPLargeSwitchSyncPacket packet = new CCPLargeSwitchSyncPacket(
					pos,
					panel.controls.indexOf(this),
					curLevel
			);
			LeafiaCustomPacket.__start(packet).__sendToAllAround(
					panel.parent.getControlWorld().provider.getDimension(),
					pos,256
			);
		}
		super.receiveEvent(evt);
	}
	public static class CCPLargeSwitchSyncPacket extends CCPSyncPacketBase {
		float level = 0;
		public CCPLargeSwitchSyncPacket() { }
		public CCPLargeSwitchSyncPacket(BlockPos pos,int idx,float level) {
			super(pos,idx);
			this.level = level;
		}
		@Override
		public void encode(LeafiaBuf buf) {
			super.encode(buf);
			buf.writeFloat(level);
		}
		@Override
		public @Nullable Consumer<MessageContext> decode(LeafiaBuf buf) {
			boolean success = load(buf);
			float level = buf.readFloat();
			return (ctx)->{
				if (!success) return;
				if (instrument instanceof LargeSwitch sw)
					sw.curLevel = level;
			};
		}
	}
	@Override
	public List<String> getOutEvents() {
		return Collections.singletonList("ctrl_press");
	}
	static final ResourceLocation guiRl = getIntegrated("control_panel/instruments/starboundswitchlol.png");
	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getGuiTexture() {
		return guiRl;
	}
	@Override
	public Control newControl(ControlPanel controlPanel) {
		return new LargeSwitch(name,registryName,controlPanel);
	}
	@Override
	public void populateDefaultNodes(List<ControlEvent> list) {
		NodeSystem ctrl_press = new NodeSystem(this);
		{
			NodeGetVar node0 = new NodeGetVar(170,100,this).setData("position",false);
			ctrl_press.addNode(node0);

			NodeMath node1 = new NodeMath(230,120).setData(Operation.SUB);
			node1.inputs.get(0).setDefault(new DataValueFloat(100));
			node1.inputs.get(1).setData(node0,0,true);
			ctrl_press.addNode(node1);

			NodeSetVar node2 = new NodeSetVar(290,130,this).setData("position",false);
			node2.inputs.get(0).setData(node1,0,true);
			ctrl_press.addNode(node2);
		}
		receiveNodeMap.put("ctrl_press",ctrl_press);
	}
}
