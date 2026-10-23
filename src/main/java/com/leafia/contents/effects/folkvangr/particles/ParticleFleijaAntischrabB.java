// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.effects.folkvangr.particles;

import com.leafia.dev.particles.AddonBillboardParticle;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleFleijaAntischrabB extends AddonBillboardParticle {

	private static final ResourceLocation texture = new ResourceLocation("hbm" + ":textures/particle/particle_base.png");

	public ParticleFleijaAntischrabB(World world,double x,double y,double z,float mx,float mz) {
		super(world,x,y,z, texture, true);
		this.particleScale = 1;
		this.particleRed = 1;
		this.particleGreen = 0.149f;
		this.particleBlue = 0;
		this.canCollide = false;
		this.motionX = mx;
		this.motionY = 0;
		this.motionZ = mz;
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
		this.motionX *= rand.nextFloat()*0.1+0.9;
		this.motionY *= rand.nextFloat()*0.1+0.9;
		this.motionZ *= rand.nextFloat()*0.1+0.9;
		this.move(this.motionX, this.motionY, this.motionZ);
		if (this.particleAge >= 10) {
			this.particleAlpha = (float)Math.pow(1-Math.min((this.particleAge-10)/40f,1),2);
		}

		if(++this.particleAge >= 50) {
			this.setExpired();
		}
	}
}
