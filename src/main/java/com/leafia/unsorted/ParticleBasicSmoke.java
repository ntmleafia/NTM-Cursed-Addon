// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted;

import com.hbm.main.client.NTMClientRegistry;
import com.leafia.dev.particles.ParticleQuad;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBasicSmoke extends Particle {
    private int age;
    public int maxAge;
    public float scale = 1;
    public float alphaMultiplier = 1;

    public ParticleBasicSmoke(World world, double x, double y, double z) {
        super(world, x, y, z);
        maxAge = 100 + rand.nextInt(40);
        setParticleTexture(NTMClientRegistry.contrail);
        setParticleColor(0.25F, 0.25F, 0.25F);
    }

    public void setParticleColor(float red, float green, float blue) {
        setRBGColorF(red, green, blue);
    }

    public void setMotion(double x, double y, double z) {
        motionX = x;
        motionY = y;
        motionZ = z;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        particleAlpha = (1 - (float) age / (float) maxAge) * alphaMultiplier;
        age++;
        if (age == maxAge)
            setExpired();
        motionX *= 0.7599999785423279D;
        motionY *= 0.7599999785423279D;
        motionZ *= 0.7599999785423279D;
        move(motionX, motionY, motionZ);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public boolean shouldDisableDepth() {
        return true;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 240;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity camera, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        ParticleQuad.emit(buffer,
                (float) (prevPosX + (posX - prevPosX) * (double) partialTicks - interpPosX),
                (float) (prevPosY + (posY - prevPosY) * (double) partialTicks - interpPosY),
                (float) (prevPosZ + (posZ - prevPosZ) * (double) partialTicks - interpPosZ),
                rotationX, rotationYZ, rotationZ, rotationXY, rotationXZ, scale,
                particleTexture.getMinU(), particleTexture.getMaxU(), particleTexture.getMinV(), particleTexture.getMaxV(),
                particleRed, particleGreen, particleBlue, particleAlpha, getBrightnessForRender(partialTicks));
    }
}
