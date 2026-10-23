// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.particles;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

class ParticleQuadTest {
    @Test
    void emitsCameraBasisInWindingOrderWithTextureAndLightmap() {
        var buffer = new BufferBuilder(128);
        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        ParticleQuad.emit(buffer, 3, 5, 7, 1, 2, 3, 0.25F, -0.5F, 2,
                0.2F, 0.8F, 0.1F, 0.9F, 1, 0.5F, 0.25F, 0.75F, 0x00f00080);
        var expected = new float[][]{
                {0.5F, -1, 4, 0.8F, 0.9F},
                {1.5F, 11, 2, 0.8F, 0.1F},
                {5.5F, 11, 10, 0.2F, 0.1F},
                {4.5F, -1, 12, 0.2F, 0.9F}
        };
        var bytes = buffer.getByteBuffer().duplicate().order(ByteOrder.nativeOrder());
        assertEquals(4, buffer.getVertexCount());
        for (var vertex = 0; vertex < expected.length; vertex++) {
            var offset = vertex * 28;
            for (var component = 0; component < expected[vertex].length; component++)
                assertEquals(expected[vertex][component], bytes.getFloat(offset + component * 4));
            assertEquals(128, bytes.getShort(offset + 24) & 0xffff);
            assertEquals(240, bytes.getShort(offset + 26) & 0xffff);
        }
    }

    @Test
    void rotatesBillboardQuarterTurnWithoutChangingItsWinding() {
        var buffer = new BufferBuilder(128);
        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        var halfAngle = (float) Math.sqrt(0.5);
        ParticleQuad.emitRotated(buffer, 0, 0, 0, 1, 0, 1, 0, 0, 1,
                0, 1, 0, 1, 1, 1, 1, 1, 240, halfAngle, 0, 0, halfAngle);
        var expected = new float[][]{{1, -1}, {-1, -1}, {-1, 1}, {1, 1}};
        var bytes = buffer.getByteBuffer().duplicate().order(ByteOrder.nativeOrder());
        assertEquals(4, buffer.getVertexCount());
        for (var vertex = 0; vertex < expected.length; vertex++) {
            assertEquals(expected[vertex][0], bytes.getFloat(vertex * 28), 0.000001F);
            assertEquals(expected[vertex][1], bytes.getFloat(vertex * 28 + 4), 0.000001F);
            assertEquals(0, bytes.getFloat(vertex * 28 + 8));
        }
    }
}
