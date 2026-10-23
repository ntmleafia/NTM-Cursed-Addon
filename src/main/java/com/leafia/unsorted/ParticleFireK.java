// SPDX-FileCopyrightText: 2025 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted;

import com.leafia.AddonBase;
import com.leafia.dev.particles.ReactiveFireParticle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleFireK extends ReactiveFireParticle {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AddonBase.MODID,
            "textures/particle/leafia/particle_fire_k.png");

    public ParticleFireK(World world, double x, double y, double z) {
        super(world, x, y, z, TEXTURE, null);
    }
}
