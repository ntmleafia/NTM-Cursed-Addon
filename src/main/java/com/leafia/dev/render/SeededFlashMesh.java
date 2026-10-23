// SPDX-FileCopyrightText: 2026 movblock <admin@movblock.mov>
// SPDX-License-Identifier: LicenseRef-PolyForm-Shield-1.0.0

package com.leafia.dev.render;

import com.hbm.render.util.NTMBufferBuilder;
import com.hbm.render.util.NTMImmediate;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

/** Draws a three-sided burst as one batch, sharing fixed seeded orientations. */
public final class SeededFlashMesh {
    private static final double[] X = {-0.866, 0.866, 0};
    private static final double[] Z = {-0.5, -0.5, 1};
    private static final int[] EDGES = {0, 1, 1, 2, 2, 0};

    private SeededFlashMesh() {}

    public static void draw(float scale, double intensity, double alpha, float red, float green, float blue) {
        try (var state = new RenderState()) {
            GlStateManager.pushMatrix();
            try {
                GlStateManager.scale(0.2F, 0.2F, 0.2F);
                GlStateManager.disableLighting();
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                GlStateManager.disableAlpha();
                GlStateManager.enableCull();
                GlStateManager.depthMask(false);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
                var center = NTMBufferBuilder.packColor(1F, 1F, 1F, (float) alpha);
                var edge = NTMBufferBuilder.packColor(red, green, blue, 0F);
                var buffer = NTMImmediate.INSTANCE.beginPositionColor(GL11.GL_TRIANGLES, SeededFlashGeometry.COUNT * 9);
                var multiplier = (float) (intensity * scale);
                var rays = SeededFlashGeometry.RAYS;
                for (var ray = 0; ray < SeededFlashGeometry.COUNT; ray++) {
                    var offset = ray * SeededFlashGeometry.STRIDE;
                    var length = rays[offset + 9] * multiplier;
                    var width = rays[offset + 10] * multiplier;
                    for (var vertex = 0; vertex < EDGES.length; vertex++) {
                        if ((vertex & 1) == 0) buffer.appendPositionColor(0, 0, 0, center);
                        var corner = EDGES[vertex];
                        var x = X[corner] * width;
                        var z = Z[corner] * width;
                        buffer.appendPositionColor(
                                (float) (rays[offset] * x + rays[offset + 3] * length + rays[offset + 6] * z),
                                (float) (rays[offset + 1] * x + rays[offset + 4] * length + rays[offset + 7] * z),
                                (float) (rays[offset + 2] * x + rays[offset + 5] * length + rays[offset + 8] * z), edge);
                    }
                }
                NTMImmediate.INSTANCE.draw();
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }
}
