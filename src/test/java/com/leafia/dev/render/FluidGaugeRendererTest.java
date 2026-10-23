// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.render;

import com.hbm.render.util.NTMBufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluidGaugeRendererTest {
    private static final TextureAtlasSprite SPRITE = new TextureAtlasSprite("test:gauge") {
        @Override public float getInterpolatedU(double u) { return (float) (0.25 + u / 64); }
        @Override public float getInterpolatedV(double v) { return (float) (0.5 + v / 64); }
    };

    @Test
    void coordinateLimitsCannotWrapTheUncheckedWriteLoop() {
        var quads = verticesAt(Integer.MAX_VALUE - 8, Integer.MIN_VALUE + 8, 8, 8);
        assertEquals(1, quads.size());
        assertArrayEquals(quad(Integer.MAX_VALUE - 8, Integer.MAX_VALUE,
                Integer.MIN_VALUE + 8, Integer.MIN_VALUE, .375F, .625F), quads.getFirst());
        assertThrows(ArithmeticException.class,
                () -> FluidGaugeRenderer.draw(SPRITE, Integer.MAX_VALUE, 0, 2, 1, 1));
        assertThrows(ArithmeticException.class,
                () -> FluidGaugeRenderer.draw(SPRITE, 0, Integer.MIN_VALUE, 2, 1, 1));
    }

    @Test
    void cropsLastRowAndColumnWithoutStretchingOrChangingTheBottomAnchor() {
        var quads = vertices(19, 21);
        assertEquals(4, quads.size());
        assertArrayEquals(quad(10, 26, 30, 14, 0.5F, 0.5F), quads.get(0));
        assertArrayEquals(quad(26, 29, 30, 14, 0.296875F, 0.5F), quads.get(1));
        assertArrayEquals(quad(10, 26, 14, 9, 0.5F, 0.671875F), quads.get(2));
        assertArrayEquals(quad(26, 29, 14, 9, 0.296875F, 0.671875F), quads.get(3));
    }

    @Test
    void rejectsDimensionsWhoseVertexReservationWouldOverflow() {
        assertEquals(4, FluidGaugeRenderer.quadCount(19, 21));
        assertThrows(ArithmeticException.class,
                () -> FluidGaugeRenderer.quadCount(16, Integer.MAX_VALUE));
        assertThrows(ArithmeticException.class,
                () -> FluidGaugeRenderer.quadCount(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Test
    void fullTilesUseTheWholeSpriteAndEmptyGaugesEmitNoVertices() {
        var quads = vertices(16, 32);
        assertEquals(2, quads.size());
        assertArrayEquals(quad(10, 26, 30, 14, 0.5F, 0.5F), quads.get(0));
        assertArrayEquals(quad(10, 26, 14, -2, 0.5F, 0.5F), quads.get(1));
        assertTrue(vertices(0, 32).isEmpty());
        assertTrue(vertices(16, 0).isEmpty());
    }

    private static float[] quad(int left, int right, int bottom, int top, float rightU, float topV) {
        return new float[]{left, bottom, 2, .25F, .75F,
                right, bottom, 2, rightU, .75F,
                right, top, 2, rightU, topV,
                left, top, 2, .25F, topV};
    }

    private static List<float[]> vertices(int width, int height) {
        return verticesAt(10, 30, width, height);
    }

    private static List<float[]> verticesAt(int left, int bottom, int width, int height) {
        var quads = new ArrayList<float[]>();
        var buffer = (NTMBufferBuilder) Proxy.newProxyInstance(NTMBufferBuilder.class.getClassLoader(),
                new Class<?>[]{NTMBufferBuilder.class}, (proxy, method, arguments) -> {
                    assertEquals("appendPositionTexQuadUnchecked", method.getName());
                    var quad = new float[arguments.length];
                    for (var i = 0; i < quad.length; i++) quad[i] = (Float) arguments[i];
                    quads.add(quad);
                    return null;
                });
        FluidGaugeRenderer.append(buffer, SPRITE, left, bottom, 2, width, height);
        return quads;
    }
}
