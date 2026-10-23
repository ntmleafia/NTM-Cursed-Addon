// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.render.NTMRenderHelper;
import com.hbm.render.misc.DiamondPronter;
import com.hbm.render.misc.EnumSymbol;
import com.leafia.transformer.LeafiaGls;
import com.leafia.unsorted.NTMRenderHelperWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = DiamondPronter.class,remap = false)
public class MixinDiamondPronter {
	@Shadow @Final private static ResourceLocation texture;
	private static final ResourceLocation secondTex = new ResourceLocation("leafia","textures/numbers.png");

	private static void drawNumber(float oX,float oY,float s,int value) {
		float p = 1F/64F;
		float width = 11.2F * s;
		float height = 14F * s;


		Minecraft.getMinecraft().renderEngine.bindTexture(secondTex);
		LeafiaGls.color(0,0,0);

		int length = value > 0 ? (int)Math.log10(value)+1 : 1;
		float startOffset = width*(length-1);
		for (int i = 0; i < length; i++) {
			int v = value/((int)Math.pow(10,i))%10;
			int x = v*5;
			int y = 0;
			float localOffset = -startOffset+i*width*2;
			NTMRenderHelper.startDrawingTexturedQuads();
			NTMRenderHelperWrapper.addVertexWithUV(0.002,height+oY,-width+oX+localOffset,(x+4)*p,y*p);
			NTMRenderHelperWrapper.addVertexWithUV(0.002,height+oY,width+oX+localOffset,x*p,y*p);
			NTMRenderHelperWrapper.addVertexWithUV(0.002,-height+oY,width+oX+localOffset,x*p,(y+5)*p);
			NTMRenderHelperWrapper.addVertexWithUV(0.002,-height+oY,-width+oX+localOffset,(x+4)*p,(y+5)*p);
			NTMRenderHelper.draw();
		}

		LeafiaGls.color(1,1,1);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
	}

	/**
	 * @author ntleafia
	 * @reason support any number
	 */
	@Overwrite
	public static void pront(int poison, int flammability, int reactivity, EnumSymbol symbol) {

		GlStateManager.pushMatrix();

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		float p = 1F/256F;
		float s = 1F/139F;
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

		NTMRenderHelper.startDrawingTexturedQuads();
		NTMRenderHelperWrapper.addVertexWithUV(0.0, 0.5, -0.5, p * 144, p * 45);
		NTMRenderHelperWrapper.addVertexWithUV(0.0, 0.5, 0.5, p * 5, p * 45);
		NTMRenderHelperWrapper.addVertexWithUV(0.0, -0.5, 0.5, p * 5, p * 184);
		NTMRenderHelperWrapper.addVertexWithUV(0.0, -0.5, -0.5, p * 144, p * 184);
		NTMRenderHelper.draw();

		float width = 10F * s;
		float height = 14F * s;

		if(poison >= 0 /*&& poison < 6*/) {

			float oY = 0;
			float oZ = 33 * s;
/*
			int x = 5 + (poison - 1) * 24;
			int y = 5;

			if(poison == 0) x = 125;

			NTMRenderHelper.startDrawingTexturedQuads();
			NTMRenderHelper.addVertexWithUV(0.01, height + oY, -width + oZ, (x + 20) * p, y * p);
			NTMRenderHelper.addVertexWithUV(0.01, height + oY, width + oZ, x * p, y * p);
			NTMRenderHelper.addVertexWithUV(0.01, -height + oY, width + oZ, x * p, (y + 28) * p);
			NTMRenderHelper.addVertexWithUV(0.01, -height + oY, -width + oZ, (x + 20) * p, (y + 28) * p);
			NTMRenderHelper.draw();
*/
			drawNumber(oZ,oY,s,poison);
		}

		if(flammability >= 0 /*&& flammability < 6*/) {

			float oY = 33 * s;
			float oZ = 0;
/*
			int x = 5 + (flammability - 1) * 24;
			int y = 5;

			if(flammability == 0) x = 125;

			NTMRenderHelper.startDrawingTexturedQuads();
			NTMRenderHelper.addVertexWithUV(0.01, height + oY, -width + oZ, (x + 20) * p, y * p);
			NTMRenderHelper.addVertexWithUV(0.01, height + oY, width + oZ, x * p, y * p);
			NTMRenderHelper.addVertexWithUV(0.01, -height + oY, width + oZ, x * p, (y + 28) * p);
			NTMRenderHelper.addVertexWithUV(0.01, -height + oY, -width + oZ, (x + 20) * p, (y + 28) * p);
			NTMRenderHelper.draw();
 */
			drawNumber(oZ,oY,s,flammability);
		}

		if(reactivity >= 0 /*&& reactivity < 6*/) {

			float oY = 0;
			float oZ = -33 * s;
/*
			int x = 5 + (reactivity - 1) * 24;
			int y = 5;

			if(reactivity == 0) x = 125;

			NTMRenderHelper.startDrawingTexturedQuads();
			NTMRenderHelper.addVertexWithUV(0.01, height + oY, -width + oZ, (x + 20) * p, y * p);
			NTMRenderHelper.addVertexWithUV(0.01, height + oY, width + oZ, x * p, y * p);
			NTMRenderHelper.addVertexWithUV(0.01, -height + oY, width + oZ, x * p, (y + 28) * p);
			NTMRenderHelper.addVertexWithUV(0.01, -height + oY, -width + oZ, (x + 20) * p, (y + 28) * p);
			NTMRenderHelper.draw();
 */
			drawNumber(oZ,oY,s,reactivity);
		}


		float symSize = 59F/2F * s;

		if(symbol != EnumSymbol.NONE) {

			float oY = -33 * s;
			float oZ = 0;

			int x = symbol.x;
			int y = symbol.y;

			NTMRenderHelper.startDrawingTexturedQuads();
			NTMRenderHelperWrapper.addVertexWithUV(0.002, symSize + oY, -symSize + oZ, (x + 59) * p, y * p);
			NTMRenderHelperWrapper.addVertexWithUV(0.002, symSize + oY, symSize + oZ, x * p, y * p);
			NTMRenderHelperWrapper.addVertexWithUV(0.002, -symSize + oY, symSize + oZ, x * p, (y + 59) * p);
			NTMRenderHelperWrapper.addVertexWithUV(0.002, -symSize + oY, -symSize + oZ, (x + 59) * p, (y + 59) * p);
			NTMRenderHelper.draw();
		}

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}
