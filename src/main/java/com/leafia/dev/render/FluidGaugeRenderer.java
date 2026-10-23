// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.render;

import com.hbm.render.util.NTMBufferBuilder;
import com.hbm.render.util.NTMImmediate;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Bottom-anchored atlas tiles, with the last row and column cropped in texture space. */
@SideOnly(Side.CLIENT)
public final class FluidGaugeRenderer {
    private FluidGaugeRenderer() {}

    public static void draw(TextureAtlasSprite sprite, int left, int bottom, float z, int width, int height) {
        if (width <= 0 || height <= 0) return;
        Math.addExact(left, width);
        Math.subtractExact(bottom, height);
        var count = quadCount(width, height);
        var immediate = NTMImmediate.INSTANCE;
        var buffer = immediate.beginPositionTexQuads(count);
        append(buffer, sprite, left, bottom, z, width, height);
        immediate.draw();
    }

    static int quadCount(int width, int height) {
        var count = Math.multiplyExact((width - 1) / 16 + 1, (height - 1) / 16 + 1);
        Math.multiplyExact(count, 4 * 5 * Float.BYTES);
        return count;
    }

    static void append(NTMBufferBuilder buffer, TextureAtlasSprite sprite,
                       int left, int bottom, float z, int width, int height) {
        var right = Math.addExact(left, width);
        var top = Math.subtractExact(bottom, height);
        var u0 = sprite.getInterpolatedU(0);
        var u16 = sprite.getInterpolatedU(16);
        var v0 = sprite.getInterpolatedV(0);
        var v16 = sprite.getInterpolatedV(16);
        var lastU = sprite.getInterpolatedU(width & 15);
        var lastV = sprite.getInterpolatedV(16 - (height & 15));
        for (var rowBottom = bottom; rowBottom > top;) {
            var rowTop = rowBottom - Math.min(16, rowBottom - top);
            var rowV = rowBottom - rowTop == 16 ? v0 : lastV;
            for (var columnLeft = left; columnLeft < right;) {
                var columnRight = columnLeft + Math.min(16, right - columnLeft);
                var columnU = columnRight - columnLeft == 16 ? u16 : lastU;
                buffer.appendPositionTexQuadUnchecked(
                        columnLeft, rowBottom, z, u0, v16,
                        columnRight, rowBottom, z, columnU, v16,
                        columnRight, rowTop, z, columnU, rowV,
                        columnLeft, rowTop, z, u0, rowV);
                columnLeft = columnRight;
            }
            rowBottom = rowTop;
        }
    }
}
