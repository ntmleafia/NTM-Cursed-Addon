package com.leafia.transformer;

import com.custom_hbm.render.amlfrom1710.CompositeBrush;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

// Yeah uh this is bullsh*t i could have just used GL11.getState which i didn't know exis
public class LeafiaGls extends GlStateManager {
	protected static boolean preventStackSyncing = false;
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
}
