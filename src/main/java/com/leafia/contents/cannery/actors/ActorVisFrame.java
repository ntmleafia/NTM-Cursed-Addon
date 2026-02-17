package com.leafia.contents.cannery.actors;

import com.hbm.wiaj.JarScript;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class ActorVisFrame extends ActorVisBase {
	final float scale;
	public ActorVisFrame(JarScript script,int x,int y,int z,int color) {
		this(script,x,y,z,color,1);
	}
	public ActorVisFrame(JarScript script,int x,int y,int z,int color,float scale) {
		super(script,x,y,z,color);
		this.scale = scale;
	}
	@Override
	public void renderContents() {
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder buf = tes.getBuffer();
		buf.begin(GL11.GL_LINES,DefaultVertexFormats.POSITION_COLOR);
		LeafiaGls.disableTexture2D();
		float offs = (1-scale)/2;
		LeafiaGls.translate(offs,offs,offs);
		LeafiaGls.scale(scale,scale,scale);
		// this is spaghetti
		buf.pos(0,0,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,0,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(0,1,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,1,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(0,0,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,0,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(0,1,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,1,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();

		buf.pos(0,0,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(0,1,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,0,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,1,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(0,0,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(0,1,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,0,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,1,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();

		buf.pos(0,0,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(0,0,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,0,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,0,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(0,1,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(0,1,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,1,0).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		buf.pos(1,1,1).color((color>>>16&0xFF)/255f,(color>>>8&0xFF)/255f,(color&0xFF)/255f,1).endVertex();
		tes.draw();
	}
}
