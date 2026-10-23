// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.controlpanel.instruments.types.lever;

import com.hbm.inventory.control_panel.Control;
import com.hbm.inventory.control_panel.ControlPanel;
import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.AddonBase;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import static com.leafia.AddonBase.getIntegrated;

public class LeverHorizontal extends LeverBase {
	public LeverHorizontal(String name,String registryName,ControlPanel panel) {
		super(name,registryName,panel);
	}
	@Override
	public float[] getSize() {
		return new float[]{ 2,1,1.25f };
	}
	@Override
	public void render() {
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		LeafiaGls.color(1,1,1);
		TextureManager texmg = Minecraft.getMinecraft().getTextureManager();
		WaveFrontObjectVAO mdl = (WaveFrontObjectVAO)getModel();
		texmg.bindTexture(AddonBase.solid);
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(posX,0,posY);
		LeafiaGls.rotate(-90,0,1,0);
		LeafiaGls.scale(0.5);
		LeafiaGls.color(0.306f,0.306f,0.306f);
		mdl.renderPart("Base");
		LeafiaGls.color(0.765f,0.765f,0.765f);
		mdl.renderPart("Base.001");
		LeafiaGls.translate(0,0.1,0);
		LeafiaGls.rotate(-50*(curLevel/50-1f),1,0,0);
		LeafiaGls.translate(0,-0.1,0);
		LeafiaGls.color(0.533f,0.533f,0.533f);
		mdl.renderPart("Lever");
		LeafiaGls.popMatrix();
		LeafiaGls.shadeModel(GL11.GL_FLAT);
	}
	static final ResourceLocation guiRl = getIntegrated("control_panel/instruments/lever_horizontal.png");
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGuiTexture() {
		return guiRl;
	}
	@Override
	public Control newControl(ControlPanel controlPanel) {
		return new LeverHorizontal(name,registryName,controlPanel);
	}
}
