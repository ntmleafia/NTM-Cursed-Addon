package com.leafia.contents.machines.powercores.dfc.particles;

import com.hbm.main.ResourceManager;
import com.hbm.render.NTMRenderHelper;
import com.leafia.AddonBase;
import com.leafia.eventbuses.LeafiaClientListener;
import com.leafia.eventbuses.LeafiaClientListener.HandlerClient;
import com.leafia.transformer.LeafiaGls;
import com.llib.technical.LeafiaEase;
import com.llib.technical.LeafiaEase.Direction;
import com.llib.technical.LeafiaEase.Ease;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticleNuke extends Particle {
	boolean reachedPlayer = false;
	public int radius = 250;
	public int flashTime = 50;
	public ParticleNuke(World world,BlockPos pos) {
		super(world,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5);
		particleMaxAge = 20*3;
	}
	@Override
	public int getFXLayer() {
		return 3;
	}
	float getScale(float age) {
		LeafiaEase ease = new LeafiaEase(Ease.QUAD,Direction.O);
		return 0+(float)(ease.get(age/particleMaxAge)*radius*2);
	}
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setExpired();
		}
	}
	@Override
	public void renderParticle(BufferBuilder buffer,Entity entityIn,float partialTicks,float rotationX,float rotationZ,float rotationYZ,float rotationXY,float rotationXZ) {
		NTMRenderHelper.resetParticleInterpPos(entityIn, partialTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(AddonBase.solid);

		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		LeafiaGls.pushMatrix();
		float a = (particleAge-(particleMaxAge-20))/20f;
		LeafiaGls.color(1,1,1,1-Math.max(a,0));
		float pX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float pY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float pZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
		LeafiaGls.translate(pX,pY,pZ);
		float sc = getScale(particleAge+partialTicks);
		LeafiaGls.scale(sc);
		ResourceManager.sphere_ruv.renderAll();
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (!reachedPlayer && sc/2 >= new Vec3d(posX,posY,posZ).distanceTo(new Vec3d(player.posX,player.posY+player.getEyeHeight(),player.posZ))) {
			reachedPlayer = true;
			HandlerClient.dfcFlashTicks = 100+flashTime;
		}
		LeafiaGls.popMatrix();
		LeafiaGls.color(1,1,1);

		GlStateManager.enableLighting();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
	}
}
