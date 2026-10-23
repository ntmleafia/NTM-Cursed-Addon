// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.particles;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/** Shared spawn and motion parameters for the addon's flame and molten particles. */
public abstract class ReactiveFireParticle extends AddonBillboardParticle {
    private final EnumParticleTypes trail;

    protected ReactiveFireParticle(World world, double x, double y, double z,
                                   ResourceLocation texture, EnumParticleTypes trail) {
        super(world, x, y, z, texture, false);
        this.trail = trail;
        posX += jitter();
        posY += jitter();
        posZ += jitter();
        if (trail == null) {
            particleScale *= 0.08F;
            particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
            canCollide = false;
        } else {
            motionX = (double) (rand.nextFloat() * 0.4F - 0.2F) / 2D;
            motionY = (double) (rand.nextFloat() * 0.4F + 0.05F) / 2D;
            motionZ = (double) (rand.nextFloat() * 0.4F - 0.2F) / 2D;
            particleScale = (particleScale * rand.nextFloat() * 2.0F + 0.2F) * 0.08F;
            particleMaxAge = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
            canCollide = true;
        }
    }

    private float jitter() {
        return (rand.nextFloat() - rand.nextFloat()) * 0.05F;
    }

    @Override
    protected float renderSize(float partialTicks) {
        var life = ((float) particleAge + partialTicks) / (float) particleMaxAge;
        return particleScale * (1.0F - life * life * 0.5F);
    }

    @Override
    public void onUpdate() {
        if (trail == null) {
            super.onUpdate();
            return;
        }
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        var expired = particleAge >= particleMaxAge;
        particleAge++;
        if (expired)
            setExpired();
        var elapsed = (float) particleAge / (float) particleMaxAge;
        if (rand.nextFloat() > elapsed)
            world.spawnParticle(trail, posX, posY, posZ, motionX, motionY, motionZ);
        motionY -= 0.03D;
        move(motionX, motionY, motionZ);
        motionX *= 0.9990000128746033D;
        motionY *= 0.9990000128746033D;
        motionZ *= 0.9990000128746033D;
        if (onGround) {
            motionX *= 0.699999988079071D;
            motionZ *= 0.699999988079071D;
        }
    }
}
