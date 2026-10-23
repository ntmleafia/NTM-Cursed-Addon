// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.passive.rendering;

import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RainColumnGeometryTest {
    @Test
    void densityRemainsFiniteAtAndBeyondTheFieldBoundary() {
        assertEquals(1, density(0));
        assertEquals(1, density(5));
        assertEquals(Math.pow(0.5, 0.5), density(7.5));
        assertTrue(density(Math.nextDown(10.0F)) > 0);
        assertEquals(0, density(Math.nextDown(10.0)));
        assertEquals(0, density(10));
        assertEquals(0, density(Math.nextUp(10.0)));
        assertEquals(0, density(Math.nextUp(10.0F)));
        assertEquals(0, density(20));
        assertEquals(0, RainColumnGeometry.falloutMultiplier(0.5, 0, 0.5, 0, 0, 0));
    }

    @Test
    void densityRetainsTheResolvedVectorDistancePrecision() {
        var source = new Vec3d(17.125, 0, -23.75);
        var column = new Vec3d(0.5, 0, 0.5);
        var expected = Math.min(Math.pow((1 - source.distanceTo(column) / 40) * 2, 0.5), 1);
        assertEquals(expected, RainColumnGeometry.falloutMultiplier(source.x, source.y, source.z, 40, 0, 0));
    }

    @Test
    void negativeWorldBorderFadeCannotWrapVertexAlpha() {
        assertEquals(0, RainColumnGeometry.radialFade(29999999.999, 29999999.999,
                29999994, 29999994, 5));
        assertEquals(0.8F, RainColumnGeometry.radialFade(0.5, 0.5, 0, 0, 5));
        assertEquals(0.5F, RainColumnGeometry.radialFade(0.5, 0.5, 5, 0, 5));
    }

    private static double density(double distance) {
        return RainColumnGeometry.falloutMultiplier(0.5 - distance, 0, 0.5, 10, 0, 0);
    }
}
