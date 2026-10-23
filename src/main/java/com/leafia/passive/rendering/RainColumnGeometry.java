// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.passive.rendering;

import net.minecraft.client.renderer.BufferBuilder;

final class RainColumnGeometry {
    private RainColumnGeometry() {}

    static long seed(int x, int z) {
        // Retain the precipitation pattern's signed 32-bit wraparound before widening.
        return x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761;
    }

    static double falloutMultiplier(double sourceX, double sourceY, double sourceZ,
                                    double radius, int x, int z) {
        var dx = sourceX - (x + 0.5);
        var dz = sourceZ - (z + 0.5);
        // Vec3d.distanceTo widens MathHelper.sqrt's float-rounded result to double.
        var distance = (double) (float) Math.sqrt(dx * dx + sourceY * sourceY + dz * dz);
        var multiplier = Math.min(Math.pow((1 - distance / radius) * 2, 0.5), 1);
        // The square root is undefined beyond the field; those columns have no opacity.
        return Double.isNaN(multiplier) ? 0 : multiplier;
    }

    static float radialFade(double cameraX, double cameraZ, int x, int z, int radius) {
        var dx = x + 0.5F - cameraX;
        var dz = z + 0.5F - cameraZ;
        var distance = (float) Math.sqrt(dx * dx + dz * dz) / radius;
        var fade = (1 - distance * distance) * 0.3F + 0.5F;
        // Far-coordinate float rounding can otherwise wrap a negative packed alpha.
        return fade < 0 ? 0 : fade;
    }

    static void emit(BufferBuilder buffer, int x, int z, int lower, int upper,
                     float halfX, float halfZ, float alpha, float uOffset,
                     float scroll, float drift, double bloodScroll, boolean blood) {
        for (var corner = 0; corner < 4; corner++) {
            var right = corner == 1 || corner == 2;
            var y = corner < 2 ? lower : upper;
            var px = (right ? x + halfX : x - halfX) + 0.5D;
            var pz = (right ? z + halfZ : z - halfZ) + 0.5D;
            var u = (right ? 1.0F : 0.0F) + uOffset;
            double v = blood ? y / 4.0F - bloodScroll : y / 4.0F + scroll + drift;
            buffer.pos(px, y, pz).tex(u, v).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        }
    }
}
