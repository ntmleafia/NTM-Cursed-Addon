// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.particles;

import net.minecraft.client.renderer.BufferBuilder;

/** Camera-facing quads, emitted directly without allocating corner vectors. */
public final class ParticleQuad {
    private ParticleQuad() {
    }

    public static void emit(BufferBuilder buffer, float x, float y, float z,
                            float rightX, float rightZ, float upY, float upX, float upZ, float size,
                            float minU, float maxU, float minV, float maxV,
                            float red, float green, float blue, float alpha, int brightness) {
        var lightU = brightness >>> 16 & 0xffff;
        var lightV = brightness & 0xffff;
        for (var corner = 0; corner < 4; corner++) {
            var right = corner >= 2;
            var top = corner == 1 || corner == 2;
            // Preserve float arithmetic before BufferBuilder stores the positions.
            var px = right ? x + rightX * size : x - rightX * size;
            var pz = right ? z + rightZ * size : z - rightZ * size;
            px = top ? px + upX * size : px - upX * size;
            pz = top ? pz + upZ * size : pz - upZ * size;
            var py = top ? y + upY * size : y - upY * size;
            buffer.pos(px, py, pz).tex(right ? minU : maxU, top ? minV : maxV)
                    .color(red, green, blue, alpha).lightmap(lightU, lightV).endVertex();
        }
    }

    public static void emitRotated(BufferBuilder buffer, float x, float y, float z,
                                   float rightX, float rightZ, float upY, float upX, float upZ, float size,
                                   float minU, float maxU, float minV, float maxV,
                                   float red, float green, float blue, float alpha, int brightness,
                                   float quaternionW, double quaternionX, double quaternionY, double quaternionZ) {
        var lengthSquared = quaternionX * quaternionX + quaternionY * quaternionY + quaternionZ * quaternionZ;
        var diagonal = (double) (quaternionW * quaternionW) - lengthSquared;
        var crossScale = (double) (2.0F * quaternionW);
        var lightU = brightness >>> 16 & 0xffff;
        var lightV = brightness & 0xffff;
        for (var corner = 0; corner < 4; corner++) {
            var right = corner >= 2;
            var top = corner == 1 || corner == 2;
            var localX = right ? rightX * size : -rightX * size;
            var localZ = right ? rightZ * size : -rightZ * size;
            localX = top ? localX + upX * size : localX - upX * size;
            localZ = top ? localZ + upZ * size : localZ - upZ * size;
            double vx = localX;
            double vy = top ? upY * size : -upY * size;
            double vz = localZ;
            var projection = 2.0D * (vx * quaternionX + vy * quaternionY + vz * quaternionZ);
            var px = quaternionX * projection + vx * diagonal
                    + (quaternionY * vz - quaternionZ * vy) * crossScale;
            var py = quaternionY * projection + vy * diagonal
                    + (quaternionZ * vx - quaternionX * vz) * crossScale;
            var pz = quaternionZ * projection + vz * diagonal
                    + (quaternionX * vy - quaternionY * vx) * crossScale;
            buffer.pos((double) x + px, (double) y + py, (double) z + pz)
                    .tex(right ? minU : maxU, top ? minV : maxV)
                    .color(red, green, blue, alpha).lightmap(lightU, lightV).endVertex();
        }
    }
}
