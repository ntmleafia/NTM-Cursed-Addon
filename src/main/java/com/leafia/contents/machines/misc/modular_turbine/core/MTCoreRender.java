// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine.core;

import com.leafia.contents.machines.misc.modular_turbine.ModularTurbineComponentRender;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

public class MTCoreRender extends TileEntitySpecialRenderer<MTCoreTE> {
	@Override
	public void render(MTCoreTE te,double x,double y,double z,float partialTicks,int destroyStage,float alpha) {
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(x+0.5,y,z+0.5);
		switch(te.getBlockMetadata()-10) {
			case 2 -> LeafiaGls.rotate(180,0F,1F,0F);
			case 3 -> LeafiaGls.rotate(0,0F,1F,0F);
			case 4 -> LeafiaGls.rotate(270,0F,1F,0F);
			case 5 -> LeafiaGls.rotate(90,0F,1F,0F);
		}
		LeafiaGls.shadeModel(GL11.GL_SMOOTH);
		bindTexture(ModularTurbineComponentRender.tex1);
		ModularTurbineComponentRender.mdl.renderPart("Core");
		LeafiaGls.shadeModel(GL11.GL_FLAT);
		LeafiaGls.popMatrix();
	}
}
