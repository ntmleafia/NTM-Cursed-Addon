// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted;

import com.hbm.util.RenderUtil;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import com.leafia.dev.particles.ParticleQuad;
import com.leafia.dev.particles.ParticleRenderState;
import net.minecraft.world.World;

public class ParticleArcWelder extends ParticleCloud {
	public float scale = 0.5f;
	public float increaseAmt = 0.05f;
	public ParticleArcWelder(World worldIn,double xCoordIn,double yCoordIn,double zCoordIn) {
		super(worldIn,xCoordIn,yCoordIn,zCoordIn,0,0,0);
		particleMaxAge = 40;
		canCollide = false;
		motionX = worldIn.rand.nextGaussian()/20*0.25;
		motionZ = worldIn.rand.nextGaussian()/20*0.25;
		particleAngle = worldIn.rand.nextFloat()*360;
		prevParticleAngle = particleAngle;
		updateColor();
	}
	void updateColor() {
		particleAlpha = 0.5f-0.5f*(float)Math.pow((double)particleAge/particleMaxAge,2);
		particleRed = 0.8f*particleAlpha;
		particleGreen = 0.925f*particleAlpha;
		particleBlue = 0.95f*particleAlpha;
	}
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		motionY += 0.3/20;
		motionY *= 0.8;
		increaseAmt += 0.005f;
		scale += increaseAmt;
		this.setParticleTextureIndex(7 - this.particleAge * 4 / this.particleMaxAge);
		this.move(this.motionX,this.motionY,this.motionZ);
		updateColor();
		particleAge++;
		if (particleAge >= particleMaxAge)
			this.setExpired();
	}
	@Override
	public void renderParticle(BufferBuilder buffer,Entity entityIn,float partialTicks,float rotationX,float rotationZ,float rotationYZ,float rotationXY,float rotationXZ) {
        var tessellator = Tessellator.getInstance();
        var blend = RenderUtil.isBlendEnabled();
        var factors = ParticleRenderState.blendFactors();
        tessellator.draw();
        var building = false;
        try {
            var growth = ((float) particleAge + partialTicks) / (float) particleMaxAge * 32.0F;
            particleScale = scale * MathHelper.clamp(growth, 0.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_COLOR, DestFactor.ONE);
            buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            building = true;
            superSuperRenderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
            building = false;
            tessellator.draw();
        } finally {
            if (building) buffer.finishDrawing();
            ParticleRenderState.restoreBlend(blend, factors);
            buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        }
	}

    public void superSuperRenderParticle(BufferBuilder buffer, Entity camera, float partialTicks,
                                         float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        var minU = (float) particleTextureIndexX / 16.0F;
        var maxU = minU + 0.0624375F;
        var minV = (float) particleTextureIndexY / 16.0F;
        var maxV = minV + 0.0624375F;
        if (particleTexture != null) {
            minU = particleTexture.getMinU();
            maxU = particleTexture.getMaxU();
            minV = particleTexture.getMinV();
            maxV = particleTexture.getMaxV();
        }
        float w = 1;
        double qx = 0, qy = 0, qz = 0;
        if (particleAngle != 0) {
            var halfAngle = (particleAngle + (particleAngle - prevParticleAngle) * partialTicks) * 0.5F;
            w = MathHelper.cos(halfAngle);
            var sine = MathHelper.sin(halfAngle);
            qx = sine * (float) cameraViewDir.x;
            qy = sine * (float) cameraViewDir.y;
            qz = sine * (float) cameraViewDir.z;
        }
        ParticleQuad.emitRotated(buffer,
                (float) (prevPosX + (posX - prevPosX) * (double) partialTicks - interpPosX),
                (float) (prevPosY + (posY - prevPosY) * (double) partialTicks - interpPosY),
                (float) (prevPosZ + (posZ - prevPosZ) * (double) partialTicks - interpPosZ),
                rotationX, rotationYZ, rotationZ, rotationXY, rotationXZ, 0.1F * particleScale,
                minU, maxU, minV, maxV, particleRed, particleGreen, particleBlue, particleAlpha,
                getBrightnessForRender(partialTicks), w, qx, qy, qz);
    }
}
