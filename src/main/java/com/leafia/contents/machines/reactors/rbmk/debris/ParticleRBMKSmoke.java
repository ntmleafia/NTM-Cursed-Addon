// SPDX-FileCopyrightText: 2020 Drillgon200 <drillgon200@gmail.com>
// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2025 mn58835 <mn58835@tutanota.com>
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.machines.reactors.rbmk.debris;

import com.hbm.main.client.NTMClientRegistry;
import com.hbm.particle.ParticleCoolingTower;
import com.hbm.render.util.NTMBufferBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class ParticleRBMKSmoke extends ParticleCoolingTower {
	private float baseScale = 1.0F;
	private float maxScale = 1.0F;
	public ParticleRBMKSmoke(World world,double x,double y,double z) {
		super(world,x,y,z);
	}

	public void setBaseScale(float f) {
		this.baseScale = f;
	}

	public void setMaxScale(float f) {
		this.maxScale = f;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		particleScale = baseScale+(maxScale-baseScale)*(float)Math.pow((float)particleAge/particleMaxAge,1.5);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(BufferBuilder buffer,Entity entityIn,float partialTicks,float rotationX,float rotationZ,float rotationYZ,float rotationXY,float rotationXZ) {
		GlStateManager.depthMask(true);
		this.particleTexture = NTMClientRegistry.contrail;
		float f = (float) this.particleTextureIndexX / 16.0F;
		float f1 = f + 0.0624375F;
		float f2 = (float) this.particleTextureIndexY / 16.0F;
		float f3 = f2 + 0.0624375F;
		float f4 = 0.75F * this.particleScale;

		if (this.particleTexture != null) {
			f = this.particleTexture.getMinU();
			f1 = this.particleTexture.getMaxU();
			f2 = this.particleTexture.getMinV();
			f3 = this.particleTexture.getMaxV();
		}

		Random urandom = new Random(this.hashCode());
		NTMBufferBuilder fastBuffer = (NTMBufferBuilder) buffer;
		for (int ii = 0; ii < 6; ii++) {

			float f5 = (float) ((this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX) + urandom.nextGaussian() * 0.5);
			float f6 = (float) ((this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY) + urandom.nextGaussian() * 0.5);
			float f7 = (float) ((this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ) + urandom.nextGaussian() * 0.5);

			this.particleRed = this.particleGreen = this.particleBlue = urandom.nextFloat() * 0.4F + 0F;

			int i = this.getBrightnessForRender(partialTicks);
			int j = i >> 16 & 65535;
			int k = i & 65535;
			Vec3d[] avec3d = new Vec3d[] { new Vec3d((double) (-rotationX * f4 - rotationXY * f4), (double) (-rotationZ * f4), (double) (-rotationYZ * f4 - rotationXZ * f4)), new Vec3d((double) (-rotationX * f4 + rotationXY * f4), (double) (rotationZ * f4), (double) (-rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double) (rotationX * f4 + rotationXY * f4), (double) (rotationZ * f4), (double) (rotationYZ * f4 + rotationXZ * f4)), new Vec3d((double) (rotationX * f4 - rotationXY * f4), (double) (-rotationZ * f4), (double) (rotationYZ * f4 - rotationXZ * f4)) };

			if (this.particleAngle != 0.0F) {
				float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
				float f9 = MathHelper.cos(f8 * 0.5F);
				float f10 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.x;
				float f11 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.y;
				float f12 = MathHelper.sin(f8 * 0.5F) * (float) cameraViewDir.z;
				Vec3d vec3d = new Vec3d((double) f10, (double) f11, (double) f12);

				for (int l = 0; l < 4; ++l) {
					avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double) (f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double) (2.0F * f9)));
				}
			}

			int packedColor = NTMBufferBuilder.packColor(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
			int packedLightmap = NTMBufferBuilder.packLightmap(j, k);
			fastBuffer.appendParticlePositionTexColorLmap(f5 + (float) avec3d[0].x, f6 + (float) avec3d[0].y, f7 + (float) avec3d[0].z, f1, f3, packedColor, packedLightmap);
			fastBuffer.appendParticlePositionTexColorLmap(f5 + (float) avec3d[1].x, f6 + (float) avec3d[1].y, f7 + (float) avec3d[1].z, f1, f2, packedColor, packedLightmap);
			fastBuffer.appendParticlePositionTexColorLmap(f5 + (float) avec3d[2].x, f6 + (float) avec3d[2].y, f7 + (float) avec3d[2].z, f, f2, packedColor, packedLightmap);
			fastBuffer.appendParticlePositionTexColorLmap(f5 + (float) avec3d[3].x, f6 + (float) avec3d[3].y, f7 + (float) avec3d[3].z, f, f3, packedColor, packedLightmap);

		}
		GlStateManager.depthMask(false);
	}
}
