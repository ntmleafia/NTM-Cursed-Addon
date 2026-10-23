// SPDX-FileCopyrightText: 2026 Leafia
// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.unsorted.explosion_vnt;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockAllocator;
import com.leafia.dev.math.CubeSurfaceRays;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;

public class BlockAllocatorSteamExplosion implements IBlockAllocator {
    private static final float STEP = 0.3F;
    protected int resolution;
    private int cachedResolution = Integer.MIN_VALUE;
    private double[] directions = new double[0];

    public BlockAllocatorSteamExplosion() {
        this(16);
    }

    public BlockAllocatorSteamExplosion(int resolution) {
        this.resolution = resolution;
    }

    @Override
    public HashSet<BlockPos> allocate(ExplosionVNT explosion, World world,
            double x, double y, double z, float size) {
        if (cachedResolution != resolution) {
            directions = CubeSurfaceRays.create(resolution);
            cachedResolution = resolution;
        }
        var affected = new HashSet<BlockPos>();
        for (int ray = 0; ray < directions.length; ray += 3) {
            double px = x;
            double py = y;
            double pz = z;
            float energy = size * (0.7F + world.rand.nextFloat() * 0.6F);
            while (energy > 0) {
                var pos = new BlockPos(px, py, pz);
                var state = world.getBlockState(pos);
                var block = state.getBlock();
                var material = block.getMaterial(state);
                if (material != Material.AIR && material != Material.WATER && material != ModBlocks.fluidcorium) {
                    float resistance = explosion.exploder == null
                            ? block.getExplosionResistance(world, pos, null, explosion.compat)
                            : explosion.exploder.getExplosionResistance(explosion.compat, world, pos, state);
                    energy -= (resistance / 10 + 0.03F) * STEP;
                }
                if (energy > 0 && (explosion.exploder == null
                        || explosion.exploder.canExplosionDestroyBlock(explosion.compat, world, pos, state, energy))) {
                    affected.add(pos);
                }
                px += directions[ray] * (double) STEP;
                py += directions[ray + 1] * (double) STEP;
                pz += directions[ray + 2] * (double) STEP;
                energy -= STEP * 2.75F;
            }
        }
        return affected;
    }
}
