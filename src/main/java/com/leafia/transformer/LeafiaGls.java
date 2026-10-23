// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.transformer;

import com.custom_hbm.render.amlfrom1710.CompositeBrush;
import com.hbm.util.RenderUtil;
import com.llib.exceptions.LeafiaDevFlaw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

// Snapshots cached render state for nested addon rendering.
public class LeafiaGls extends GlStateManager {
	protected static boolean preventStackSyncing = false;
	public static class GlStates {
		public final float r = RenderUtil.getCurrentColorRed();
		public final float g = RenderUtil.getCurrentColorGreen();
		public final float b = RenderUtil.getCurrentColorBlue();
		public final float a = RenderUtil.getCurrentColorAlpha();
		public final boolean depth = RenderUtil.isDepthEnabled();
		public final boolean lighting = RenderUtil.isLightingEnabled();
		public final boolean alpha = RenderUtil.isAlphaEnabled();
		public final int alphaFunc = RenderUtil.getAlphaFunc();
		public final float alphaRef = RenderUtil.getAlphaRef();
		public final boolean blend = RenderUtil.isBlendEnabled();
		public final int blendSrc = RenderUtil.getBlendSrcFactor();
		public final int blendDst = RenderUtil.getBlendDstFactor();
		public final int blendSrcAlpha = RenderUtil.getBlendSrcAlphaFactor();
		public final int blendDstAlpha = RenderUtil.getBlendDstAlphaFactor();
		public final int activeTexture = RenderUtil.getActiveTextureUnitEnum();
		public final boolean tex2d = RenderUtil.isTexture2DEnabled();
		public final boolean cull = RenderUtil.isCullEnabled();
		public final boolean depthmask = RenderUtil.isDepthMaskEnabled();
		public final int depthfunc = RenderUtil.getDepthFunc();
		public void apply() {
			if (r == -1 && g == -1 && b == -1 && a == -1) LeafiaGls.resetColor();
			else LeafiaGls.color(r,g,b,a);
			if (depth) LeafiaGls.enableDepth(); else LeafiaGls.disableDepth();
			if (lighting) LeafiaGls.enableLighting(); else LeafiaGls.disableLighting();
			if (alpha) LeafiaGls.enableAlpha(); else LeafiaGls.disableAlpha();
			LeafiaGls.alphaFunc(alphaFunc,alphaRef);
			if (blend) LeafiaGls.enableBlend(); else LeafiaGls.disableBlend();
			LeafiaGls.tryBlendFuncSeparate(blendSrc, blendDst, blendSrcAlpha, blendDstAlpha);
			LeafiaGls.setActiveTexture(activeTexture);
			if (tex2d) LeafiaGls.enableTexture2D(); else LeafiaGls.disableTexture2D();
			if (cull) LeafiaGls.enableCull(); else LeafiaGls.disableCull();
			LeafiaGls.depthMask(depthmask);
			LeafiaGls.depthFunc(depthfunc);
		}
	}
	public static void resetEffects() {
		LeafiaGls.color(1,1,1,1);
		LeafiaGls.disableDepth();
		LeafiaGls.disableLighting();
		LeafiaGls.enableAlpha();
		LeafiaGls.alphaFunc(GL11.GL_ALWAYS,0);
		LeafiaGls.enableBlend();
		LeafiaGls.blendFunc(SourceFactor.SRC_ALPHA,DestFactor.ONE_MINUS_SRC_ALPHA);
	}
	public static void translate(Vec3d vector) {
		LeafiaGls.translate(vector.x,vector.y,vector.z);
	}
	public static void rotate(double angle,Vec3d normal) {
		LeafiaGls.rotate((float) angle, (float) normal.x, (float) normal.y, (float) normal.z);
	}
	public static void scale(double factor) {
		LeafiaGls.scale(factor,factor,factor);
	}
	public static void scale(double x,double y) {
		LeafiaGls.scale(x,y,1);
	}
	public static void inLocalSpace(Runnable callback) {
		LeafiaGls.pushMatrix();
		try {
			callback.run();
		} finally {
			LeafiaGls.popMatrix();
		}
	}
	public static void setGradient(boolean smooth) {
		LeafiaGls.shadeModel(smooth ? GL11.GL_SMOOTH : GL11.GL_FLAT);
	}
	public static class Tools {
		public static Tessellator getTessellator() {
			return Tessellator.getInstance();
		}
		public static BufferBuilder getBufferBuilder() {
			return getTessellator().getBuffer();
		}
		public static CompositeBrush getBrush() {
			return CompositeBrush.instance;
		}
		public static FontRenderer getFontRenderer() {
			return Minecraft.getMinecraft().fontRenderer;
		}
	}
	public static class Util3D {
		public static void rotateToCamera() {
			LeafiaGls.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			LeafiaGls.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		}
	}
	static final List<GlStates> stacks = new ArrayList<>();
	public static void _push() {
		stacks.add(new GlStates());
	}
	public static void _pop() {
		if (stacks.size() <= 0)
			throw new LeafiaDevFlaw("GlStates stack underflow");
		stacks.remove(stacks.size()-1).apply();
	}
	public static void inStack(Runnable callback) {
		LeafiaGls._push();
		try {
			callback.run();
		} finally {
			LeafiaGls._pop();
		}
	}
}
