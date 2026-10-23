// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.effects.folkvangr.particles;

import com.leafia.dev.particles.AddonBillboardParticle;
import com.leafia.overwrite_contents.interfaces.IMixinEntityCloudFleija;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleFleijaCloud extends AddonBillboardParticle {

	private static final ResourceLocation texture = new ResourceLocation("hbm" + ":textures/particle/particle_base.png");
	IMixinEntityCloudFleija cloud;
	Vec3d lookVector;
	Vec3d rightVector;
	double mySize = -1;
	double mySpeed = 1;
	void updatePosition() {
		double s = cloud.getScale()*1.4;
		this.posX = ((Entity)cloud).posX+lookVector.x*s+rightVector.x*s;
		this.posY = ((Entity)cloud).posY+lookVector.y*s+rightVector.y*s;
		this.posZ = ((Entity)cloud).posZ+lookVector.z*s+rightVector.z*s;
	}
	public ParticleFleijaCloud(World world,Vec3d forward,Vec3d right,IMixinEntityCloudFleija cloud) {
		super(world, ((Entity)cloud).posX, ((Entity)cloud).posY, ((Entity)cloud).posZ, texture, true);
		this.particleScale = 3;
		this.particleRed = this.particleGreen = this.particleBlue = 0.9F + world.rand.nextFloat() * 0.05F;
		this.canCollide = false;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.cloud = cloud;
		this.lookVector = forward;
		this.rightVector = right;
		updatePosition();
		//this.particleAngle = rand.nextFloat()*360;   not worth the effort
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.particleAlpha = 0;
	}

	@Override
	public void onUpdate() {
		if (((Entity)cloud).isDead) {
			// again, not worth the effort      world.spawnParticle(new ParticleFleijaVacuum(world,posX,posY,posZ,3,rand.nextFloat()*0.1f+0.1f,new VacuumInstance()));
			this.setExpired();
			return;
		}
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		//this.move(this.motionX, this.motionY, this.motionZ);
		updatePosition();
		if (this.particleAge <= 10) {
			this.particleAlpha = this.particleAge/10f;
		}
		if (this.particleAge >= 20*2) {
			this.particleAlpha = (float)Math.pow(1-Math.min((this.particleAge-20*2)/20f,1),1.5);
		}

		if(++this.particleAge >= 20*3) {
			this.setExpired();
		}
	}
}
