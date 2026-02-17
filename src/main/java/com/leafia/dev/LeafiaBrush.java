package com.leafia.dev;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/// For making porting easy
public class LeafiaBrush {
	public static final LeafiaBrush instance = new LeafiaBrush();
	Tessellator tessellator = Tessellator.getInstance();
	BufferBuilder buf = tessellator.getBuffer();
	public void draw() {
		tessellator.draw();
	}
	public void startDrawingQuads() {
		buf.begin(GL11.GL_QUADS,DefaultVertexFormats.POSITION_TEX);
	}
	public void addVertexWithUV(double x,double y,double z,double u,double v) {
		buf.pos(x,y,z).tex(u,v).endVertex();
	}
}
