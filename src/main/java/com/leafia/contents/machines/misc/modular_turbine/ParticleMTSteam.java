// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.misc.modular_turbine;

import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class ParticleMTSteam extends ParticleCloud {
	int travelDistance;
	double distanceTravelled = 0;
	int actualParticleAge = 0;
	public ParticleMTSteam(World world,double x,double y,double z,double mx,double my,double mz,int travelDistance) {
		super(world,x,y,z,mx,my,mz);
		this.travelDistance = travelDistance;
		canCollide = false;
		particleMaxAge = 200;
		motionX = mx;
		motionY = my;
		motionZ = mz;
	}
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		distanceTravelled += Math.abs(motionX)+Math.abs(motionY)+Math.abs(motionZ);
		double progress = distanceTravelled/travelDistance;
		actualParticleAge++;
		if (progress >= 1 || actualParticleAge > particleMaxAge)
			setExpired();
		this.setParticleTextureIndex(7-(int)Math.min(progress*8,7));
		// trick the renderer so the scale becomes the way we want
		particleAge = (int)(distanceTravelled*particleMaxAge/32);
		this.move(this.motionX,this.motionY,this.motionZ);
	}
}
