// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted;

import com.hbm.render.NTMRenderHelper;
import com.hbm.util.RenderUtil;
import com.leafia.AddonBase;
import com.leafia.dev.render.SeededFlashMesh;
import com.leafia.dev.particles.ParticleRenderState;
import com.leafia.transformer.LeafiaGls;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleFlash extends Particle {
    float maxScale;
    int ticksIn;
    int ticksOut;

    public ParticleFlash(World world, double x, double y, double z, float maxScale, int ticksIn, int ticksOut) {
        super(world, x, y, z);
        this.maxScale = maxScale;
        this.ticksIn = ticksIn;
        this.ticksOut = ticksOut;
        particleMaxAge = ticksIn + ticksOut;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity camera, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        NTMRenderHelper.resetParticleInterpPos(camera, partialTicks);
        var elapsed = particleAge + partialTicks;
        var envelope = elapsed < ticksIn ? elapsed / (float) ticksIn
                : 1 - (elapsed - ticksIn) / (float) ticksOut;
        var textureUnit = RenderUtil.getActiveTextureUnitIndex();
        var textureName = GlStateManager.textureState[textureUnit].textureName;
        LeafiaGls.pushMatrix();
        try {
            LeafiaGls.translate(posX - interpPosX, posY - interpPosY, posZ - interpPosZ);
            NTMRenderHelper.bindTexture(AddonBase.solid_e);
            LeafiaGls.scale(0.05 * maxScale * Math.pow(envelope, 0.75));
            SeededFlashMesh.draw(1, 20, 1, 1, 1, 1);
        } finally {
            LeafiaGls.popMatrix();
            ParticleRenderState.restoreTexture(textureUnit, textureName);
        }
    }
}
