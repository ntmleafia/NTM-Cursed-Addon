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

// Yeah uh this is bullsh*t i could have just used GL11.getState which i didn't know exis
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
		public final boolean tex2d = RenderUtil.isTexture2DEnabled();
		public final boolean cull = RenderUtil.isCullEnabled();
		public final boolean depthmask = RenderUtil.isDepthMaskEnabled();
		public final int depthfunc = RenderUtil.getDepthFunc();
		public void apply() {
			LeafiaGls.color(r,g,b,a);
			if (RenderUtil.isDepthEnabled() != depth) {
				if (depth) LeafiaGls.enableDepth();
				else LeafiaGls.disableDepth();
			}
			if (RenderUtil.isLightingEnabled() != lighting) {
				if (lighting) LeafiaGls.enableLighting();
				else LeafiaGls.disableLighting();
			}
			if (RenderUtil.isAlphaEnabled() != alpha) {
				if (alpha) LeafiaGls.enableAlpha();
				else LeafiaGls.disableAlpha();
			}
			if (RenderUtil.getAlphaFunc() != alphaFunc || RenderUtil.getAlphaRef() != alphaRef)
				LeafiaGls.alphaFunc(alphaFunc,alphaRef);
			if (RenderUtil.isBlendEnabled() != blend) {
				if (blend) LeafiaGls.enableBlend();
				else LeafiaGls.disableBlend();
			}
			if (RenderUtil.getBlendSrcFactor() != blendSrc || RenderUtil.getBlendDstFactor() != blendDst)
				LeafiaGls.blendFunc(blendSrc,blendDst);
			if (RenderUtil.isTexture2DEnabled() != tex2d) {
				if (tex2d) LeafiaGls.enableTexture2D();
				else LeafiaGls.disableTexture2D();
			}
			if (RenderUtil.isCullEnabled() != cull) {
				if (cull) LeafiaGls.enableCull();
				else LeafiaGls.disableCull();
			}
			if (RenderUtil.getDepthFunc() != depthfunc)
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
		GL11.glRotated(angle,normal.x,normal.y,normal.z);
	}
	public static void scale(double factor) {
		LeafiaGls.scale(factor,factor,factor);
	}
	public static void scale(double x,double y) {
		LeafiaGls.scale(x,y,1);
	}
	public static void inLocalSpace(Runnable callback) {
		LeafiaGls.pushMatrix();
		callback.run();
		LeafiaGls.popMatrix();
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
		callback.run();
		LeafiaGls._pop();
	}
}
