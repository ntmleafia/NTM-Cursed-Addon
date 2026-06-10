package com.leafia.unsorted;

import com.hbm.render.NTMRenderHelper;
import com.hbm.render.misc.BeamPronter;
import com.hbm.render.misc.BeamPronter.EnumBeamType;
import com.hbm.render.misc.BeamPronter.EnumWaveType;
import com.leafia.overwrite_contents.interfaces.IMixinTileEntityCore.DFCShock;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ParticleDFCShockParticleEditionTM extends Particle {
	final List<Vec3d> shock;
	public ParticleDFCShockParticleEditionTM(World worldIn,double posXIn,double posYIn,double posZIn,List<Vec3d> shock) {
		super(worldIn,posXIn,posYIn,posZIn);
		this.shock = shock;
		particleMaxAge = 4;
	}
	@Override
	public int getFXLayer() {
		return 3;
	}
	@Override
	public void renderParticle(BufferBuilder buffer,Entity entityIn,float partialTicks,float rotationX,float rotationZ,float rotationYZ,float rotationXY,float rotationXZ) {
		Vec3d lastPos = null;
		NTMRenderHelper.resetParticleInterpPos(entityIn, partialTicks);
		LeafiaGls.pushMatrix();
		float pX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float pY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float pZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
		LeafiaGls.translate(pX-this.posX,pY-this.posY,pZ-this.posZ);
		if (world.rand.nextInt(4) >= 1) {
			for (Vec3d pos : shock) {
				if (lastPos != null) {
					if (pos.distanceTo(lastPos) < 0.1) continue;
					LeafiaGls.pushMatrix();
					LeafiaGls.translate(lastPos);
					Vec3d vec3 = pos.subtract(lastPos);
					BeamPronter.prontBeam(
							vec3,
							EnumWaveType.STRAIGHT,EnumBeamType.SOLID,
							0x5B1D00,0x7F7F7F,
							0,1,0,2,0.25f
					);
					LeafiaGls.popMatrix();
				}
				lastPos = pos;
			}
		}
		LeafiaGls.popMatrix();
	}
}
