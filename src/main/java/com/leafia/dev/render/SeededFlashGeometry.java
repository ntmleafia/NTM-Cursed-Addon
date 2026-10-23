// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.render;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

/** Seeded ray orientations and dimensions, independent of the active GL context. */
final class SeededFlashGeometry {
    static final int COUNT = 300;
    static final int STRIDE = 11;
    static final float[] RAYS = create();

    private SeededFlashGeometry() {}

    private static float[] create() {
        var rays = new float[COUNT * STRIDE];
        var random = new Random(432);
        var orientation = new Matrix4f();
        var axes = new Vector3f[]{new Vector3f(1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, 0, 1)};
        var order = new int[]{0, 1, 2, 0, 1};
        for (var ray = 0; ray < COUNT; ray++) {
            for (var axis : order) {
                Matrix4f.rotate((float) Math.toRadians(random.nextFloat() * 360F), axes[axis], orientation, orientation);
            }
            var offset = ray * STRIDE;
            rays[offset] = orientation.m00;
            rays[offset + 1] = orientation.m01;
            rays[offset + 2] = orientation.m02;
            rays[offset + 3] = orientation.m10;
            rays[offset + 4] = orientation.m11;
            rays[offset + 5] = orientation.m12;
            rays[offset + 6] = orientation.m20;
            rays[offset + 7] = orientation.m21;
            rays[offset + 8] = orientation.m22;
            rays[offset + 9] = random.nextFloat() * 20F + 5F + 10F;
            rays[offset + 10] = random.nextFloat() * 2F + 1F + 2F;
        }
        return rays;
    }
}
