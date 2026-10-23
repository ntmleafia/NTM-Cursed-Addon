// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.starbound;

import com.hbm.inventory.control_panel.*;
import com.hbm.inventory.control_panel.types.*;
import com.hbm.inventory.control_panel.controls.ControlType;
import com.hbm.inventory.control_panel.nodes.*;
import com.hbm.inventory.control_panel.nodes.NodeMath.Operation;
import com.hbm.render.loader.IModelCustom;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.machines.controlpanel.instruments.types.AddonInstrumentModels;
import com.leafia.contents.machines.controlpanel.nodes.NodeSounder;
import com.leafia.contents.machines.controlpanel.nodes.NodeSounder.NodeSnd;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

import static com.leafia.AddonBase.getIntegrated;

public class LargeButton extends com.hbm.inventory.control_panel.controls.ButtonPush {
	public LargeButton(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
        vars.remove("color");
		vars.put("isPushed",new DataValueFloat(0));
		vars.put("isLit",new DataValueFloat(0));
	}
	@Override
	public ControlType getControlType() {
		return ControlType.BUTTON;
	}
	@Override
	public float[] getSize() {
		return new float[]{ 6,6,0.5f };
	}
	@SideOnly(Side.CLIENT)
	@Override
	public IModelCustom getModel() {
		return AddonInstrumentModels.large_button;
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
		mdl.renderAllExcept("Circle.001");
		float lX = OpenGlHelper.lastBrightnessX;
		float lY = OpenGlHelper.lastBrightnessY;
		if (!vars.get("isLit").getBoolean())
			LeafiaGls.color(0.5f,0.5f,0.5f);
		else
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,240,240);
		if (vars.get("isPushed").getBoolean())
			LeafiaGls.translate(0,-0.125,0);
		mdl.renderPart("Circle.001");
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,lX,lY);
		LeafiaGls.popMatrix();
		LeafiaGls.shadeModel(GL11.GL_FLAT);
		LeafiaGls.color(1,1,1);
	}
	@Override
	public List<String> getOutEvents() {
		return Collections.singletonList("ctrl_press");
	}
	static final ResourceLocation guiRl = getIntegrated("control_panel/instruments/starboundbuttonlol.png");
	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getGuiTexture() {
		return guiRl;
	}
	@Override
	public Control newControl(ControlPanel controlPanel) {
		return new LargeButton(name,registryName,controlPanel);
	}
	@Override
	public void populateDefaultNodes(List<ControlEvent> receiveEvents) {
        super.populateDefaultNodes(receiveEvents);
        var tick = receiveNodeMap.get("tick");
        ((NodeBuffer) tick.nodes.get(1)).inputs.get(1).setDefault(new DataValueFloat(45));

        var press = new NodeSystem(this);
        var sound = new NodeSounder(230, 40);
        sound.snd = NodeSnd.BUTTON;
        press.addNode(sound);
        var one = new NodeMath(170, 100).setData(Operation.ADD);
        one.inputs.get(0).setDefault(new DataValueFloat(1));
        press.addNode(one);
        String[] targets = {"isLit", "isPushed"};
        for (int target = 0; target < targets.length; target++) {
            var write = new NodeSetVar(230, target == 0 ? 140 : 90, this).setData(targets[target], false);
            write.inputs.get(0).setData(one, 0, true);
            press.addNode(write);
        }
        receiveNodeMap.put("ctrl_press", press);
	}
}
