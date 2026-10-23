// SPDX-FileCopyrightText: 2025-2026 Leafia
// SPDX-FileCopyrightText: 2025-2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.custom_hbm.render.misc;

import com.hbm.render.util.NTMBufferBuilder;
import com.hbm.render.util.NTMImmediate;
import com.hbm.util.BobMathUtil;
import com.leafia.dev.render.AMSRenderSupport;
import com.leafia.dev.render.RenderState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/** Layered square-section beams, including sparse seeded random paths. */
public class LCEBeamPronter {
    private static final Random RANDOM = new Random();
    private static int[] colors = new int[16];
    private static float[] radii = new float[16];

    @Deprecated
    public static void prontBeam(Vec3d skeleton, EnumWaveType wave, EnumBeamType beam, int outerColor, int innerColor,
                                  int start, int segments, float spinRadius, int layers, float thickness) {
        prontBeam(true, skeleton, wave, beam, outerColor, innerColor, start, segments, spinRadius, layers, thickness);
    }

    public static void prontBeam(boolean shouldDisableTexture2D, Vec3d skeleton, EnumWaveType wave, EnumBeamType beam,
                                  int outerColor, int innerColor, int start, int segments, float spinRadius,
                                  int layers, float thickness) {
        try (var state = new RenderState()) {
            GlStateManager.pushMatrix();
            try {
                var yaw = (float) (Math.atan2(skeleton.x, skeleton.z) * 180F / Math.PI);
                var horizontal = MathHelper.sqrt(skeleton.x * skeleton.x + skeleton.z * skeleton.z);
                var pitch = (float) (Math.atan2(skeleton.y, horizontal) * 180F / Math.PI);
                GlStateManager.rotate(180F, 0, 1, 0);
                GlStateManager.rotate(yaw, 0, 1, 0);
                GlStateManager.rotate(pitch - 90, 1, 0, 0);
                if (shouldDisableTexture2D) GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.depthMask(beam != EnumBeamType.SOLID);
                if (beam == EnumBeamType.SOLID) {
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                    GlStateManager.disableCull();
                    prepareLayers(layers, thickness, innerColor, outerColor);
                }
                var lineColor = opaque(outerColor);
                var vertices = beam == EnumBeamType.LINE ? Math.max(segments, 0) * 2 + 2
                        : Math.max(segments, 0) * Math.max(layers, 0) * 16;
                var buffer = NTMImmediate.INSTANCE.beginPositionColor(beam == EnumBeamType.LINE ? GL11.GL_LINES : GL11.GL_QUADS, vertices);
                var length = skeleton.length();
                var step = length / segments;
                var previousX = 0D;
                var previousY = 0D;
                var previousZ = 0D;
                var angle = (float) Math.PI * start / 180F;
                var startCosine = MathHelper.cos(angle);
                var startSine = MathHelper.sin(angle);
                RANDOM.setSeed(start);
                for (var index = 0; index <= segments; index++) {
                    var x = 0D;
                    var y = step * index;
                    var z = 0D;
                    if (wave == EnumWaveType.SPIRAL) {
                        var turn = (float) Math.PI * 45F / 180F * index;
                        var cosine = MathHelper.cos(turn);
                        var sine = MathHelper.sin(turn);
                        var firstX = spinRadius * (double) startCosine;
                        var firstZ = -spinRadius * (double) startSine;
                        x = firstX * cosine + firstZ * sine;
                        z = firstZ * cosine - firstX * sine;
                    } else if (wave == EnumWaveType.RANDOM && index != 0 && index != segments) {
                        var radius = spinRadius * (double) RANDOM.nextFloat();
                        var turn = (float) (Math.PI * 2 * RANDOM.nextFloat());
                        x = radius * MathHelper.cos(turn);
                        z = -radius * MathHelper.sin(turn);
                        if (RANDOM.nextInt(3) == 0) continue;
                    }
                    if (index > 0 && beam == EnumBeamType.LINE) {
                        buffer.appendPositionColor((float) x, (float) y, (float) z, lineColor);
                        buffer.appendPositionColor((float) previousX, (float) previousY, (float) previousZ, lineColor);
                    } else if (index > 0 && beam == EnumBeamType.SOLID) {
                        for (var layer = 0; layer < layers; layer++) {
                            AMSRenderSupport.column(buffer, previousX, previousZ, x, z,
                                    previousY, y, radii[layer], colors[layer]);
                        }
                    }
                    previousX = x;
                    previousY = y;
                    previousZ = z;
                }
                if (beam == EnumBeamType.LINE) {
                    var centerColor = opaque(innerColor);
                    buffer.appendPositionColor(0, 0, 0, centerColor);
                    buffer.appendPositionColor(0, (float) length, 0, centerColor);
                }
                NTMImmediate.INSTANCE.draw();
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }

    private static void prepareLayers(int layers, float thickness, int innerColor, int outerColor) {
        if (layers > colors.length) {
            colors = new int[layers];
            radii = new float[layers];
        }
        var radius = thickness / layers;
        for (var index = 0; index < layers; index++) {
            var color = layers == 1 ? outerColor : BobMathUtil.interpolateColor(innerColor, outerColor, index / (float) (layers - 1));
            colors[index] = opaque(color);
            radii[index] = radius * (index + 1);
        }
    }

    private static int opaque(int color) {
        return NTMBufferBuilder.packColor(color >> 16 & 255, color >> 8 & 255, color & 255, 255);
    }

    public enum EnumWaveType { RANDOM, SPIRAL, STRAIGHT }
    public enum EnumBeamType { SOLID, LINE }
}
