package com.leafia.unsorted;

import com.hbm.main.client.NTMClientRegistry;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBasicSmoke extends Particle {

	private int age;
	public int maxAge;
	public float scale = 1;
	public float alphaMultiplier = 1;

	public ParticleBasicSmoke(World worldIn,double posXIn,double posYIn,double posZIn) {
		super(worldIn, posXIn, posYIn, posZIn);
		maxAge = 100 + rand.nextInt(40);
		this.setParticleTexture(NTMClientRegistry.contrail);
		setParticleColor(0.25f,0.25f,0.25f);
	}

	public void setParticleColor(float r,float g,float b) {
		particleRed = r;
		particleGreen = g;
		particleBlue = b;
	}

	public void setMotion(double x, double y, double z){
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		particleAlpha = (1 - ((float) age / (float) maxAge))*alphaMultiplier;

		++this.age;

		if (this.age == this.maxAge) {
			this.setExpired();
		}

		this.motionX *= 0.7599999785423279D;
		this.motionY *= 0.7599999785423279D;
		this.motionZ *= 0.7599999785423279D;

		this.move(this.motionX, this.motionY, this.motionZ);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public void renderParticle(BufferBuilder buffer,Entity entityIn,float partialTicks,float rotationX,float rotationZ,float rotationYZ,float rotationXY,float rotationXZ) {int j = this.getBrightnessForRender(partialTicks);
		int k = j >> 16 & 65535;
		int l = j & 65535;

		float pX = (float) ((this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX));
		float pY = (float) ((this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY));
		float pZ = (float) ((this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ));

		buffer.pos((double)(pX - rotationX * scale - rotationXY * scale), (double)(pY - rotationZ * scale), (double)(pZ - rotationYZ * scale - rotationXZ * scale)).tex(particleTexture.getMaxU(), particleTexture.getMaxV()).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(k, l).endVertex();
		buffer.pos((double)(pX - rotationX * scale + rotationXY * scale), (double)(pY + rotationZ * scale), (double)(pZ - rotationYZ * scale + rotationXZ * scale)).tex(particleTexture.getMaxU(), particleTexture.getMinV()).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(k, l).endVertex();
		buffer.pos((double)(pX + rotationX * scale + rotationXY * scale), (double)(pY + rotationZ * scale), (double)(pZ + rotationYZ * scale + rotationXZ * scale)).tex(particleTexture.getMinU(), particleTexture.getMinV()).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(k, l).endVertex();
		buffer.pos((double)(pX + rotationX * scale - rotationXY * scale), (double)(pY - rotationZ * scale), (double)(pZ + rotationYZ * scale - rotationXZ * scale)).tex(particleTexture.getMinU(), particleTexture.getMaxV()).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(k, l).endVertex();
	}

	@Override
	public int getBrightnessForRender(float p_189214_1_) {
		return 240;
	}
}
