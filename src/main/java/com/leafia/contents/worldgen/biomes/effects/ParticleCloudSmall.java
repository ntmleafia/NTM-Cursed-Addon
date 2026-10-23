// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.contents.worldgen.biomes.effects;

import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/** Vanilla cloud motion with an addon-sized billboard. */
public class ParticleCloudSmall extends ParticleCloud {
    private final float sizeFactor;

    public ParticleCloudSmall(World world, double x, double y, double z) {
        this(world, x, y, z, 0.75F);
    }

    public ParticleCloudSmall(World world, double x, double y, double z, float scale) {
        this(world, x, y, z, 0, 0, 0, scale);
    }

    public ParticleCloudSmall(World world, double x, double y, double z,
                              double motionX, double motionY, double motionZ, float scale) {
        super(world, x, y, z, motionX, motionY, motionZ);
        sizeFactor = scale / (0.75F * 2.5F);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity camera, float partialTicks,
                               float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, camera, partialTicks, rotationX * sizeFactor, rotationZ * sizeFactor,
                rotationYZ * sizeFactor, rotationXY * sizeFactor, rotationXZ * sizeFactor);
    }
}
