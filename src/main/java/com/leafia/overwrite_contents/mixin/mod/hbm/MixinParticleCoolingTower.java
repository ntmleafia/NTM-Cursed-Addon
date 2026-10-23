// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.overwrite_contents.mixin.mod.hbm;

import com.hbm.particle.ParticleCoolingTower;
import com.leafia.passive.Wind;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ParticleCoolingTower.class)
public class MixinParticleCoolingTower extends Particle {
	protected MixinParticleCoolingTower(World worldIn,double posXIn,double posYIn,double posZIn) {
		super(worldIn,posXIn,posYIn,posZIn);
	}
	@Redirect(method = "onUpdate",at = @At(value = "FIELD", target = "Lcom/hbm/particle/ParticleCoolingTower;windDir:Z",remap = false),require = 1)
	boolean leafia$onOnUpdate(ParticleCoolingTower instance) {
		double multiplier = Wind.dimensionWindMultiplier.getOrDefault(world.provider.getDimension(),0d);
		if (multiplier > 0) {
			double acceleration = 0.0025*Math.pow(Wind.power/100,0.65)*multiplier;
			/*double yoffset = Math.max(0,posY-60);
			double mul = yoffset/40;
			acceleration *= Math.min(Math.pow(mul,2),1);*/
			motionZ += Math.cos(Wind.angle/180*Math.PI)*acceleration;
			motionX -= Math.sin(Wind.angle/180*Math.PI)*acceleration;
		}
		return false;
	}
}
