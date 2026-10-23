// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.math;

/** Unit vectors through a cube's surface, ordered by increasing x, y and z. */
public final class CubeSurfaceRays {
    private CubeSurfaceRays() {}

    public static double[] create(int resolution) {
        if (resolution <= 0) return new double[0];
        long count = resolution == 1 ? 1 : 6L * resolution * resolution - 12L * resolution + 8;
        var directions = new double[Math.toIntExact(count * 3)];
        int last = resolution - 1;
        float divisor = resolution - 1.0F;
        int cursor = 0;
        for (int x = 0; x <= last; x++) {
            double dx = x / divisor * 2.0F - 1.0F;
            for (int y = 0; y <= last; y++) {
                double dy = y / divisor * 2.0F - 1.0F;
                int stride = x == 0 || x == last || y == 0 || y == last ? 1 : last;
                for (int z = 0; z <= last; z += stride) {
                    double dz = z / divisor * 2.0F - 1.0F;
                    double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    directions[cursor++] = dx / length;
                    directions[cursor++] = dy / length;
                    directions[cursor++] = dz / length;
                }
            }
        }
        return directions;
    }
}
