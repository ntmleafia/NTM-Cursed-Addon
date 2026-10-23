// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.effects.folkvangr.particles;

import com.leafia.dev.particles.AddonBillboardParticle;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleFleijaAntischrabA extends AddonBillboardParticle {

	private static final ResourceLocation texture = new ResourceLocation("hbm" + ":textures/particle/particle_base.png");
	int angle;
	double originX;
	double originY;
	double originZ;
	double position = 1;
	void updatePosition() {
		position = position + 2/Math.pow(position,0.35);
		angle = angle + (int)(10/Math.pow(particleAge+1,0.5))*2;
		double theta = angle/180d*Math.PI;
		this.posX = originX+Math.cos(theta)*position*4;
		this.posZ = originZ+Math.sin(theta)*position*4;
		this.posY = world.getHeight((int)(this.posX),(int)(this.posZ));
	}
	public ParticleFleijaAntischrabA(World world,double x,double y,double z,int angle) {
		super(world,x,y,z, texture, true);
		this.originX = x;
		this.originY = y;
		this.originZ = z;
		this.particleScale = 2;
		this.particleRed = 1;
		this.particleGreen = 0.149f;
		this.particleBlue = 0;
		this.angle = angle;
		this.canCollide = false;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		updatePosition();
		//this.particleAngle = rand.nextFloat()*360;   not worth the effort
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
	}

	@Override
	public void onUpdate(){
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		//this.move(this.motionX, this.motionY, this.motionZ);
		updatePosition();
		if (this.particleAge >= 10) {
			this.particleAlpha = (float)Math.pow(1-Math.min((this.particleAge-10)/40f,1),2);
		}

		if(++this.particleAge >= 50) {
			this.setExpired();
		}
	}
}
