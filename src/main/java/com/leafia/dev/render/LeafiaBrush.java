package com.leafia.dev.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

/// For making porting easy
public class LeafiaBrush {
	public static final LeafiaBrush instance = new LeafiaBrush();
	public final Tessellator tessellator = Tessellator.getInstance();
	public final BufferBuilder buf = tessellator.getBuffer();
	public void draw() {
		tessellator.draw();
	}
	public void startDrawingQuads() {
		buf.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_TEX);
	}
	public void startDrawing(BrushMode mode,VertexFormat format) {
		buf.begin(mode.value,format);
	}
	public enum BrushMode {
		QUADS(GL11.GL_QUADS),
		TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN),
		LINES(GL11.GL_LINES);
		final int value;
		BrushMode(int value) {
			this.value = value;
		}
	}
	public void addVertex(double x,double y,double z) {
		buf.pos(x,y,z).endVertex();
	}
	public void addVertexWithUV(double x,double y,double z,double u,double v) {
		buf.pos(x,y,z).tex(u,v).endVertex();
	}
	public void addVertexWithUVAndColor(double x,double y,double z,double u,double v,float r,float g,float b,float a) {
		buf.pos(x,y,z).tex(u,v).color(r,g,b,a).endVertex();
	}
	public void addVertexWithColor(double x,double y,double z,float r,float g,float b,float a) {
		buf.pos(x,y,z).color(r,g,b,a).endVertex();
	}
}
