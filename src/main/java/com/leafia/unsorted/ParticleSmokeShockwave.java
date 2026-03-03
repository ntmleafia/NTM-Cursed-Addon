package com.leafia.unsorted;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSmokeShockwave extends Particle {
	public double increment = 4;
	public float particleScale = 5;
	public double maxRadius = 200;
	public int particleLifetime = 12;
	public ParticleSmokeShockwave(World worldIn,double x,double y,double z) {
		super(worldIn,x,y,z);
	}
	public void circleParticles(double radius,int amount,float scale,int lifetime,float alpha) {
		double angleIncrement = Math.PI*2/amount;
		for (int i = 0; i < amount; i++) {
			double angle = angleIncrement*i;
			double oX = Math.cos(angle)*radius;
			double oZ = Math.sin(angle)*radius;
			int y = world.getHeight((int)(posX+oX),(int)(posZ+oZ));
			ParticleBasicSmoke smoke = new ParticleBasicSmoke(
					world,
					posX+oX+world.rand.nextGaussian()*0.2,
					y+world.rand.nextGaussian()*0.2,
					posZ+oZ+world.rand.nextGaussian()*0.2
			);
			smoke.scale = scale;
			smoke.maxAge = lifetime;
			smoke.alphaMultiplier = alpha;
			Minecraft.getMinecraft().effectRenderer.addEffect(smoke);
		}
	}
	public int getAmountForRadius(double radius) {
		return 8+(int)(Math.pow(radius,0.4)*20);
	}
	@Override
	public void onUpdate() {
		double radius = particleAge*increment;
		double perc = radius/maxRadius;
		float sc = (float)Math.pow(1-perc,0.5);
		circleParticles(radius,getAmountForRadius(radius),particleScale*(0.3f+sc*0.7f),particleLifetime,sc*0.8f);
		if (radius >= maxRadius)
			setExpired();
		particleAge++;
	}
}
