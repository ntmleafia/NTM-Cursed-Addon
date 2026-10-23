// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted;

import com.leafia.AddonBase;
import com.leafia.dev.particles.ReactiveFireParticle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleFireLavaK extends ReactiveFireParticle {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AddonBase.MODID,
            "textures/particle/leafia/particle_lava_k.png");

    public ParticleFireLavaK(World world, double x, double y, double z) {
        super(world, x, y, z, TEXTURE, EnumParticleTypes.CLOUD);
    }
}
