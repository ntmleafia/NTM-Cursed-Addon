package com.leafia.contents.machines.elevators.items;

import com.hbm.render.loader.WaveFrontObjectVAO;
import com.leafia.contents.machines.elevators.car.ElevatorRender;
import com.leafia.contents.machines.elevators.car.ElevatorRender.S6;
import com.leafia.dev.LeafiaItemRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class EvSpawnItemRender extends LeafiaItemRenderer {
	@Override
	protected double _sizeReference() {
		return 5.2;
	}
	@Override
	protected double _itemYoffset() {
		return -0.05;
	}
	@Override
	protected ResourceLocation __getTexture() {
		return ElevatorRender.support;
	}
	@Override
	protected WaveFrontObjectVAO __getModel() {
		return S6.mdl;
	}
	@Override
	public void renderCommon() {
		GL11.glScaled(0.5, 0.5, 0.5);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		bindTexture(__getTexture());
		__getModel().renderPart("Frames");
		GlStateManager.shadeModel(GL11.GL_FLAT);
	}
}
