// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.render;

import com.hbm.render.util.NTMBufferBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RendererGeometryTest {
    @Test
    void burstRetainsSeededDimensionsAndCumulativeOrientations() {
        var random = new Random(432);
        var reference = new double[]{1, 0, 0, 0, 1, 0, 0, 0, 1};
        var order = new int[]{0, 1, 2, 0, 1};
        var actual = SeededFlashGeometry.RAYS;
        assertEquals(300 * 11, actual.length);
        for (var ray = 0; ray < 300; ray++) {
            for (var axis : order) {
                var radians = Math.toRadians(random.nextFloat() * 360F);
                reference = rotate(reference, axis, radians);
            }
            var offset = ray * 11;
            for (var column = 0; column < 3; column++) {
                for (var row = 0; row < 3; row++) {
                    assertEquals(reference[row * 3 + column], actual[offset + column * 3 + row], 0.00005,
                            "ray " + ray + ", matrix component " + row + "," + column);
                }
            }
            assertEquals(random.nextFloat() * 20F + 5F + 10F, actual[offset + 9]);
            assertEquals(random.nextFloat() * 2F + 1F + 2F, actual[offset + 10]);
        }
    }

    @Test
    void columnPreservesFaceOrderAndFloatRoundedCoordinates() {
        var captured = new ArrayList<Vertex>();
        var color = 0xAABBCCDD;
        AMSRenderSupport.column(buffer(captured), 0.25, -0.75, -0.125, 0.5, 7, 6, 0.12F, color);
        var radius = (double) 0.12F;
        var expected = new double[][]{
                {0.25 + radius, 7, -0.75 + radius}, {0.25 + radius, 7, -0.75 - radius},
                {-0.125 + radius, 6, 0.5 - radius}, {-0.125 + radius, 6, 0.5 + radius},
                {0.25 - radius, 7, -0.75 + radius}, {0.25 - radius, 7, -0.75 - radius},
                {-0.125 - radius, 6, 0.5 - radius}, {-0.125 - radius, 6, 0.5 + radius},
                {0.25 + radius, 7, -0.75 + radius}, {0.25 - radius, 7, -0.75 + radius},
                {-0.125 - radius, 6, 0.5 + radius}, {-0.125 + radius, 6, 0.5 + radius},
                {0.25 + radius, 7, -0.75 - radius}, {0.25 - radius, 7, -0.75 - radius},
                {-0.125 - radius, 6, 0.5 - radius}, {-0.125 + radius, 6, 0.5 - radius}
        };
        assertEquals(expected.length, captured.size());
        for (var index = 0; index < expected.length; index++) {
            var vertex = captured.get(index);
            assertEquals((float) expected[index][0], vertex.x());
            assertEquals((float) expected[index][1], vertex.y());
            assertEquals((float) expected[index][2], vertex.z());
            assertEquals(color, vertex.color());
        }
    }

    @Test
    void stabilizerPreservesGradientOnEveryFace() {
        var captured = new ArrayList<Vertex>();
        AMSRenderSupport.stabilizerBeam(buffer(captured), 0.12);
        var transparent = NTMBufferBuilder.packColor(0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 0F);
        var opaque = NTMBufferBuilder.packColor(0.408F - 0.175F, 0.686F - 0.175F, 0.686F - 0.175F, 1F);
        assertEquals(16, captured.size());
        for (var index = 0; index < captured.size(); index++) {
            var vertex = captured.get(index);
            var far = (index & 3) < 2;
            assertEquals(far ? 4F : 0F, vertex.x());
            assertEquals((float) 0.12, Math.abs(vertex.y()));
            assertEquals((float) 0.12, Math.abs(vertex.z()));
            assertEquals(far ? transparent : opaque, vertex.color());
        }
    }

    private static NTMBufferBuilder buffer(List<Vertex> captured) {
        return (NTMBufferBuilder) Proxy.newProxyInstance(NTMBufferBuilder.class.getClassLoader(),
                new Class<?>[]{NTMBufferBuilder.class}, (_, method, arguments) -> {
                    if (!method.getName().equals("appendPositionColor")) {
                        throw new AssertionError("Unexpected buffer operation " + method.getName());
                    }
                    captured.add(new Vertex((float) arguments[0], (float) arguments[1],
                            (float) arguments[2], (int) arguments[3]));
                    return null;
                });
    }

    private static double[] rotate(double[] matrix, int axis, double angle) {
        var cosine = Math.cos(angle);
        var sine = Math.sin(angle);
        var rotation = switch (axis) {
            case 0 -> new double[]{1, 0, 0, 0, cosine, -sine, 0, sine, cosine};
            case 1 -> new double[]{cosine, 0, sine, 0, 1, 0, -sine, 0, cosine};
            default -> new double[]{cosine, -sine, 0, sine, cosine, 0, 0, 0, 1};
        };
        var result = new double[9];
        for (var row = 0; row < 3; row++) {
            for (var column = 0; column < 3; column++) {
                for (var inner = 0; inner < 3; inner++) {
                    result[row * 3 + column] += matrix[row * 3 + inner] * rotation[inner * 3 + column];
                }
            }
        }
        return result;
    }

    private record Vertex(float x, float y, float z, int color) {}
}
