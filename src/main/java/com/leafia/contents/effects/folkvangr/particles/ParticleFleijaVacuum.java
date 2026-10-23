// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.effects.folkvangr.particles;

import com.leafia.dev.particles.AddonBillboardParticle;
import com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr.VacuumInstance;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import static com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr.vacuumEase;
import static com.leafia.contents.effects.folkvangr.EntityNukeFolkvangr.vacuumForceMultiplier;

public class ParticleFleijaVacuum extends AddonBillboardParticle {

	private static final ResourceLocation texture = new ResourceLocation("hbm" + ":textures/particle/particle_base.png");
	private float baseScale;
	VacuumInstance instance;
	double startDistance;
	float baseAlpha;

	public ParticleFleijaVacuum(World worldIn,double posXIn,double posYIn,double posZIn,float scale,float alpha,VacuumInstance instance){
		super(worldIn, posXIn, posYIn, posZIn, texture, false);
		this.particleScale = scale;
		this.baseScale = scale;
		this.particleRed = this.particleGreen = this.particleBlue = 0.9F + world.rand.nextFloat() * 0.05F;
		this.canCollide = false;
		this.particleAlpha = alpha;
		this.baseAlpha = alpha;
		this.instance = instance;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.startDistance = distanceToCenter();
	}
	
	@Override
	public void onUpdate(){
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		double distance = distanceToCenter();
		double ratio = Math.max(0,(distance-instance.vacuumStart/2)/(startDistance-instance.vacuumStart/2));

		this.particleScale = baseScale * (float)Math.pow(ratio,0.1);
		this.particleAlpha = baseAlpha*(float)Math.pow(1-ratio,0.5);

		double force = vacuumEase.get((distance-instance.vacuumStart)/(instance.vacuumEnd-instance.vacuumStart),1,0,true);
		double pullX = 0, pullY = 0, pullZ = 0;
		if (!(distance < 1.0E-4D)) {
			pullX = (instance.pos.x-posX)/distance;
			pullY = (instance.pos.y-posY)/distance;
			pullZ = (instance.pos.z-posZ)/distance;
		}
		this.motionX += pullX*instance.vacuumForce*vacuumForceMultiplier*force;
		this.motionY += pullY*instance.vacuumForce*vacuumForceMultiplier*force;
		this.motionZ += pullZ*instance.vacuumForce*vacuumForceMultiplier*force;
		this.move(this.motionX, this.motionY, this.motionZ);

		if(++this.particleAge >= 20*10 || (distance < instance.vacuumStart/2)) {
			this.setExpired();
		}
	}

    private double distanceToCenter() {
        var dx = posX - instance.pos.x;
        var dy = posY - instance.pos.y;
        var dz = posZ - instance.pos.z;
        return MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
