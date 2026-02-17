package com.leafia.unsorted;

import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.leafia.AddonBase;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class ParticleFlash extends Particle {
	float maxScale = 10;
	int ticksIn = 5;
	int ticksOut = 10;
	public ParticleFlash(World worldIn,double posXIn,double posYIn,double posZIn,float maxScale,int ticksIn,int ticksOut) {
		super(worldIn,posXIn,posYIn,posZIn);
		this.maxScale = maxScale;
		this.ticksIn = ticksIn;
		this.ticksOut = ticksOut;
		particleMaxAge = ticksIn+ticksOut;
	}
	@Override
	public int getFXLayer(){
		return 3;
	}
	@Override
	public void renderParticle(BufferBuilder buffer,Entity entityIn,float partialTicks,float rotationX,float rotationZ,float rotationYZ,float rotationXY,float rotationXZ) {
		NTMRenderHelper.resetParticleInterpPos(entityIn, partialTicks);
		LeafiaGls.pushMatrix();
		LeafiaGls.translate(posX-interpPosX,posY-interpPosY,posZ-interpPosZ);
		NTMRenderHelper.bindTexture(AddonBase.solid_e);
		float scale = 0;
		if ((particleAge+partialTicks) < ticksIn)
			scale = (particleAge+partialTicks)/(float)ticksIn;
		else
			scale = 1-((particleAge+partialTicks)-ticksIn)/(float)ticksOut;
		LeafiaGls.scale(0.05*maxScale*Math.pow(scale,0.75));
		renderFlash(1,20,1,1,1,1);
		LeafiaGls.popMatrix();
	}
	private void renderFlash(float scale, double intensity, double alpha, float r, float g, float b) {

		GL11.glScalef(0.2F, 0.2F, 0.2F);

		double inverse = 1.0D - intensity;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuffer();
		RenderHelper.disableStandardItemLighting();

		Random random = new Random(432L);
		//GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.enableCull();
		GlStateManager.depthMask(false);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

		GL11.glPushMatrix();

		for(int i = 0; i < 300; i++) {

			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);

			float vert1 = (random.nextFloat() * 20.0F + 5.0F + 1 * 10.0F) * (float)(intensity * scale);
			float vert2 = (random.nextFloat() * 2.0F + 1.0F + 1 * 2.0F) * (float)(intensity * scale);

			buf.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			buf.pos(0D, 0D, 0D).color(1.0F, 1.0F, 1.0F, (float)alpha/*(float) inverse*/).endVertex();
			buf.pos(-0.866D * vert2, vert1, -0.5D * vert2).color(r,g,b, 0.0F).endVertex();
			buf.pos(0.866D * vert2, vert1, -0.5D * vert2).color(r,g,b, 0.0F).endVertex();
			buf.pos(0.0D, vert1, 1.0D * vert2).color(r,g,b, 0.0F).endVertex();
			buf.pos(-0.866D * vert2, vert1, -0.5D * vert2).color(r,g,b, 0.0F).endVertex();
			tessellator.draw();
		}

		GL11.glPopMatrix();

		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1, 1, 1, 1);
		//GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		RenderHelper.enableStandardItemLighting();
	}
}
